package com.builtbroken.creation.content.forge;

import com.builtbroken.creation.Creation;
import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Helper to keep track of Forge size data
 */
public enum ForgeSize
{
    /** 1 */A(1),
    /** 2 */B(2),
    /** 3 */C(3),
    /** 4 */D(4),
    /** 5 */E(5),
    /** 6 */F(6),
    /** 7 */G(7),
    /** 8 */H(8),
    /** 9 */I(9);

    /** Radius of the sphere */
    public final float r;
    /** Max volume of the sphere */
    public final int volume;
    /** Damage inflicted to entities, final value is based on distance from center */
    public final float damage;

    /** Center offset of the sphere from tile */
    private final Pos center;
    /** Collision box size of the sphere */
    private final Cube collision_cube;

    ForgeSize(float size)
    {
        this.r = size / 2f;
        this.damage = r;
        this.volume = (int) ((((4 * Math.PI * r * r * r) / 3) * Creation.FORGE_BUCKETS_PER_METER) * FluidContainerRegistry.BUCKET_VOLUME);
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