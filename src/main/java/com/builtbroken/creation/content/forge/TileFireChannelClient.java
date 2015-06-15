package com.builtbroken.creation.content.forge;

import com.builtbroken.creation.content.tests.TileSphereMorph;
import com.builtbroken.jlib.model.IcoSphereCreator;
import com.builtbroken.jlib.model.Model;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Version of the Forge for clint use only. Used to cut down on code in the main class for the tile.
 * Created by Dark on 6/15/2015.
 */
@SideOnly(Side.CLIENT)
public class TileFireChannelClient extends TileFireChannel
{
    protected Model model;
    protected float model_yaw;
    protected float model_scale = 2f;
    protected float orbit_radius = 0;
    protected float render_volume = 0;

    /** List of items orbiting the sphere */
    protected List<MoltenOrbitData> orbiting_items = new ArrayList();


    public TileFireChannelClient()
    {
        super();
        this.model = new Model(IcoSphereCreator.create(2));
    }

    @Override
    public void update()
    {
        super.update();

        //Update animation
        if(Math.abs(volume - render_volume) > 0.01f)
        {
            render_volume = MathUtility.lerp(render_volume, volume, 0.05f);
            updateValues();
        }
        for(MoltenOrbitData data : orbiting_items)
        {
            //TODO scale change by speed & size for a nicer visual effect
            data.desired_radius = data.angle + 1; // We want the angle to change 1 degree a second
            data.angle  = MathUtility.lerp(data.angle, data.desired_angle, 0.05f);
            data.radius = MathUtility.lerp(data.radius, data.desired_radius, 0.05f); // 5% change a tick
            data.y  = MathUtility.lerp(data.y, sphere_center.y(), 0.05f);
            data.update2D_path();
        }
    }

    @Override
    protected void updateValues()
    {
        percent_filled = render_volume / size.volume;
        current_radius = percent_filled * size.r;
        model_scale = Math.max(0.3f, (size.ordinal() + 1) * (2/5) * percent_filled);
        //20% larger than radius TODO adjust to avoid visual collision
        orbit_radius = current_radius + (current_radius * .2f) + 1;
    }

    @Override
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Init data
        for (OrbitData data : orbiting_items)
        {
            RenderItemOverlayUtility.renderItem(getWorldObj(), ForgeDirection.UNKNOWN, data.stack, pos.add(data.x, data.y, data.z), 0, 0);
        }

        model_yaw = (model_yaw + 1) % 360;
        //Start mesh rendering
        GL11.glPushMatrix();
        GL11.glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 0.5f + size.r + sphere_y_delta, pos.zf() + 0.5f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileSphereMorph.lava_texture);
        GL11.glScalef(model_scale, model_scale, model_scale);
        GL11.glRotatef(model_yaw, 0, 1, 0);
        model.render();
        GL11.glPopMatrix();
    }

    @Override
    public void addItem(ItemStack stack, Pos pos)
    {
        super.addItem(stack, pos);
        MoltenOrbitData data = new MoltenOrbitData(stack, sphere_center);
        data.linked_data = smelting_items.get(smelting_items.size() - 1);
        data.x = pos.x();
        data.y = pos.y();
        data.z = pos.z();
        data.radius = (float) pos.distance(sphere_center.x(), sphere_center.y(), sphere_center.z());
        data.desired_radius = orbit_radius;
        orbiting_items.add(data);
    }

    @Override
    protected void remove(ItemStack stack)
    {
        super.remove(stack);
        Iterator<MoltenOrbitData> it = orbiting_items.iterator();
        while (it.hasNext())
        {
            MoltenOrbitData data = it.next();
            if (ItemStack.areItemStacksEqual(data.stack, stack))
            {
                it.remove();
                break;
            }
        }
    }

    @Override
    public boolean read(ByteBuf buf, int id, EntityPlayer player, PacketType type)
    {
        boolean read = super.read(buf, id, player, type);
        if(read && id == 0)
        {
            //TODO check how often this is called
            if(orbiting_items.size() != smelting_items.size())
            {
                Iterator<MoltenOrbitData> it = orbiting_items.iterator();
                while(it.hasNext())
                {
                    MoltenOrbitData data  = it.next();
                    if(!smelting_items.contains(data.linked_data))
                        it.remove();
                }
            }

        }
        else if (read && id == 3)
        {
            //TODO add lerp effect to volume fill so not to have a snap to size effect
        }
        return read;
    }

    @Override
    public IIcon getIcon()
    {
        return Blocks.stone.getIcon(0, 0);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    /**
     * Version of Orbit data that tracks how long the item has been melting
     */
    public class MoltenOrbitData extends OrbitData
    {
        //TODO have item degrade over time, slowly falling apart while glowing red

        /** Ticks that heat has been applied */
        public SmeltStack linked_data;

        public MoltenOrbitData(ItemStack stack, IWorldPosition center)
        {
            super(stack, center);
        }
    }
}
