package com.builtbroken.creation.content.forge;

import com.builtbroken.creation.content.tests.TileSphereMorph;
import com.builtbroken.jlib.model.IcoSphereCreator;
import com.builtbroken.jlib.model.Model;
import com.builtbroken.mc.api.IWorldPosition;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.lib.helper.MathUtility;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.*;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;
import com.builtbroken.mc.lib.transform.vector.Point;

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
    protected static Model original_model;
    protected Model model;
    protected float model_yaw;
    protected float model_scale = 2f;
    protected float orbit_radius = 0;
    protected float render_volume = 0;

    protected boolean invert = false;

    /** List of items orbiting the sphere */
    protected List<MoltenOrbitData> orbiting_items = new ArrayList();


    public TileFireChannelClient()
    {
        super();
        sphere_center = new IWorldPosition()
        {
            @Override
            public World world()
            {
                return TileFireChannelClient.this.getWorldObj();
            }

            @Override
            public double x()
            {
                return 0.5;
            }

            @Override
            public double y()
            {
                return 0.5 + (size != null ? size.r : 3) + TileFireChannelClient.this.sphere_y_delta;
            }

            @Override
            public double z()
            {
                return 0.5;
            }
        };
    }

    protected Model getModel()
    {
        if(original_model == null)
            original_model = new Model(IcoSphereCreator.create(2));
        if(model == null)
            model = original_model.clone();
        return model;
    }

    @Override
    public void update()
    {
        super.update();

        //Update animation
        if (Math.abs(volume - render_volume) > 0.01f)
        {
            render_volume = MathUtility.lerp(render_volume, volume, 0.05f);
            updateValues();
        }
        for (MoltenOrbitData data : orbiting_items)
        {
            data.desired_radius = orbit_radius;
            data.angle = (data.angle + 2) % 360;
            data.radius = MathUtility.lerp(data.radius, data.desired_radius, 0.05f); // 5% change a tick
            data.y = MathUtility.lerp(data.y, sphere_center.y(), 0.05f);
            data.update2D_path();
        }

        if (ticks % 3 == 0)
        {
            //Change model back to original slowly
            float changePercent = .1f;
            float randomChangeChance = .1f;

            List<Pos> newVerts = new ArrayList();
            List<Pos> oldVerts = getModel().meshes.get(0).getVertices();
            for (int i = 0; i < oldVerts.size() && i < original_model.meshes.get(0).getVertices().size(); i++)
            {
                newVerts.add(oldVerts.get(i).lerp(original_model.meshes.get(0).getVertices().get(i), changePercent));
            }

            //Mess model up to give the impression of movement, slowly
            for (int i = 0; i < newVerts.size(); i++)
            {
                if (worldObj.rand.nextFloat() <= randomChangeChance)
                {
                    Pos pos = new Pos().addRandom(worldObj.rand, .1);
                    newVerts.set(i, newVerts.get(i).add(pos));
                }
            }

            model.meshes.get(0).getVertices().clear();
            model.meshes.get(0).getVertices().addAll(newVerts);
        }
    }

    @Override
    protected void updateValues()
    {
        percent_filled = render_volume / size.volume;
        current_radius = percent_filled * size.r;
        model_scale = Math.max(0.3f, (size.ordinal() + 1) * (2 / 5) * percent_filled);
        //20% larger than radius TODO adjust to avoid visual collision
        orbit_radius = current_radius + (current_radius * .2f) + 0.5f;
    }

    @Override
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if (invert)
        {
            sphere_y_delta += .001 * size.r;
            if (sphere_y_delta >= 0.1 * size.r)
                invert = false;
        } else
        {
            sphere_y_delta -= .001 * size.r;
            if (sphere_y_delta <= -0.1 * size.r)
                invert = true;
        }

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
        getModel().render();
        GL11.glPopMatrix();
    }

    @Override
    public void addItem(ItemStack stack, Pos pos)
    {
        super.addItem(stack, pos);
        MoltenOrbitData data = new MoltenOrbitData(stack, sphere_center);
        data.linked_data = smelting_items.get(smelting_items.size() - 1);
        data.x = pos.x() - (x() + sphere_center.x());
        data.y = pos.y() - (y() + sphere_center.y());
        data.z = pos.z() - (z() + sphere_center.z());
        data.radius = (float) pos.toVector2().distance(new Point(sphere_center.x() + x(), sphere_center.z() + z()));
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
        if (read && id == 0)
        {
            //TODO check how often this is called
            if (orbiting_items.size() != smelting_items.size())
            {
                Iterator<MoltenOrbitData> it = orbiting_items.iterator();
                while (it.hasNext())
                {
                    MoltenOrbitData data = it.next();
                    if (!smelting_items.contains(data.linked_data))
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

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        //TODO maybe cache?
        return new Cube(-size.r, 0, -size.r, size.r, size.r + (size.r * 0.5), size.r).add(x(), y(), z()).toAABB();
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
