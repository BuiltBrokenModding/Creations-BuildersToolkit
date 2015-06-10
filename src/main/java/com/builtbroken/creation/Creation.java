package com.builtbroken.creation;

import com.builtbroken.creation.content.ItemGlove;
import com.builtbroken.creation.content.forge.TileSphere;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

/**
 * Created by robert on 10/1/2014.
 */

@Mod(name = "Creations: Builder's Toolkit", modid = Creation.DOMAIN, version = "@VERSION@", dependencies = "required-after:VoltzEngine")
public class Creation extends AbstractMod
{
    public static final String DOMAIN = "creationsbt";
    public static final String PREFIX = DOMAIN + ":";

    @SidedProxy(clientSide = "com.builtbroken.creation.client.ClientProxy", serverSide = "com.builtbroken.creation.CommonProxy")
    public static CommonProxy proxy;

    @Instance(DOMAIN)
    public static Creation INSTANCE;

    public static ModCreativeTab creativeTab;
    public static Item glove;

    public static Block blockSphere;

    public Creation()
    {
        super(DOMAIN, "CreationsBuilderToolkit");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);
        // TODO re-enabled when we have more items
        //creativeTab = new ModCreativeTab(DOMAIN);
        //getManager().setTab(creativeTab);

        glove = getManager().newItem(ItemGlove.class);
        blockSphere = getManager().newBlock(TileSphere.class);
        //MinecraftForge.EVENT_BUS.register(new RenderRoboticArm());
        //creativeTab.itemStack = new ItemStack(glove);
    }

    @EventHandler
    public void init(FMLInitializationEvent e)
    {
        super.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        super.postInit(e);
    }

    @Override
    public AbstractProxy getProxy()
    {
        return proxy;
    }
}