package com.builtbroken.creation.content;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.selection.Selection;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.IPacketReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Location;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by robert on 1/25/2015.
 */
public class ItemGlove extends Item implements IPacketIDReceiver
{
    public static final int MODE_PACKET = 0;

    protected int max_mode = 1;
    protected int min_mode = 0;

    public ItemGlove()
    {
        this.setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (world != null && player != null)
        {
            Location location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block != null && !location.isAirBlock())
            {
                if (!world.isRemote)
                {
                    switch (stack.getItemDamage())
                    {
                        case 0:
                            handelSelection(stack, player, location);
                            break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the selection of two points
     */
    public void handelSelection(ItemStack stack, EntityPlayer player, Location location)
    {
        Selection select = SelectionHandler.getSelection(player.getUniqueID());

        if (player.isSneaking())
        {
            select.setPointOne(location.toVector3());
            if (Creation.isDevEnv())
                player.addChatComponentMessage(new ChatComponentText(("Point One: " + select)));
        }
        else
        {
            select.setPointTwo(location.toVector3());
            if (Creation.isDevEnv())
                player.addChatComponentMessage(new ChatComponentText(("Point Two: " + select)));
        }
    }

    @SubscribeEvent
    public void mouseHandler(MouseEvent e)
    {
        if (e.dwheel != 0)
        {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player.isSneaking())
            {
                ItemStack stack = player.getCurrentEquippedItem();
                if(stack != null && stack.getItem() == this)
                {
                    cycleMode(stack, player, e.dwheel / 120);
                    e.setCanceled(true);
                }
            }
        }
    }

    /**
     * Causes the mode to cycle
     * @param stack - itemstack
     * @param player - player
     * @param delta - change in mode
     */
    protected void cycleMode(ItemStack stack, EntityPlayer player, int delta)
    {
        int n = getMode(stack) + delta;
        if(n > max_mode)
            n = min_mode;
        if(n < min_mode)
            n = max_mode;

        if(getMode(stack) != n)
        {
            setMode(stack, n);
            Engine.instance.packetHandler.sendToServer(new PacketPlayerItem(player, MODE_PACKET, n));
        }
    }

    protected int getMode(ItemStack stack)
    {
        if(stack.getTagCompound() != null)
        {
            return stack.getTagCompound().getInteger("mode");
        }
        return min_mode;
    }

    protected void setMode(ItemStack stack, int mode)
    {
        if(stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("mode", mode);
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType packet)
    {
        System.out.println("Packet: " + packet +"   ID: " + id);
        if(packet instanceof PacketPlayerItem)
        {
            if(id == MODE_PACKET)
            {
                int slot = ((PacketPlayerItem) packet).slotId;
                ItemStack stack = player.inventory.getStackInSlot(slot);
                setMode(stack, buf.readInt());
                player.addChatComponentMessage(new ChatComponentText("Mode set to " + getMode(stack)));
                return true;
            }
        }
        return false;
    }
}
