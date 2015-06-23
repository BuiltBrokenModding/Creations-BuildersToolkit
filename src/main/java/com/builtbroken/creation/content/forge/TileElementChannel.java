package com.builtbroken.creation.content.forge;

import com.builtbroken.mc.prefab.tile.Tile;
import net.minecraft.block.material.Material;

/**
 * Created by Dark on 6/9/2015.
 */
public abstract class TileElementChannel extends Tile
{
    public TileElementChannel(String name)
    {
        super(name, Material.rock);
    }
}
