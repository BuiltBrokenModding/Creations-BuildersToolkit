package shadowteam.creation;

import java.util.logging.Logger;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.classloading.FMLForgePlugin;
import net.minecraftforge.common.Configuration;
import shadowteam.creation.commands.CommandSchematic;
import shadowteam.creation.network.HandlerClient;
import shadowteam.creation.network.HandlerServer;
import shadowteam.creation.network.PacketBase;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by robert on 10/1/2014.
 */
@NetworkMod(clientSideRequired = true, serverSideRequired = true,
    clientPacketHandlerSpec = @SidedPacketHandler(channels = { PacketBase.CHANNEL }, packetHandler = HandlerClient.class),
    serverPacketHandlerSpec = @SidedPacketHandler(channels = { PacketBase.CHANNEL }, packetHandler = HandlerServer.class))
@Mod(name = "Creation Mod", modid = Creation.MODID, version = "@VERSION@")
public class Creation
{
    public static final String MODID = "creationMod";
    
    @SidedProxy(clientSide = "shadowteam.creation.client.ClientProxy", serverSide = "shadowteam.creation.CommonProxy")   
    public static CommonProxy proxy;
    
    @Instance(MODID)
    public static Creation INSTANCE;
    public static Logger LOGGER;
    
    public static Item wand;
    public static CreativeTabs creativeTab;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        // configure logger
        LOGGER = e.getModLog();
        
        // do config stuff
        Configuration config = new Configuration(e.getSuggestedConfigurationFile());
        int wandId = config.getItem("wanditemId", 9001).getInt();
        config.save();

        // make wand
        wand = new Item(wandId-255).setNoRepair().setMaxStackSize(1).setMaxDamage(0).setTextureName(MODID+":wand").setUnlocalizedName("CreationWand");
        GameRegistry.registerItem(wand, MODID+":wand");
        
        // creative tab
        creativeTab = new CreativeTabs(MODID) { @Override public Item getTabIconItem() { return wand; } };
        wand.setCreativeTab(creativeTab);
        
        // proxy
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLPostInitializationEvent e)
    {
        SelectionHandler.init();
        
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
        proxy.postInit();
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
    public void serverStopped(FMLServerStoppedEvent event)
    {
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);
        serverCommandManager.registerCommand(new CommandSchematic());
    }
}