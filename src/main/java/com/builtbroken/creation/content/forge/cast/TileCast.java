package com.builtbroken.creation.content.forge.cast;

import com.builtbroken.mc.api.items.ICastItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * Yes it works the same as Tinker's casting table, No it's not a clone/copy/etc
 * <p/>
 * Basic casting block used to mold parts from molten fluids. Requires an item stack
 * as the cast which implement ICastItem on it's item
 * Created by Dark on 6/23/2015.
 */
public class TileCast extends Tile implements IFluidHandler, IPacketIDReceiver
{
    /** Time in ticks it takes items to cool off */
    public static int COOLING_TIME = 10 /* Seconds */ * 20 /* Ticks */;

    private FluidTank tank;
    /** Item that is the cast */
    protected ItemStack cast_stack;
    protected ItemStack output_stack;

    /** Current ticks, counting up, for the item to cool off */
    protected int cooling_ticks = 0;
    protected int prev_volume = 0;


    public TileCast()
    {
        super("moltenFluidCast", Material.rock);
    }

    @Override
    public void update()
    {
        super.update();
        if (cast_stack != null && cast_stack.getItem() instanceof ICastItem && output_stack == null)
        {
            if (ticks % 3 == 0)
            {
                if (getTank() == null)
                {
                    if (prev_volume != 0)
                    {
                        Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 3, false), this);
                        prev_volume = 0;
                    }
                }
                else if (Math.abs(getTank().getFluidAmount() - prev_volume) > (getTank().getCapacity() * .05))
                {
                    Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 3, true, getTank().getFluid()), this);
                    prev_volume = getTank().getFluidAmount();
                }
            }
            if (ticks % 5 == 0 && getTank().getFluidAmount() >= ((ICastItem) cast_stack.getItem()).getFluidCapacity(cast_stack))
            {
                if (cooling_ticks <= COOLING_TIME)
                {
                    cooling_ticks += 5;
                }
                else
                {
                    output_stack = ((ICastItem) cast_stack.getItem()).doCast(cast_stack, getTank().getFluid(), this);
                    getTank().setFluid(null);
                    Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 2, output_stack != null, output_stack != null ? output_stack : new ItemStack(Blocks.air)), this);
                }
            }
        }
    }

    protected void setCast(ItemStack stack)
    {
        this.cast_stack = stack != null ? stack.copy() : stack;
        if (cast_stack == null)
            clearCast();
        else if (cast_stack.getItem() instanceof ICastItem)
            tank = new FluidTank(((ICastItem) cast_stack.getItem()).getFluidCapacity(cast_stack));

        if (isServer())
            Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 1, cast_stack != null, cast_stack != null ? cast_stack : new ItemStack(Blocks.air)), this);
    }

    /** Tank storing the fluid */
    public FluidTank getTank()
    {
        if (tank == null && cast_stack != null && cast_stack.getItem() instanceof ICastItem)
        {
            tank = new FluidTank(((ICastItem) cast_stack.getItem()).getFluidCapacity(cast_stack));
            prev_volume = 0;
        }
        return tank;
    }

    protected void clearCast()
    {
        tank = null;
        cooling_ticks = 0;
    }


    @Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (player.getHeldItem() == null)
        {
            if (isClient())
                return true;
            if (output_stack != null)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, output_stack);
                output_stack = null;
                player.inventoryContainer.detectAndSendChanges();
                Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 2, output_stack != null, output_stack != null ? output_stack : new ItemStack(Blocks.air)), this);
            }
            if (cast_stack != null)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, cast_stack);
                setCast(null);
                player.inventoryContainer.detectAndSendChanges();
            }
        }
        else if (player.getHeldItem().getItem() instanceof ICastItem)
        {
            if (isClient())
                return true;
            if (cast_stack == null)
            {
                if (player.getItemInUse().stackSize > 1)
                {
                    setCast(player.getItemInUse().copy());
                    cast_stack.stackSize = 1;
                    player.getItemInUse().stackSize--;
                }
                else
                {
                    setCast(player.getItemInUse());
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
        }
        else if (player.getHeldItem().getItem() instanceof ItemBucket)
        {
            //TODO add support for quick cooling
            //TODO add hook to ICast for changing output cast for quick cooling
        }
        return false;
    }

    @Override
    public Tile newTile()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return new TileCastClient();
        else
            return new TileCast();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (getTank() != null && resource != null && output_stack == null)
        {
            int fill = getTank().fill(resource, doFill);
            if (doFill && cast_stack != null && cast_stack.getItem() instanceof ICastItem)
            {
                setCast(((ICastItem) cast_stack.getItem()).onFluidAdded(cast_stack, resource.getFluid(), fill, getTank().getFluidAmount() + fill));
            }
            return fill;
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return getTank() != null && resource != null && getTank().getFluid() != null && getTank().getFluid().getFluidID() == resource.getFluidID() ? getTank().drain(resource.amount, doDrain) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return getTank() != null ? getTank().drain(maxDrain, doDrain) : null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return cast_stack != null && cast_stack.getItem() instanceof ICastItem && ((ICastItem) cast_stack.getItem()).allowFluid(cast_stack, fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return getTank() != null;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        if (getTank() != null)
        {
            FluidTankInfo[] info = new FluidTankInfo[1];
            info[0] = getTank().getInfo();
            return info;
        }
        return new FluidTankInfo[0];
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, cast_stack != null, output_stack != null, cast_stack != null ? output_stack : new ItemStack(Blocks.air), output_stack != null ? output_stack : new ItemStack(Blocks.air));
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            if (id == 0)
            {
                boolean c = buf.readBoolean();
                boolean o = buf.readBoolean();
                if (c)
                {
                    setCast(ByteBufUtils.readItemStack(buf));
                    if (o)
                    {
                        output_stack = ByteBufUtils.readItemStack(buf);
                    }
                    else
                    {
                        output_stack = null;
                    }
                }
                else
                {
                    setCast(null);
                    if (o)
                    {
                        output_stack = ByteBufUtils.readItemStack(buf);
                    }
                    else
                    {
                        output_stack = null;
                    }
                }
                return true;
            }
            else if (id == 1)
            {
                if (buf.readBoolean())
                {
                    setCast(ByteBufUtils.readItemStack(buf));
                }
                else
                {
                    setCast(null);
                }
                return true;
            }
            else if (id == 2)
            {
                if (buf.readBoolean())
                {
                    output_stack = ByteBufUtils.readItemStack(buf);
                }
                else
                {
                    output_stack = null;
                }
                return true;
            }
            else if (id == 3)
            {
                if (getTank() != null && buf.readBoolean())
                {
                    getTank().setFluid(FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf)));
                }
                return true;
            }
        }
        return false;
    }
}
