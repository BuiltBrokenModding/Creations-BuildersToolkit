package com.builtbroken.creation.content.forge;


import com.builtbroken.jlib.model.IcoSphereCreator;
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
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
public class TileSphereMorph extends Tile
{
    float y = 0;
    Model model;
    Model original_model;

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
            original_model = new Model();

            IcoSphereCreator isoSphereCreator = new IcoSphereCreator();
            model.meshes.add(isoSphereCreator.Create(2));
            original_model.meshes.add(isoSphereCreator.Create(2));
        }
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
                    newVerts.add(oldVerts.get(i).lerp(original_model.meshes.get(0).getVertices().get(i), changePercent));
                }

                //Mesh model back up, slowly
                for (int i = 0; i < newVerts.size(); i++)
                {
                    if (worldObj.rand.nextFloat() <= randomChangeChance)
                    {
                        Pos pos = new Pos().addRandom(worldObj.rand, 0.1);
                        newVerts.add(i, newVerts.get(i).add(pos));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        //Start mesh rendering
        GL11.glPushMatrix();
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 1, pos.zf() + 0.5f);
        GL11.glScalef(0.3f, 0.3f, 0.3f);
        GL11.glRotatef(y += 1, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(References.GREY_TEXTURE);
        model.render();
        //drawSphere(1, 1, 1, 10, 10);
        GL11.glPopMatrix();
    }
}
