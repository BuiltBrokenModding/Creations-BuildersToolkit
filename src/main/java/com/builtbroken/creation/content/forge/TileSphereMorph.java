package com.builtbroken.creation.content.forge;


import com.builtbroken.creation.Creation;
import com.builtbroken.jlib.model.IcoSphereCreator;
import com.builtbroken.jlib.model.Mesh;
import com.builtbroken.jlib.model.Model;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
public class TileSphereMorph extends Tile
{
    float yaw = 0;
    float y_level = 0;
    boolean invert = false;
    Model model;
    List<Pos> original_verts = new ArrayList();

    @SideOnly(Side.CLIENT)
    public static final ResourceLocation lava_texture = new ResourceLocation(Creation.DOMAIN, References.TEXTURE_DIRECTORY +"models/lava.png");

    public TileSphereMorph()
    {
        super("TileSphereMorph", Material.anvil);
        this.itemBlock = ItemBlockMetadata.class;
        this.bounds = new Cube(0, 0, 0, 1, .1, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
        this.creativeTab = CreativeTabs.tabBlock;
    if (model == null)
    {
        model = new Model();
        IcoSphereCreator isoSphereCreator = new IcoSphereCreator();
        Mesh m = isoSphereCreator.Create(2);
        model.meshes.add(m);
        original_verts.addAll(m.getVertices());
    }
}

    public int getLightValue()
    {
        return 15;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
    {
        //TODO list.add(new ItemStack(item, 1, 0)); add Micro missile renderer
        list.add(new ItemStack(item, 1, 1));
    }


    @Override
    public Tile newTile()
    {
        return new TileSphereMorph();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon()
    {
        return Blocks.planks.getIcon(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        //We have no icons to register
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new Cube(0, 0, 0, 1, 3, 1).add(x(), y(), z()).toAABB();
    }

    @Override
    public void update()
    {
        super.update();
        if(isClient())
        {
            if (ticks % 3 == 0)
            {
                //Change model back to original slowly
                float changePercent = .1f;
                float randomChangeChance = .1f;

                List<Pos> newVerts = new ArrayList();
                List<Pos> oldVerts = model.meshes.get(0).getVertices();
                for (int i = 0; i < oldVerts.size(); i++)
                {
                    newVerts.add(oldVerts.get(i).lerp(original_verts.get(i), changePercent));
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
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if(invert)
        {
            y_level += .001;
            if(y_level >= 0.1)
                invert = false;
        }
        else
        {
            y_level -= .001;
            if(y_level <= -0.1)
                invert = true;
        }

        //Start mesh rendering
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glColor3f(Color.GRAY.getRed(), Color.GRAY.getBlue(), Color.GRAY.getGreen());
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 1 + y_level, pos.zf() + 0.5f);
        GL11.glScalef(0.3f, 0.3f, 0.3f);
        GL11.glRotatef(yaw += 1, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(lava_texture);
        model.render();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();

        /*
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 1, pos.zf() + 0.5f);
        GL11.glScalef(0.3001f, 0.3001f, 0.3001f);
        GL11.glRotatef(yaw, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(lava_texture);
        model.line = true;
        model.render();
        model.line = false;
        GL11.glPopMatrix();
        */

    }
}
