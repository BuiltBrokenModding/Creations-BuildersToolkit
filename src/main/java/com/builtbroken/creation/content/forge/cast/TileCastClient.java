package com.builtbroken.creation.content.forge.cast;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

/**
 * Created by Dark on 6/23/2015.
 */
public class TileCastClient extends TileCast
{
    @Override
    public IIcon getIcon(int side)
    {
        //TODO render in 3D
        if(side == 1 && cast_stack != null)
        {
            return cast_stack.getIconIndex();
        }
        return super.getIcon(side);
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
}
