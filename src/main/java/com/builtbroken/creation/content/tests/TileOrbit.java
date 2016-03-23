package com.builtbroken.creation.content.tests;

import com.builtbroken.creation.content.forge.OrbitData;
import com.builtbroken.jlib.model.IcoSphereCreator;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.render.model.Model;
import com.builtbroken.mc.lib.transform.region.Cube;
import com.builtbroken.mc.lib.transform.vector.Location;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import com.builtbroken.mc.prefab.tile.item.ItemBlockMetadata;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dark on 6/12/2015.
 */
public class TileOrbit extends Tile
{
    Model model;
    float y = 0;
    List<OrbitData> floating_items = new ArrayList();
    Location orbit_center;

    public TileOrbit()
    {
        super("OrbitTest", Material.rock);
        this.itemBlock = ItemBlockMetadata.class;
        this.bounds = new Cube(0, 0, 0, 1, .1, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
        this.creativeTab = CreativeTabs.tabBlock;
        this.model = new Model(IcoSphereCreator.create(2));
    }

    @Override
    public Tile newTile()
    {
        return new TileOrbit();
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

    @Override
    public void update()
    {
        super.update();
        for(OrbitData data: floating_items)
        {
            data.angle = (data.angle + 1) % 360;
            data.onRenderTick();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if(orbit_center == null)
            orbit_center = new Location(world(), 0.5, 3, 0.5);
        if(floating_items.size() == 0)
        {
            for(int i = 0; i < 10; i++)
            {
                OrbitData item = new OrbitData(new ItemStack(Block.getBlockById(i + 1), 1, 0), orbit_center);
                item.angle = 36 * i;
                item.radius = 3;
                floating_items.add(item);
            }
        }
        else
        {
            for(OrbitData data : floating_items)
            {
                RenderItemOverlayUtility.renderItem(getWorldObj(), ForgeDirection.UNKNOWN, data.stack, pos.add(data.x, data.y, data.z), 0, 0);
            }
        }
        //Start mesh rendering
        GL11.glPushMatrix();
        GL11.glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 3, pos.zf() + 0.5f);
        float scale = 2f;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(y += 1, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TileSphereMorph.lava_texture);
        //model.render_wireframe = true;
        model.render();
        GL11.glPopMatrix();
    }
}
