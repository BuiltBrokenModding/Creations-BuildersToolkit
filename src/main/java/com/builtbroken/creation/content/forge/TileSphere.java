package com.builtbroken.creation.content.forge;


import com.builtbroken.jlib.data.Colors;
import com.builtbroken.jlib.model.IcoSphereCreator;
import com.builtbroken.jlib.model.Mesh;
import com.builtbroken.jlib.model.Model;
import com.builtbroken.mc.core.References;
import com.builtbroken.mc.lib.render.RenderUtility;
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

import java.awt.*;
import java.util.HashMap;
import java.util.List;

/**
 * Version of the Missile Display that is static
 * Created by robert on 1/16/2015.
 */
public class TileSphere extends Tile
{
    float y = 0;
    int index = 0;
    HashMap<Integer, Model> models = new HashMap();

    public TileSphere()
    {
        super("TileSphere", Material.anvil);
        this.itemBlock = ItemBlockMetadata.class;
        this.bounds = new Cube(0, 0, 0, 1, .1, 1);
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
        this.creativeTab = CreativeTabs.tabBlock;
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
        return new TileSphere();
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

        if (ticks % 80 == 0)
        {
            index++;
            if (index > 6)
                index = 0;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        if (!models.containsKey(index))
        {
            models.put(index, new Model(IcoSphereCreator.create(index)));
        }
        Model model = models.get(index);

        //Render debug data
        RenderUtility.renderFloatingText("I: " + index + " T:" + ticks, pos.x(), pos.y() + 2, pos.z(), Colors.WHITE.toInt());
        RenderUtility.renderFloatingText("V: " + model.meshes.get(0).getVertices().size(), pos.x(), pos.y() + 1.7, pos.z(), Colors.WHITE.toInt());

        //Start mesh rendering
        GL11.glPushMatrix();
        GL11.glColor3f(Color.white.getRed(), Color.white.getGreen(), Color.white.getBlue());
        GL11.glTranslatef(pos.xf() + 0.5f, pos.yf() + 6, pos.zf() + 0.5f);
        float scale = 2f;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(y += 1, 0, 1, 0);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(References.GREY_TEXTURE);
        model.render_wireframe = true;
        model.render();
        GL11.glPopMatrix();
    }
}
