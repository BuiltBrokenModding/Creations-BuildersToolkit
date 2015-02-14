package com.builtbroken.creation.content;

import com.builtbroken.creation.Creation;
import com.builtbroken.creation.selection.Selection;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.api.items.IModeItem;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketPlayerItem;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.transform.vector.Location;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

/**
 * Created by robert on 1/25/2015.
 */
public class ItemGlove extends Item implements IModeItem.IModeScrollItem
{
    protected int max_mode = 1;
    protected int min_mode = 0;

    public ItemGlove()
    {
        this.setMaxStackSize(1);
    }

    @Override @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean b)
    {
        //TODO translate based on mode type
        lines.add("Mode: " + getMode(stack));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (world != null && player != null)
        {
            Location location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block != null && !location.isAirBlock())
            {
                if (!world.isRemote)
                {
                    switch (stack.getItemDamage())
                    {
                        case 0:
                            handelSelection(stack, player, location);
                            break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the selection of two points
     */
    public void handelSelection(ItemStack stack, EntityPlayer player, Location location)
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

    @Override
    public int cycleMode(ItemStack stack, EntityPlayer player, int delta)
    {
        int n = getMode(stack) + delta;
        if (n > max_mode)
            n = min_mode;
        if (n < min_mode)
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
        return min_mode;
    }

    @Override
    public void setMode(ItemStack stack, int mode)
    {
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setInteger("mode", mode);
    }
}
