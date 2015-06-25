package com.builtbroken.creation.client;

import com.builtbroken.creation.CommonProxy;
import com.builtbroken.creation.Creation;
import com.builtbroken.creation.content.forge.TileFireChannelClient;
import com.builtbroken.creation.content.forge.cast.TileCastClient;

/**
 * Created by robert on 10/1/2014.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        Creation.blockFireChannel = Creation.INSTANCE.getManager().newBlock(TileFireChannelClient.class);
        Creation.blockCast = Creation.INSTANCE.getManager().newBlock(TileCastClient.class);
    }

    @Override
    public void init()
    {
        super.init();
    }
}
