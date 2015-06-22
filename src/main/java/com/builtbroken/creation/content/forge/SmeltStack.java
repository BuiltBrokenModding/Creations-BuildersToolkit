package com.builtbroken.creation.content.forge;

import com.builtbroken.mc.api.ISave;
import com.builtbroken.mc.core.network.IByteBufWriter;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Data object used to track the progress of smelting an item stack & acts as an inventory slot if needed
 * Created by Dark on 6/15/2015.
 */
public class SmeltStack implements IByteBufWriter, ISave
{
    public ItemStack stack;
    public int ticks;

    public SmeltStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public SmeltStack(ByteBuf buf)
    {
        this.stack = ByteBufUtils.readItemStack(buf);
        this.ticks = buf.readInt();
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, stack != null ? stack : new ItemStack(Blocks.air));
        buf.writeInt(ticks);
        return buf;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        ticks = nbt.getInteger("cookTime");
        stack = ItemStack.loadItemStackFromNBT(nbt);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        if (stack != null)
            stack.writeToNBT(nbt);
        nbt.setInteger("cookTime", ticks);
        return nbt;
    }

    @Override
    public String toString()
    {
        return "SmeltStack[s ='" + stack + "' t = '" + ticks + "]@" + hashCode();
    }
}
