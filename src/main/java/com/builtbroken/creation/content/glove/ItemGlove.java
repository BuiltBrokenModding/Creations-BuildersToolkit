package com.builtbroken.creation.content.glove;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.content.glove.modes.GloveMode;
import com.builtbroken.mc.api.items.IModeItem;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by robert on 1/25/2015.
 */
public class ItemGlove extends Item implements IModeItem.IModeScrollItem, IPostInit
{
    protected int max_mode = 3;
    public static int max_energy = 10000;


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
        GameRegistry.addShapedRecipe(new ItemStack(Creation.itemGlove), "lGl", "geg", "lll", 'l', Items.leather, 'e', Items.ender_pearl, 'g', Items.gold_nugget, 'G', Blocks.glass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        //TODO translate based on mode type
        lines.add(LanguageUtility.getLocal(getUnlocalizedName() + ".info.name"));
        lines.add(LanguageUtility.getLocal(getUnlocalizedName() + ".mode.name") + ": " + LanguageUtility.getLocal(getUnlocalizedName() + ".mode." + getMode(stack) + ".name"));
        getGloveMode(stack).addInformation(stack, player, lines, b);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        getGloveMode(stack).onUpdate(stack, world, entity, slot, held);
    }


    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return getGloveMode(stack).onEntitySwing(entityLiving, stack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return getGloveMode(stack).onLeftClickEntity(stack, player, entity);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        return getGloveMode(stack).onItemRightClick(stack, world, player);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return getGloveMode(stack).getItemUseAction(stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return getGloveMode(stack).getMaxItemUseDuration(stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int ticks)
    {
        getGloveMode(stack).onPlayerStoppedUsing(stack, world, player, ticks);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return getGloveMode(stack).onItemUse(stack, player, new Location(world, x, y, z), side, hitX, hitY, hitZ);
    }


    /**
     * Handles the selection of two points
     */


    @Override
    public int cycleMode(ItemStack stack, EntityPlayer player, int delta)
    {
        //TODO check if the mode is installed
        int n = getMode(stack) + delta;
        if (n > max_mode)
            n = 0;
        if (n < 0)
            n = max_mode;

        return n;
    }

    public GloveMode getGloveMode(ItemStack stack)
    {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < GloveModes.values().length)
        {
            return GloveModes.values()[meta].mode;
        }
        return GloveModes.NONE.mode;
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

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return GloveMode.getEnergy(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return GloveMode.getEnergy(stack) / max_energy;
    }

}
