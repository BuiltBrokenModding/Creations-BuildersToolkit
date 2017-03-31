package com.builtbroken.creation.content.glove.modes;

import com.builtbroken.mc.core.handler.SelectionHandler;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.lib.world.edit.Selection;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark on 6/26/2015.
 */
public class GM_Delete extends GloveMode
{
    protected int energy_cost_delete = 5;

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode || getEnergy(stack) >= energy_cost_delete * 5)
        {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int ticks_left)
    {
        if (ticks_left > 0)
        {
            Selection select = SelectionHandler.getSelection(player);

            if (select != null && select.isValid() && (player.capabilities.isCreativeMode || consumeEnergy(stack, energy_cost_delete * ticks_left, false)))
            {
                List<Pos> l = select.getLocationsWithin(new Location(player), 1, 50);
                if (ticks_left % 5 == 0)
                {
                    if (l != null && l.size() > 0 && l.get(0) != null)
                    {
                        Pos pos = l.get(0);
                        if (pos.distance(new Pos(player)) <= 50)
                        {
                            Block block = pos.getBlock(player.worldObj);
                            if (block != null && !pos.isAirBlock(player.worldObj))
                            {
                                int meta = pos.getBlockMetadata(player.worldObj);
                                if (!player.worldObj.isRemote)
                                {
                                    block.onBlockDestroyedByPlayer(player.worldObj, pos.xi(), pos.yi(), pos.zi(), meta);
                                    ArrayList<ItemStack> items = block.getDrops(player.worldObj, pos.xi(), pos.yi(), pos.zi(), meta, 0);
                                    pos.setBlockToAir(player.worldObj);
                                    if (!player.capabilities.isCreativeMode)
                                    {
                                        for (ItemStack s : items)
                                        {
                                            if (!player.inventory.addItemStackToInventory(s))
                                            {
                                                InventoryUtility.dropItemStack(new Location(player), s);
                                            }
                                        }
                                    }
                                }
                                else
                                {
                                    playBlockBreakAnimation(new Location(player.worldObj, pos.add(0.5)));
                                }
                            }
                        }
                        else
                        {
                            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("item.glove.info.delete.noblockdistance.name")));
                            player.stopUsingItem();
                        }
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("item.glove.info.delete.noblocks.name")));
                        player.stopUsingItem();
                    }
                }
            }
            else
            {
                if (!player.worldObj.isRemote)
                {
                    if (select == null)
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("item.glove.info.noselection.name")));
                    }
                    else if (!select.isValid())
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal("item.glove.info.invalidselection.name")));
                    }
                    player.stopUsingItem();
                }
            }
        }
        else
        {
            player.stopUsingItem();
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int ticks)
    {
        //Consume energy after stop using to prevent client side sync issues
        consumeEnergy(stack, energy_cost_delete * ticks, true);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.bow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 10000;
    }
}
