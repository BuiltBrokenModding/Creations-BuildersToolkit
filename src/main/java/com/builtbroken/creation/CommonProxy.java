package com.builtbroken.creation;

import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by robert on 10/1/2014.
 */
public class CommonProxy extends AbstractProxy
{
    @Override
    public void init()
    {
        super.init();
        MinecraftForge.EVENT_BUS.register(SelectionHandler.INSTANCE);
    }

}
