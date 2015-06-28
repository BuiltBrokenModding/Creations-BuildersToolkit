package com.builtbroken.creation.content.glove;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.content.glove.modes.GloveMode;
import com.builtbroken.jlib.lang.TextColor;
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

import java.util.Arrays;
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
        if (stack.getItemDamage() == 1)
            lines.add(TextColor.PURPLE.getColorString() + LanguageUtility.getLocal(getUnlocalizedName() + ".creative.name"));
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

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int ticks_left)
    {
        getGloveMode(stack).onUsingTick(stack, player, ticks_left);
    }

    @Override
    public int cycleMode(ItemStack stack, EntityPlayer player, int delta)
    {
        if (delta == 0)
            return getMode(stack);
        if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("modes") && stack.getTagCompound().getCompoundTag("modes").hasKey("list"))
        {
            int[] list = stack.getTagCompound().getCompoundTag("modes").getIntArray("list");
            int n = getMode(stack) + delta;
            if (n > list.length)
                n = 0;
            else if (n < 0)
                n = list.length;
            return n;
        }
        return 0;
    }

    public boolean isModeInstalled(ItemStack stack, int mode)
    {
        if (mode == 0 || mode == 1)
            return true;

        if (mode >= 0 && mode < GloveModes.values().length)
        {
            return isModeInstalled(stack, GloveModes.values()[mode]);
        }
        return false;
    }

    public boolean isModeInstalled(ItemStack stack, GloveModes enum_mode)
    {
        //Remove this will cause an infinite loop in cycleMode
        if (enum_mode == GloveModes.NONE || enum_mode == GloveModes.SELECTION)
            return true; //Glove can always select an area

        if (stack.getTagCompound() != null)
        {
            stack.setTagCompound(new NBTTagCompound());
            if (stack.getTagCompound().hasKey("modes"))
            {
                //TODO change save format to allow modes to save data
                NBTTagCompound tag = stack.getTagCompound().getCompoundTag("modes");
                if (tag.hasKey(enum_mode.name().toLowerCase()))
                {
                    return tag.getInteger(enum_mode.name().toLowerCase()) > 0;
                }
            }
        }
        return false;
    }

    public boolean installMode(ItemStack stack, int mode)
    {
        if (mode >= 0 && mode < GloveModes.values().length)
        {
            GloveModes enum_mode = GloveModes.values()[mode];
            if (isModeInstalled(stack, enum_mode))
            {
                //TODO change save format to allow modes to save data
                NBTTagCompound tag;
                if (stack.getTagCompound().hasKey("modes"))
                    tag = stack.getTagCompound().getCompoundTag("modes");
                else
                    tag = new NBTTagCompound();
                if (!tag.hasKey(enum_mode.name().toLowerCase()))
                {
                    if (!tag.hasKey("list"))
                    {
                        tag.setIntArray("list", new int[]{0, 1, mode});
                    }
                    else
                    {
                        int[] pm = tag.getIntArray("list");
                        int[] m = new int[pm.length + 1];
                        m[pm.length] = mode;
                        for (int i = 0; i < pm.length; i++)
                        {
                            m[i] = pm[i];
                        }
                        Arrays.sort(m);
                        tag.setIntArray("list", m);
                    }
                    tag.setInteger(enum_mode.name().toLowerCase(), 1);
                    stack.getTagCompound().setTag("modes", tag);
                    return true;
                }
            }
        }
        return false;
    }


    public GloveMode getGloveMode(ItemStack stack)
    {
        int mode = getMode(stack);
        if (stack.getTagCompound() != null)
        {
            if (!stack.getTagCompound().hasKey("modes"))
            {
                stack.getTagCompound().setTag("modes", new NBTTagCompound());
            }
            if (!stack.getTagCompound().getCompoundTag("modes").hasKey("list"))
            {
                stack.getTagCompound().getCompoundTag("modes").setIntArray("list", new int[]{0, 1});
            }
            int[] list = stack.getTagCompound().getCompoundTag("modes").getIntArray("list");
            if (mode >= 0 && mode < list.length)
            {
                int actual_mode = list[mode];
                if (actual_mode >= 0 && actual_mode < GloveModes.values().length)
                {
                    return GloveModes.values()[actual_mode].mode;
                }
            }
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

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0));

        ItemStack stack = new ItemStack(item, 1, 1);
        for (GloveModes mode : GloveModes.values())
        {
            installMode(stack, mode.ordinal());
        }
        list.add(stack);
    }

}
