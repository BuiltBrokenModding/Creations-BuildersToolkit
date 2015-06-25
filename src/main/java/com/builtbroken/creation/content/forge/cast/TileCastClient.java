package com.builtbroken.creation.content.forge.cast;

import com.builtbroken.creation.Creation;
import com.builtbroken.mc.api.items.I2DCastItem;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.builtbroken.mc.prefab.tile.Tile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Dark on 6/23/2015.
 */
public class TileCastClient extends TileCast
{
    protected static IIcon top_icon;
    protected static IIcon side_icon;

    public TileCastClient()
    {
        super();
        this.isOpaque = false;
        this.renderNormalBlock = true;
        this.renderTileEntity = true;
    }

    @Override
    public Tile newTile()
    {
        return new TileCastClient();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side)
    {
        if (side == 1)
            return top_icon;
        return side_icon;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        this.side_icon = iconRegister.registerIcon(Creation.PREFIX + "cast_side");
        this.top_icon = iconRegister.registerIcon(Creation.PREFIX + "cast_top");
    }

    @SideOnly(Side.CLIENT)
    public void renderDynamic(Pos pos, float frame, int pass)
    {
        RenderItemOverlayUtility.renderIcon(Blocks.lava.getIcon(0, 0), 0, pos.add(0.5f, .73f, 0.5f), ForgeDirection.UP, false);

        if (cast_stack != null)
        {
            if (cast_stack.getItem() instanceof I2DCastItem)
            {
                RenderItemOverlayUtility.renderIcon(((I2DCastItem) cast_stack.getItem()).getCastIcon(cast_stack), 1, pos.add(0.5f, .75f, 0.5f), ForgeDirection.UP, true);
            }
            if (getTank() != null)
            {

            }
        }
    }
}
