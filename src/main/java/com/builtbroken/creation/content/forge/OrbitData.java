package com.builtbroken.creation.content.forge;

import com.builtbroken.jlib.data.vector.IPos3D;
import net.minecraft.item.ItemStack;

/**
 * Used to track ItemStacks orbiting the something
 */
public class OrbitData
{
    public ItemStack stack;

    public IPos3D center;
    public float radius;
    public float l;

    public float desired_radius;
    public float desired_l;

    public float heatLevel;

    public OrbitData(ItemStack stack)
    {
        this.stack = stack;
    }

    public void update()
    {

    }

    protected void update2D_path()
    {

    }
}