package com.builtbroken.creation.content.ender;

import com.builtbroken.mc.prefab.tile.TileModuleMachine;
import com.builtbroken.mc.prefab.tile.module.TileModuleInventory;
import net.minecraft.block.material.Material;

import java.util.HashMap;

/**
 * Created by robert on 5/1/2015.
 */
public class TileEnderChest extends TileModuleMachine
{
    protected String inventory_set = "global";
    protected short inventory_id = 0;

    public TileEnderChest()
    {
        super("enderChest", Material.rock);
    }

    @Override
    public TileModuleInventory getInventory()
    {
        return null;
    }
}
