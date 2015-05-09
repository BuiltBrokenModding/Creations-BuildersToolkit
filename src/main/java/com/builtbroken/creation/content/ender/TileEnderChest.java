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

    private EnderInventory client_inv;
    private static final EnderInventory empty_inv = new EnderInventory(0);

    public TileEnderChest()
    {
        super("enderChest", Material.rock);
    }

    @Override
    public EnderInventory getInventory()
    {
        if(isClient())
        {
            if(client_inv == null)
                client_inv = new EnderInventory(27);
            return client_inv;
        }
        else
        {
            ChestStorageMap map = ChestMapLoader.chestSets.get(inventory_set);
            if(map != null)
                return map.getInventory(inventory_id);
        }
        return null;
    }
}
