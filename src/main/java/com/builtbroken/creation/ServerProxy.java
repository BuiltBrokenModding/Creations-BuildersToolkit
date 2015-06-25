package com.builtbroken.creation;

import com.builtbroken.creation.CommonProxy;
import com.builtbroken.creation.Creation;
import com.builtbroken.creation.content.forge.TileFireChannel;
import com.builtbroken.creation.content.forge.cast.TileCast;

/**
 * Created by Dark on 6/25/2015.
 */
public class ServerProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        Creation.blockFireChannel = Creation.INSTANCE.getManager().newBlock(TileFireChannel.class);
        Creation.blockCast = Creation.INSTANCE.getManager().newBlock(TileCast.class);
    }
}
