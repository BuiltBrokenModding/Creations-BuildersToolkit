package com.builtbroken.creation;

import com.builtbroken.creation.content.ItemGlove;
import com.builtbroken.creation.selection.commands.CommandSchematic;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
import com.builtbroken.mc.lib.mod.ModCreativeTab;
import com.sun.java.browser.plugin2.DOM;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.classloading.FMLForgePlugin;

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

    public Creation()
    {
        super(DOMAIN);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);
        // TODO re-enabled when we have more items
        //creativeTab = new ModCreativeTab(DOMAIN);
        //getManager().setTab(creativeTab);

        glove = getManager().newItem(ItemGlove.class);
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

    /**
     * returns true if the FML runtime deobf is disabled, aka the dev env.
     * MAY NOT WORK IN 1.8, since lex wants install-time deobf rather than runtime.
     */
    public static boolean isDevEnv()
    {
        return !FMLForgePlugin.RUNTIME_DEOBF;
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandSchematic());
    }
}