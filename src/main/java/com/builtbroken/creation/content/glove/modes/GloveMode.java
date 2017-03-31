package com.builtbroken.creation.content.glove.modes;

import com.builtbroken.creation.content.glove.ItemGlove;
import com.builtbroken.mc.imp.transform.vector.Location;
import com.builtbroken.mc.imp.transform.vector.Pos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by Dark on 6/26/2015.
 */
public class GloveMode
{

    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {

    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean held)
    {
        if (entity instanceof EntityPlayer && !((EntityPlayer) entity).isUsingItem())
        {
            if (getEnergy(stack) < ItemGlove.max_energy)
            {
                //TODO add different types of regen based on upgrades and location
                this.setEnergy(stack, Math.min(ItemGlove.max_energy, getEnergy(stack) + 1));
            }
        }
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
        return false;
    }

    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
        return false;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        return stack;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int ticks)
    {

    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int ticks_left)
    {

    }

    public boolean onItemUse(ItemStack stack, EntityPlayer player, Location location, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 0;
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.none;
    }


    public static int getEnergy(ItemStack stack)
    {
        if (stack.getTagCompound() != null)
        {
            return stack.getTagCompound().getInteger("en");
        }
        return 0;
    }

    public static void setEnergy(ItemStack stack, int e)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("en", e);
    }

    public static boolean consumeEnergy(ItemStack stack, int e, boolean doConsume)
    {
        if (getEnergy(stack) >= e)
        {
            if (doConsume)
                setEnergy(stack, getEnergy(stack) - e);
            return true;
        }
        return false;
    }

    public static void playBlockBreakAnimation(Location pos)
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
}
