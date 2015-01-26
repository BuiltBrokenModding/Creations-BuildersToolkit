package com.builtbroken.creation.content;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.selection.Selection;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by robert on 1/25/2015.
 */
public class ItemGlove extends Item
{
    protected int mode = 0;

    public ItemGlove()
    {
        this.setMaxStackSize(1);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(world != null && player != null)
        {
            Location location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block != null && !location.isAirBlock())
            {
                if (!world.isRemote)
                {
                    switch (stack.getItemDamage())
                    {
                        case 0: handelSelection(stack, player, location); break;
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

        if(player.isSneaking())
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
}
