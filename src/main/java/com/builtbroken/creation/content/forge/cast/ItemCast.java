package com.builtbroken.creation.content.forge.cast;

import com.builtbroken.mc.api.items.I2DCastItem;
import com.builtbroken.mc.api.items.ICastItem;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.prefab.recipe.cast.CastingData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Dark on 6/23/2015.
 */
public class ItemCast extends Item implements ICastItem, I2DCastItem
{
    public ItemCast()
    {
        this.setMaxStackSize(1);
    }

    @Override
    public IIcon getCastIcon(ItemStack stack)
    {
        return null;
    }

    @Override
    public IIcon getCastedItemIcon(ItemStack stack, Fluid fluid)
    {
        return null;
    }

    @Override
    public boolean allowFluid(ItemStack stack, Fluid fluid)
    {
        return false;
    }

    @Override
    public int getFluidCapacity(ItemStack stack)
    {
        return Cast.getVolume(stack);
    }

    @Override
    public String getCastType(ItemStack stack)
    {
        return CastingData.INGOT.name();
    }

    @Override
    public ItemStack doCast(ItemStack stack, FluidStack fluidStack, TileEntity cast)
    {
        return null;
    }

    @Override
    public ItemStack onFluidAdded(ItemStack stack, Fluid fluid, int amount, int volume)
    {
        return stack;
    }

    public enum Cast
    {
        INGOT(Engine.INGOT_VOLUME),
        NUGGET(Engine.INGOT_VOLUME / 16);

        final int volume;

        Cast(int volume)
        {
            this.volume = volume;
        }

        public static int getVolume(ItemStack stack)
        {
            return get(stack).volume;
        }

        public static Cast get(ItemStack stack)
        {
            if (stack != null && stack.getItemDamage() >= 0 && stack.getItemDamage() < values().length)
                return values()[stack.getItemDamage()];
            return INGOT;
        }
    }
}
