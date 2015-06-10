package com.builtbroken.creation.content.forge;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * https://docs.google.com/document/d/1Sbcma7PVv_MNtIqQh_qn7cPZRwP8SUsYz0sqaV_rKB0/edit?usp=sharing
 * Created by Dark on 6/9/2015.
 */
public class TileFireChannel extends TileElementChannel implements IFluidHandler
{
    public static int BUCKETS_PER_METER = 16;

    //Orb size data
    protected AxisAlignedBB fireBB;
    protected ForgeSize size;

    //Tank related info
    protected HashMap<Integer, FluidTank> tanks = new HashMap();
    protected int volume;

    //Orb size, based on tank volume
    protected double percent_filled = 0;
    protected double current_radius = 0;

    protected List<EntityLivingBase> entities_to_damage = new ArrayList();

    public TileFireChannel()
    {
        super("fireChannel");
    }

    @Override
    public void firstTick()
    {
        super.firstTick();
        fireBB = size.axisAlignedBB(toPos());
    }

    @Override
    public void update()
    {
        super.update();
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

                //Limit to orb
                if (distance <= current_radius && !entity.isDead && (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode))
                {
                    double percent = (current_radius - distance) / current_radius;
                    entity.attackEntityFrom(DamageSource.lava, (float) (percent * size.damage));
                }
                else
                {
                    it.remove();
                }
            }
        }
    }

    protected void updateValues()
    {
        percent_filled = volume / size.volume;
        current_radius = percent_filled * size.r;
    }

    @Override
    public Tile newTile()
    {
        return new TileFireChannel();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (from == ForgeDirection.UNKNOWN && resource != null && resource.getFluid() != null)
        {
            if (!tanks.containsKey(resource.getFluidID()))
            {
                tanks.put(resource.getFluidID(), new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 100));
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return from == ForgeDirection.UNKNOWN;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return from == ForgeDirection.UNKNOWN;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[0];
    }

    //helper to keep track of the size and bounds that goes with it
    public enum ForgeSize
    {
        ONE(1),
        THREE(3),
        FIVE(5),
        SEVEN(7),
        NINE(9);

        public final int size;
        public final int r;
        public final int volume;
        public final float damage;
        private final Pos center;
        private final Cube cube;

        ForgeSize(int size)
        {
            this.size = size;
            this.r = size / 2;
            this.damage = r;
            this.volume = (int) ((((4 * Math.PI * r * r * r) / 3) * BUCKETS_PER_METER) * FluidContainerRegistry.BUCKET_VOLUME);
            this.center = new Pos(0, r, 0);
            this.cube = new Cube(0, 0, 0, size, size, size).add(center);
        }

        public AxisAlignedBB axisAlignedBB(IPos3D tile)
        {
            return cube.clone().add(tile).toAABB();
        }
    }
}
