package com.builtbroken.creation.content.forge;

import com.builtbroken.creation.Creation;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.PlacementData;
import com.builtbroken.mc.lib.world.heat.HeatedBlockRegistry;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Spirit magic implementation of a fluid furnace, converts any item into its molten version.
 * <p/>
 * Will incinerate any item it can't convert or throw out based on map data.
 * <p/>
 * Design Doc:
 * https://docs.google.com/document/d/1Sbcma7PVv_MNtIqQh_qn7cPZRwP8SUsYz0sqaV_rKB0/edit?usp=sharing
 * <p/>
 * <p/>
 * Created by Dark on 6/9/2015.
 */
public class TileFireChannel extends TileElementChannel implements IFluidHandler, IWorldPosition, IPacketIDReceiver
{
    //TODO add power drain when energy code is added
    //TODO add effect if power goes out, for example have the sphere degrade and turn into a solid clump of mass

    /** Limit of items that can orbit the forge */
    public static int MAX_STORED_ITEMS = 20;

    /** Current volume of stored fluids */
    protected int volume;
    private int prev_volume;
    /** How full the tank is between 0 - 1 */
    protected float percent_filled = 0;
    /** Radius of the sphere to render, based on percent filled */
    protected float current_radius = 0;

    /** Bounding box for sphere, used to detect entity collisions */
    protected AxisAlignedBB collisionAABB;
    protected Cube collisionCube;
    /** Data to based the size of the forge on */
    protected ForgeSize size = ForgeSize.C;

    /** Fluid ID to Tank */
    protected HashMap<String, FluidTank> tanks = new HashMap();
    /** List of entities to attack each tick */
    protected List<EntityLivingBase> entities_to_damage = new ArrayList();
    protected List<SmeltStack> smelting_items = new ArrayList();


    /** Center of sphere, used to set the point of orbit */
    protected IWorldPosition sphere_center;
    protected float sphere_y_delta = 0;

    public TileFireChannel()
    {
        super("fireChannel");
        this.itemBlock = ItemBlockMetadata.class;
        this.bounds = new Cube(0, 0, 0, 1, .7, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;

        //Ensures that feed back from the var always matches the correct data
        sphere_center = new IWorldPosition()
        {
            @Override
            public World world()
            {
                return TileFireChannel.this.getWorldObj();
            }

            @Override
            public double x()
            {
                return TileFireChannel.this.x() + 0.5;
            }

            @Override
            public double y()
            {
                return TileFireChannel.this.y() + 0.5 + (size != null ? size.r : 3) + TileFireChannel.this.sphere_y_delta;
            }

            @Override
            public double z()
            {
                return TileFireChannel.this.z() + 0.5;
            }
        };
    }

    @Override
    public Tile newTile()
    {
        if (FMLClientHandler.instance().getSide() == Side.CLIENT)
            return new TileFireChannelClient();
        else
            return new TileFireChannel();
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        if (sphere_center == null)
        {
            sphere_center = new IWorldPosition()
            {
                @Override
                public World world()
                {
                    return TileFireChannel.this.getWorldObj();
                }

                @Override
                public double x()
                {
                    return TileFireChannel.this.x() + 0.5;
                }

                @Override
                public double y()
                {
                    return TileFireChannel.this.y() + 0.5 + (size != null ? size.r : 3) + TileFireChannel.this.sphere_y_delta;
                }

                @Override
                public double z()
                {
                    return TileFireChannel.this.z() + 0.5;
                }
            };
        }
        if (size == null)
            size = ForgeSize.C;
        collisionAABB = size.axisAlignedBB(toPos());
        collisionCube = size.collisionCube(toPos());
        this.updateValues();
    }

    @Override
    public void update()
    {
        super.update();
        if (isServer())
        {
            //Eat orbiting items
            if (ticks % 5 == 0)
            {
                //TODO create an item to molten metal list
                //TODO allow melting broken tools as a Math.max(.1 * ingotValue, (tool.getDamage / tool.getMaxDamage) * ingotValue);
                //TODO add handling for non-metal parts, for example turn sticks into char pile & tool handles into char
                List<SmeltStack> remove_list = new ArrayList();
                for (SmeltStack stack : smelting_items)
                {
                    //TODO maybe turn the item into a ball of fluid. This way the player knows the item is cooked but the system is full.
                    if (stack.ticks >= 40)
                    {
                        if (MachineRecipeType.FLUID_SMELTER.getHandler() != null && volume < (size.volume - Engine.INGOT_VOLUME))
                        {
                            Object out = MachineRecipeType.FLUID_SMELTER.getRecipe(0, 0, stack.stack);
                            if (out instanceof FluidStack)
                            {
                                FluidStack fluidStack = (FluidStack) out;
                                if (fill(ForgeDirection.UNKNOWN, fluidStack, false) >= fluidStack.amount)
                                {
                                    fill(ForgeDirection.UNKNOWN, fluidStack, true);
                                    stack.stack.stackSize--;
                                    if (stack.stack.stackSize <= 0) //TODO test
                                        remove_list.add(stack);
                                }
                            }
                        }
                    }
                    else
                    {
                        stack.ticks++;
                    }
                }

                for (SmeltStack stack : remove_list)
                {
                    remove(stack);
                }
            }


            if (ticks % 3 == 0)
            {
                //Delay on volume packet updates to prevent spam
                if (Math.abs(volume - prev_volume) > 500)
                {
                    Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 3, volume), this);
                    prev_volume = volume;
                }

                //TODO degrade sphere if collision is

                //Detect for collision with blocks, and damage blocks with heat effects
                boolean collision = false;
                for (int y = collisionCube.min().yi(); y < collisionCube.max().yi(); y++)
                {
                    for (int x = collisionCube.min().xi(); x < collisionCube.max().xi(); x++)
                    {
                        for (int z = collisionCube.min().zi(); z < collisionCube.max().zi(); z++)
                        {
                            Location loc = new Location(world(), x, y, z);
                            double d = loc.distance(sphere_center.x(), sphere_center.y(), sphere_center.z()) - 0.5;//half a block;
                            if (!loc.isAirBlock() && d <= current_radius)
                            {
                                collision = true;
                                //TODO replace with heat map, that is when heat map is finished
                                PlacementData data = HeatedBlockRegistry.getResultWarmUp(loc.getBlock(), (int) (((current_radius - d) / current_radius) * 1500 + 500));
                                if (data != null && data.block() != null)
                                {
                                    loc.setBlock(data.block(), data.meta() == -1 ? 0 : data.meta());
                                }
                            }
                        }
                    }
                }

                //Search for entities to attack
                //TODO damage blocks in a larger radius if can burn
                //Find all entities to attack in a radius
                List list = world().getEntitiesWithinAABB(Entity.class, collisionAABB);
                for (Object object : list)
                {
                    if (object instanceof Entity && ((Entity) object).isEntityAlive())
                    {
                        double d = ((Entity) object).getDistance(x(), y(), z());
                        if (object instanceof EntityLivingBase && d <= current_radius)
                        {
                            if (!entities_to_damage.contains(object))
                                entities_to_damage.add((EntityLivingBase) object);

                        }
                        else if (object instanceof EntityItem)
                        {
                            if (smelting_items.size() < MAX_STORED_ITEMS)
                            {
                                Object o = MachineRecipeType.FLUID_SMELTER.getRecipe(0, 0, ((EntityItem) object).getEntityItem());
                                if (o instanceof FluidStack && ((FluidStack) o).amount + volume <= size.volume)
                                {
                                    //TODO only grab smelt-able items, destroy the rest with fire

                                    addItem(((EntityItem) object).getEntityItem(), new Pos((Entity) object));
                                    ((Entity) object).setDead();
                                }
                            }
                        }
                    }
                }
            }

            //Tick damage to all entities within range
            if (ticks % 2 == 0)
            {
                Iterator<EntityLivingBase> it = entities_to_damage.iterator();
                while (it.hasNext())
                {
                    EntityLivingBase entity = it.next();
                    double distance = entity.getDistance(x(), y(), z());

                    //Limit to orb size, things that are alive, and ignore creative players
                    if (distance <= current_radius && entity.isEntityAlive() && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode))
                    {
                        //If in water the effect is pointless :p
                        if (!entity.isInWater())
                        {
                            //Chance to set fire
                            if (!entity.isImmuneToFire() && worldObj.rand.nextFloat() >= 0.7f)
                            {
                                entity.setFire(2);
                            }
                            //Deal damage based on distance & size of sphere TODO make the damage curved due to center being hotter than edge non-linear
                            double percent = (current_radius - distance) / current_radius;
                            entity.attackEntityFrom(DamageSource.lava, (float) (percent * size.damage));
                        }
                    }
                    else
                    {
                        it.remove();
                    }
                }
            }
        }
    }

    /**
     * Adds an item to be inserted into the forge
     *
     * @param stack  - itemStack to be added
     * @param source - location the item started at
     */
    public void addItem(ItemStack stack, Pos source)
    {
        //TODO maybe extend based on size of sphere
        if (smelting_items.size() < MAX_STORED_ITEMS)
        {
            smelting_items.add(new SmeltStack(stack));
        }
        if (isServer())
        {
            Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 1, stack, source), this);
        }
    }

    protected void remove(SmeltStack stack)
    {
        if (stack != null && stack.stack != null)
        {
            this.smelting_items.remove(stack);
            if (isServer())
            {
                Engine.instance.packetHandler.sendToAllAround(new PacketTile(this, 2, stack.stack), this);
            }
        }
        else if (Engine.runningAsDev)
        {
            Creation.INSTANCE.logger().error("Something tried to remove an item with an empty stack", new RuntimeException());
        }
    }

    protected void remove(ItemStack stack)
    {
        if (stack != null)
        {
            //Technically this removes the first matching item rather than the exact
            Iterator<SmeltStack> it = smelting_items.iterator();
            while (it.hasNext())
            {
                SmeltStack smelt_stack = it.next();
                if (smelt_stack.stack == null)
                    it.remove();
                else if (ItemStack.areItemStacksEqual(smelt_stack.stack, stack))
                {
                    it.remove();
                    break;
                }
            }
        }
        else if (Engine.runningAsDev)
        {
            Creation.INSTANCE.logger().error("Something tried to remove an item with an empty stack", new RuntimeException());
        }
    }

    protected void updateValues()
    {
        percent_filled = Math.min(.01f, (volume / size.volume));
        current_radius = percent_filled * size.r;
    }

    public boolean hasTankForFluid(FluidStack fluid)
    {
        return fluid != null && fluid.getFluid() != null ? hasTankForFluid(fluid.getFluid()) : false;
    }

    public boolean hasTankForFluid(Fluid fluid)
    {
        return fluid != null ? tanks.containsKey(fluid.getName()) : false;
    }

    public FluidTank getTankForFluid(Fluid fluid)
    {
        if (fluid != null)
        {
            if (!hasTankForFluid(fluid))
            {
                tanks.put(fluid.getName(), new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 100));
            }
            return tanks.get(fluid.getName());
        }
        return null;
    }

    protected void addVolume(int v)
    {
        volume += v;
        updateValues();
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        int fill = getTankForFluid(resource.getFluid()).fill(resource, doFill);
        if (doFill)
            addVolume(fill);
        return fill;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (hasTankForFluid(resource))
        {
            FluidStack stack = getTankForFluid(resource.getFluid()).drain(resource.amount, doDrain);
            if (doDrain)
                addVolume(-stack.amount);
            return stack;
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        //Avoid using as it can easily iterate over all tanks if empty
        //TODO add clean up code to remove empty tanks from map
        if (maxDrain > 0)
        {
            for (FluidTank tank : tanks.values())
            {
                if (tank.getFluidAmount() > 0)
                {
                    FluidStack stack = tank.drain(maxDrain, doDrain);
                    if (doDrain)
                        addVolume(-stack.amount);
                    return stack;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        //TODO allow pipe connections if upgraded with pipe fittings
        //TODO if filled from external source play filling animation
        //Animation should show fluid flowing from the base into the ball
        return from == ForgeDirection.UNKNOWN;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        //TODO allow pipe connections if upgraded with pipe fittings
        //TODO if drained from external source play filling animation
        //Animation should show fluid flowing from the ball to the base
        return from == ForgeDirection.UNKNOWN;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        //TODO generate and cache each time the tank map changes
        return new FluidTankInfo[0];
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        if (isClient())
        {
            //Sync all packet
            if (id == 0)
            {

                size = ForgeSize.values()[buf.readInt()];
                volume = buf.readInt();

                int s = buf.readInt();
                smelting_items.clear();
                for (int i = 0; i < s; i++)
                {
                    smelting_items.add(new SmeltStack(buf));
                }
                updateValues();
                //TODO update client tile's data as well
                return true;
            }
            //Add item packet
            else if (id == 1)
            {
                addItem(ByteBufUtils.readItemStack(buf), new Pos(buf));
                return true;
            }
            else if (id == 2)
            {
                ItemStack stack = ByteBufUtils.readItemStack(buf);
                if (stack.stackSize == 0)
                    stack.stackSize = 1;
                remove(stack);
                return true;
            }
            else if (id == 3)
            {
                this.volume = buf.readInt();
                updateValues();
                return true;
            }
        }
        return false;
    }

    @Override
    public PacketTile getDescPacket()
    {
        PacketTile packet = new PacketTile(this, 0);
        ByteBuf buf = packet.data();
        buf.writeInt(size.ordinal());
        buf.writeInt(volume);
        buf.writeInt(smelting_items.size());
        for (SmeltStack stack : smelting_items)
        {
            stack.writeBytes(buf);
        }
        return packet;
    }
}
