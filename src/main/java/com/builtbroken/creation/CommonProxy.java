package com.builtbroken.creation;

import com.builtbroken.mc.lib.mod.AbstractProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
