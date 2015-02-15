package com.builtbroken.creation.content;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.selection.Selection;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.api.items.IModeItem;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.inventory.InventoryUtility;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by robert on 1/25/2015.
 */
public class ItemGlove extends Item implements IModeItem.IModeScrollItem, IPostInit
{
    protected int max_mode = 3;
    protected int max_energy = 10000;
    protected int energy_cost_delete = 5;

    public ItemGlove()
    {
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setTextureName(Creation.PREFIX + "glove");
        this.setUnlocalizedName(Creation.PREFIX + "glove");
    }

    @Override
    public void onPostInit()
    {
        GameRegistry.addShapedRecipe(new ItemStack(Creation.glove), "lGl", "geg", "lll", 'l', Items.leather, 'e', Items.ender_pearl, 'g', Items.gold_nugget, 'G', Blocks.glass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        //TODO translate based on mode type
        lines.add(LanguageUtility.getLocal(getUnlocalizedName() + ".info.name"));
        lines.add(LanguageUtility.getLocal(getUnlocalizedName() + ".mode.name") + ": " + LanguageUtility.getLocal(getUnlocalizedName() + ".mode." + getMode(stack) + ".name"));
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getItemInUse() != stack)
        {
            if (getEnergy(stack) < max_energy)
            {
                //TODO add different types of regen based on upgrades and location
                this.setEnergy(stack, Math.min(max_energy, getEnergy(stack) + 10));
            }
        }
    }


    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        int mode = getMode(stack);
        if (mode == GloveModes.DELETE.ordinal())
        {
            if (player.capabilities.isCreativeMode || getEnergy(stack) >= energy_cost_delete * 5)
            {
                player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
            }
        }
        return stack;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        int mode = getMode(stack);
        if (mode == GloveModes.DELETE.ordinal())
        {
            return EnumAction.bow;
        }
        return EnumAction.none;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        int mode = getMode(stack);
        if (mode == GloveModes.DELETE.ordinal())
        {
            return 10000;
        }
        return 0;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int ticks)
    {
        int mode = getMode(stack);
        if (mode == GloveModes.DELETE.ordinal())
        {
            //Consume energy after stop using to prevent client side sync issues
            consumeEnergy(stack, energy_cost_delete * ticks, true);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int ticks_left)
    {
        int mode = getMode(stack);
        System.out.println("Using tick - " + ticks_left + " mode: " + mode);
        if (mode == GloveModes.DELETE.ordinal() && ticks_left > 0)
        {
            Selection select = SelectionHandler.getSelection(player.getUniqueID());

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
                            player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal(getUnlocalizedName() + ".info.delete.noblockdistance.name")));
                            player.stopUsingItem();
                        }
                    }
                    else
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal(getUnlocalizedName() + ".info.delete.noblocks.name")));
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
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal(getUnlocalizedName() + ".info.noselection.name")));
                    }
                    else if (!select.isValid())
                    {
                        player.addChatComponentMessage(new ChatComponentText(LanguageUtility.getLocal(getUnlocalizedName() + ".info.invalidselection.name")));
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (getMode(stack) != GloveModes.NONE.ordinal() && world != null && player != null)
        {
            Location location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block != null && !location.isAirBlock())
            {
                int mode = getMode(stack);
                if (mode == GloveModes.SELECTION.ordinal())
                {
                    handelSelection(stack, player, location);
                    return true;
                }
                else if (mode == GloveModes.HARVEST.ordinal())
                {
                    if (block.getBlockHardness(world, x, y, z) >= 0)
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
        }
        return false;
    }

    public void playBlockBreakAnimation(Location pos)
    {
        pos.playBlockBreakAnimation();

        //Spawn random particles
        Random rand = pos.world().rand;
        for (int i = 0; i < 3 + rand.nextInt(10); i++)
        {
            Location v = pos.addRandom(rand, 0.5);
            Pos vel = new Pos().addRandom(rand, 0.2);
            v.spawnParticle("portal", vel);
        }
    }

    /**
     * Handles the selection of two points
     */
    public void handelSelection(ItemStack stack, EntityPlayer player, Location location)
    {
        if (!location.world().isRemote)
        {
            Selection select = SelectionHandler.getSelection(player.getUniqueID());

            if (player.isSneaking())
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

    @Override
    public int cycleMode(ItemStack stack, EntityPlayer player, int delta)
    {
        int n = getMode(stack) + delta;
        if (n > max_mode)
            n = 0;
        if (n < 0)
            n = max_mode;

        return n;
    }

    @Override
    public int getMode(ItemStack stack)
    {
        if (stack.getTagCompound() != null)
        {
            return stack.getTagCompound().getInteger("mode");
        }
        return 0;
    }

    @Override
    public void setMode(ItemStack stack, int mode)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("mode", mode);
    }

    public int getEnergy(ItemStack stack)
    {
        if (stack.getTagCompound() != null)
        {
            return stack.getTagCompound().getInteger("en");
        }
        return 0;
    }

    public void setEnergy(ItemStack stack, int e)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("en", e);
    }

    public boolean consumeEnergy(ItemStack stack, int e, boolean doConsume)
    {
        if (getEnergy(stack) >= e)
        {
            if (doConsume)
                setEnergy(stack, getEnergy(stack) - e);
            return true;
        }
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return getEnergy(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return getEnergy(stack) / max_energy;
    }

    public static enum GloveModes
    {
        NONE, SELECTION, DELETE, HARVEST;
    }
}
