package com.builtbroken.creation.content.glove.modes;

import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.handler.SelectionHandler;
import com.builtbroken.mc.imp.transform.region.Cube;
import com.builtbroken.mc.imp.transform.vector.Location;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Dark on 6/26/2015.
 */
public class GM_Selection extends GloveMode
{
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, Location location, int side, float hitX, float hitY, float hitZ)
    {
        Block block = location.getBlock();
        if (block != null && !location.isAirBlock())
        {
            handelSelection(stack, player, location);
            return true;
        }
        return false;
    }

    protected void handelSelection(ItemStack stack, EntityPlayer player, Location location)
    {
        if (!location.world().isRemote)
        {
            Cube select = SelectionHandler.getSelection(player);

            if (player.isSneaking())
            {
                select.setPointOne(location.toPos());
                if (Engine.runningAsDev)
                    player.addChatComponentMessage(new ChatComponentText(("Point One: " + select)));
            }
            else
            {
                select.setPointTwo(location.toPos());
                if (Engine.runningAsDev)
                    player.addChatComponentMessage(new ChatComponentText(("Point Two: " + select)));
            }
        }
    }
}
