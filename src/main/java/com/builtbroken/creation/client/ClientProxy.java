package com.builtbroken.creation.client;

import com.builtbroken.creation.CommonProxy;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by robert on 10/1/2014.
 */
public class ClientProxy extends CommonProxy
{
    //https://github.com/AbrarSyed/SecretRoomsMod-forge/blob/master/src/main/java/com/github/abrarsyed/secretroomsmod/client/ProxyClient.java#L33
    //https://github.com/AbrarSyed/SecretRoomsMod-forge/blob/master/src/main/java/com/github/abrarsyed/secretroomsmod/client/SecretKeyHandler.jav

    @Override
    public void init()
    {
        super.init();
        MinecraftForge.EVENT_BUS.register(new RenderSelection());
    }
}
