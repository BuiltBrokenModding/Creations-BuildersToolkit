package com.builtbroken.creation.content.forge;

import com.builtbroken.mc.core.network.IByteBufWriter;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/** Data object used to track the progress of smelting an item stack & acts as an inventory slot if needed
 * Created by Dark on 6/15/2015.
 */
public class SmeltStack implements IByteBufWriter
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
}
