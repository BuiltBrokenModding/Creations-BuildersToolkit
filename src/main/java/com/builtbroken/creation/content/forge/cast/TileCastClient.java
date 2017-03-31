package com.builtbroken.creation.content.forge.cast;

import com.builtbroken.creation.Creation;
import com.builtbroken.mc.api.items.crafting.I2DCastItem;
import com.builtbroken.mc.lib.render.RenderItemOverlayUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
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
        if (cast_stack != null)
        {
            if (cast_stack.getItem() instanceof I2DCastItem)
            {
                RenderItemOverlayUtility.renderIcon(((I2DCastItem) cast_stack.getItem()).getCastIcon(cast_stack), 1, pos.add(0.5f, .70f, 0.5f), ForgeDirection.UP, true);
            }
            if (output_stack != null)
            {
                RenderItemOverlayUtility.renderIcon(output_stack.getIconIndex(), output_stack.getItemSpriteNumber(), 0.49f, pos.add(0.5f, .725f, 0.5f), ForgeDirection.UP, true);
            }
            else if (getTank() != null && getTank().getFluid() != null && getTank().getFluid().getFluid() != null && getTank().getFluidAmount() > 0)
            {
                float percent = getTank().getFluidAmount() / getTank().getCapacity();
                IIcon icon = getTank().getFluid().getFluid().getIcon();
                if (icon == null)
                    icon = Blocks.lava.getIcon(0, 0);
                RenderItemOverlayUtility.renderIcon(icon, 0, pos.add(0.5f, .71f + (.045 * percent), 0.5f), ForgeDirection.UP, false);
            }
        }
    }
}
