package shadowteam.creation;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by robert on 10/1/2014.
 */
@Mod(name = "Creation Mod", modid = "creationMod", version = "Dev_0.0.0.0")
public class Creation
{
    @SidedProxy(clientSide = "shadowteam.creation.ClientProxy", serverSide = "shadowteam.creation.CommonProxy")   
    public static CommonProxy proxy;
    
    public static Creation INSTANCE;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        INSTANCE = this;
    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent e)
    {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {

    }
}
