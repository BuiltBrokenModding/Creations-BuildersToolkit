package com.builtbroken.creation.content.forge;

import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.core.network.IByteBufReader;
import com.builtbroken.mc.core.network.IByteBufWriter;
import com.builtbroken.mc.lib.helper.MathUtility;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * Used to track ItemStacks orbiting the something
 */
public class OrbitData implements IByteBufReader, IByteBufWriter
{
    /** Stack that is orbiting */
    public ItemStack stack;

    /** Point to orbit, API allows a tile to be the call back for location */
    public IWorldPosition center;
    /** Current pos of the stack */
    public double x, y, z;
    /** Orbit radius */
    public float radius;
    /** Yaw orbit angle TODO support other angles */
    public float angle;


    /** Desired radius to aim towards */
    public float desired_radius;
    /** Desired angle to aim for */
    public float desired_angle;

    /** Sink all data from server to client, places orbit control into server side only */
    public boolean sink_all = false;

    public OrbitData(ItemStack stack, IWorldPosition center)
    {
        this.stack = stack;
        this.center = center;
    }

    /** Called to update any logic the tile needs server side */
    public void onTileUpdate()
    {
        if(sink_all)
            update2D_path();
    }

    /** Called inside render code to update orbit path */
    public void onRenderTick()
    {
        //Path is not update server side as its not needed and would increase packet load per tile (data * items)
        if(!sink_all)
            update2D_path();
    }

    protected void update2D_path()
    {
        x = center.x() + radius * Math.cos(Math.toRadians(angle));
        z = center.z() + radius * Math.sin(Math.toRadians(angle));
    }

    @Override
    public Object readBytes(ByteBuf buf)
    {
        boolean all = buf.readBoolean();
        stack = ByteBufUtils.readItemStack(buf);
        if (all)
        {

        }
        return buf;
    }

    @Override
    public ByteBuf writeBytes(ByteBuf buf)
    {
        buf.writeBoolean(sink_all);
        ByteBufUtils.writeItemStack(buf, stack);
        if (sink_all)
        {

        }
        return buf;
    }
}