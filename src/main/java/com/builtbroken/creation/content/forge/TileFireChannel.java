package com.builtbroken.creation.content.forge;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
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
public class TileFireChannel extends TileElementChannel implements IFluidHandler, IWorldPosition
{
    //TODO add power drain when energy code is added
    //TODO add effect if power goes out, for example have the sphere degrade and turn into a solid clump of mass

    /** Number of buckets each meter of the sphere can contain, controlls volume of the sphere */
    public static int BUCKETS_PER_METER = 16;
    /** Conversion ratio of ingot to fluid volume, based on Tinkers *in theory* */
    public static int INGOT_VOLUME = 144;

    /** Bounding box for sphere, used to detect entity collisions */
    protected AxisAlignedBB fireBB;
    /** Data to based the size of the forge on */
    protected ForgeSize size;

    /** Fluid ID to Tank */
    protected HashMap<Integer, FluidTank> tanks = new HashMap();
    /** Current volume of stored fluids */
    protected int volume;

    /** How full the tank is between 0 - 1 */
    protected double percent_filled = 0;
    /** Radius of the sphere to render, based on percent filled */
    protected double current_radius = 0;
    /** Radius items orbit the sphere */
    protected double orbit_radius = 0;
    /** Amount an item can float away from orbit path, random limit */
    protected double orbit_float = 0;

    /** List of entities to attack each tick */
    protected List<EntityLivingBase> entities_to_damage = new ArrayList();
    /** List of items orbiting the sphere */
    protected List<MoltenOrbitData> orbiting_items = new ArrayList();

    public TileFireChannel()
    {
        super("fireChannel");
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        fireBB = size.axisAlignedBB(toPos());
        this.updateValues();
    }

    @Override
    public void update()
    {
        super.update();

        //Eat orbiting items
        if (ticks % 5 == 0)
        {
            //TODO create an item to molten metal list
            //TODO allow melting broken tools as a Math.max(.1 * ingotValue, (tool.getDamage / tool.getMaxDamage) * ingotValue);
        }

        //Search for entities to attack
        if (ticks % 3 == 0)
        {
            //TODO suck in items in a radius
            //TODO damage blocks in a larger radius if can burn
            if (current_radius >= 0.01)
            {
                //Find all entities to attack in a radius
                List list = world().getEntitiesWithinAABB(EntityLivingBase.class, fireBB);
                for (Object object : list)
                {
                    if (object instanceof EntityLivingBase && ((EntityLivingBase) object).getDistance(x(), y(), z()) <= current_radius)
                    {
                        if (!entities_to_damage.contains(object))
                            entities_to_damage.add((EntityLivingBase) object);

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

    /**
     * Adds an item to be inserted into the forge
     *
     * @param stack  - itemStack to be added
     * @param source - location the item started at
     */
    public void addItem(ItemStack stack, Pos source)
    {

    }

    protected void updateValues()
    {
        percent_filled = volume / size.volume;
        current_radius = percent_filled * size.r;
        //20% larger than radius TODO adjust to avoid visual collision
        orbit_radius = current_radius +  ( current_radius * .2);
    }

    @Override
    public Tile newTile()
    {
        return new TileFireChannel();
    }

    public boolean hasTankForFluid(FluidStack fluid)
    {
        return fluid != null && fluid.getFluid() != null ? hasTankForFluid(fluid.getFluid()) : false;
    }

    public boolean hasTankForFluid(Fluid fluid)
    {
        return fluid != null ? tanks.containsKey(fluid.getID()) : false;
    }

    public FluidTank getTankForFluid(Fluid fluid)
    {
        if(fluid != null)
        {
            if (!hasTankForFluid(fluid))
            {
                tanks.put(fluid.getID(), new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 100));
            }
            return tanks.get(fluid.getID());
        }
        return null;
    }


    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        return hasTankForFluid(resource) ? getTankForFluid(resource.getFluid()).fill(resource, doFill) : 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return hasTankForFluid(resource) ? getTankForFluid(resource.getFluid()).drain(resource.amount, doDrain) : null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        //Avoid using as it can easily iterate over all tanks if empty
        //TODO add clean up code to remove empty tanks from map
        if(maxDrain > 0)
        {
            for(FluidTank tank : tanks.values())
            {
                if(tank.getFluidAmount() > 0)
                {
                    return tank.drain(maxDrain, doDrain);
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

    /**
     * Helper to keep track of Forge size data
     */
    public enum ForgeSize
    {
        /** 1 */A,
        /** 2 */B,
        /** 3 */C,
        /** 4 */D,
        /** 5 */E,
        /** 6 */F,
        /** 7 */G,
        /** 8 */H,
        /** 9 */I;


        /** Radius of the sphere */
        public final int r;
        /** Max volume of the sphere */
        public final int volume;
        /** Damage inflicted to entities, final value is based on distance from center */
        public final float damage;

        /** Center offset of the sphere from tile */
        private final Pos center;
        /** Collision box size of the sphere */
        private final Cube collision_cube;

        ForgeSize()
        {
            int size = ordinal() + 1;
            this.r = size / 2;
            this.damage = r;
            this.volume = (int) ((((4 * Math.PI * r * r * r) / 3) * BUCKETS_PER_METER) * FluidContainerRegistry.BUCKET_VOLUME);
            this.center = new Pos(0, r, 0);
            this.collision_cube = new Cube(0, 0, 0, size, size, size).add(center);
        }

        /** Generates a new AxisAlignedBB to be used for entity detection */
        public AxisAlignedBB axisAlignedBB(IPos3D tile)
        {
            return collisionCube(tile).toAABB();
        }

        /** Generates a Cube from the collision box data */
        public Cube collisionCube(IPos3D tile)
        {
            return collision_cube.clone().add(tile);
        }
    }

    /**
     * Version of Orbit data that tracks how long the item has been melting
     */
    public class MoltenOrbitData extends OrbitData
    {
        //TODO have item degrade over time, slowly falling apart while glowing red

        /** Ticks that heat has been applied */
        public int heat_ticks = 0;

        public MoltenOrbitData(ItemStack stack, IWorldPosition center)
        {
            super(stack, center);
        }
    }
}
