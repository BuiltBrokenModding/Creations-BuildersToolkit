package com.builtbroken.creation.content.glove.modes;

import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * Created by Dark on 6/26/2015.
 */
public class GM_Harvest extends GloveMode
{
    protected int energy_cost_delete = 5;

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, Location location, int side, float hitX, float hitY, float hitZ)
    {
        Block block = location.getBlock();
        if (block != null && location.getHardness() >= 0)
        {
            if (player.capabilities.isCreativeMode || consumeEnergy(stack, energy_cost_delete, false))
            {
                if (!player.worldObj.isRemote)
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        consumeEnergy(stack, energy_cost_delete, true);
                        block.onBlockDestroyedByPlayer(player.worldObj, location.xi(), location.yi(), location.zi(), location.getBlockMetadata());
                        ArrayList<ItemStack> items = block.getDrops(player.worldObj, location.xi(), location.yi(), location.zi(), location.getBlockMetadata(), 0);

                        for (ItemStack s : items)
                        {
                            if (!player.inventory.addItemStackToInventory(s))
                            {
                                InventoryUtility.dropItemStack(new Location(player), s);
                            }
                        }
                    }
                    location.setBlockToAir();
                }
                else
                {
                    playBlockBreakAnimation(location.add(0.5));
                }
            }
        }
        return true;
    }

}
