package com.builtbroken.creation.content.forge.cast;

import com.builtbroken.creation.Creation;
import com.builtbroken.mc.api.items.I2DCastItem;
import com.builtbroken.mc.api.items.ICastItem;
import com.builtbroken.mc.api.recipe.ICastingRecipe;
import com.builtbroken.mc.api.recipe.MachineRecipeType;
import com.builtbroken.mc.core.Engine;
import com.builtbroken.mc.core.registry.implement.IPostInit;
import com.builtbroken.mc.prefab.recipe.cast.CastingData;
import com.builtbroken.mc.prefab.recipe.cast.MRHandlerCast;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.List;

/**
 * Created by Dark on 6/23/2015.
 */
public class ItemCast extends Item implements ICastItem, I2DCastItem, IPostInit
{
    public ItemCast()
    {
        this.setMaxStackSize(1);
        this.setUnlocalizedName(Creation.PREFIX + "clayCast");
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void onPostInit()
    {
        //TODO test clay ore name and see what mods use it
        String clay_oreName = null;
        int[] ids = OreDictionary.getOreIDs(new ItemStack(Items.clay_ball));
        if (ids != null)
        {
            for (int id : ids)
            {
                String name = OreDictionary.getOreName(id);
                if (name.contains("clay"))
                {
                    clay_oreName = name;
                    break;
                }

            }
        }
        //TODO add ore name check for sand and gravel

        //Loop threw ore names looking for items that can be used as part of the recipe
        for (String oreName : OreDictionary.getOreNames())
        {
            if (oreName.contains("ingot") && FluidRegistry.getFluid(oreName.replace("ingot", "").toLowerCase()) != null)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(this, 1, 0), oreName, Blocks.sand, clay_oreName != null ? clay_oreName : Items.clay_ball));
            }
            else if (oreName.contains("nugget") && FluidRegistry.getFluid(oreName.replace("nugget", "").toLowerCase()) != null)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(this, 1, 1), oreName, Blocks.sand, clay_oreName != null ? clay_oreName : Items.clay_ball));
            }
        }
    }

    @Override
    public boolean allowFluid(ItemStack stack, Fluid fluid)
    {
        if (MachineRecipeType.FLUID_CAST.getHandler() != null)
        {
            if (MachineRecipeType.FLUID_CAST.getHandler() instanceof MRHandlerCast && ((MRHandlerCast) MachineRecipeType.FLUID_CAST.getHandler()).cast_map.containsKey(getCastType(stack)))
            {
                return ((MRHandlerCast) MachineRecipeType.FLUID_CAST.getHandler()).cast_map.get(getCastType(stack)).containsKey(fluid);
            }
            return true;
        }
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
        return CastingData.INGOT.name().toLowerCase();
    }

    @Override
    public ItemStack doCast(ItemStack stack, FluidStack fluidStack, TileEntity cast)
    {
        Object recipe = MachineRecipeType.FLUID_CAST.getRecipe(0, 0, stack, fluidStack);
        if (recipe instanceof ICastingRecipe)
        {
            return ((ICastingRecipe) recipe).handleRecipe(new Object[]{stack, fluidStack}, 0, 0);
        }
        return null;
    }

    @Override
    public ItemStack onFluidAdded(ItemStack stack, Fluid fluid, int amount, int volume)
    {
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg)
    {
        Cast.INGOT.icon = reg.registerIcon(Creation.PREFIX + "cast_ingot");
        Cast.NUGGET.icon = reg.registerIcon(Creation.PREFIX + "cast_nugget");

        Cast.INGOT.icon = reg.registerIcon(Creation.PREFIX + "cast_ingot_2");
        Cast.NUGGET.icon = reg.registerIcon(Creation.PREFIX + "cast_nugget_2");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta)
    {
        if (meta == 0)
            return Cast.INGOT.icon;
        else if (meta == 1)
            return Cast.NUGGET.icon;
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getCastIcon(ItemStack stack)
    {
        if (stack.getItemDamage() == 0)
            return Cast.INGOT.icon_2;
        else if (stack.getItemDamage() == 1)
            return Cast.NUGGET.icon_2;
        return this.itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < Cast.values().length; i++)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public enum Cast
    {
        INGOT(Engine.INGOT_VOLUME),
        NUGGET(Engine.INGOT_VOLUME / 16);

        final int volume;

        @SideOnly(Side.CLIENT)
        public IIcon icon;
        @SideOnly(Side.CLIENT)
        public IIcon icon_2;

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

    @SubscribeEvent
    /**
     * Prevents some items from being consumed during crafting
     */
    public void onCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting != null && event.crafting.getItem() == this)
        {
            for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++)
            {
                ItemStack slotStack = event.craftMatrix.getStackInSlot(i);
                if (slotStack != null)
                {
                    int[] ids = OreDictionary.getOreIDs(slotStack);
                    for (int id : ids)
                    {
                        String oreName = OreDictionary.getOreName(id);
                        if (oreName.contains("ingot") || oreName.contains("nugget"))
                        {
                            slotStack.stackSize++;
                            if (event.isCancelable())
                                event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }
    }
}
