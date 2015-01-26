package com.builtbroken.creation;

import com.builtbroken.creation.selection.commands.CommandSchematic;
import com.builtbroken.mc.lib.mod.AbstractMod;
import com.builtbroken.mc.lib.mod.AbstractProxy;
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
import net.minecraftforge.classloading.FMLForgePlugin;

/**
 * Created by robert on 10/1/2014.
 */

@Mod(name = "Creation Mod", modid = Creation.MODID, version = "@VERSION@")
public class Creation extends AbstractMod
{
    public static final String MODID = "creations";

    @SidedProxy(clientSide = "com.builtbroken.creation.client.ClientProxy", serverSide = "com.builtbroken.creation.CommonProxy")
    public static CommonProxy proxy;

    @Instance(MODID)
    public static Creation INSTANCE;

    public static Item wand;
    public static CreativeTabs creativeTab;

    public Creation()
    {
        super(MODID);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        super.preInit(e);
        // creative tab
        creativeTab = new CreativeTabs(MODID)
        {
            @Override
            public Item getTabIconItem()
            {
                return wand;
            }
        };
        getManager().setTab(creativeTab);


        wand = new Item().setNoRepair().setMaxStackSize(1).setMaxDamage(0).setTextureName(MODID + ":wand").setUnlocalizedName("CreationWand");
        wand.setCreativeTab(creativeTab);
        GameRegistry.registerItem(wand, MODID + ":wand");

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