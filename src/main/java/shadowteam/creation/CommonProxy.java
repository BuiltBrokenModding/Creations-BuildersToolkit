package shadowteam.creation;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by robert on 10/1/2014.
 */
public class CommonProxy implements IGuiHandler
{
    /** Called on pre-load state of the mod */
    protected void preInit()
    {

    }

    /** Called on main load state of the mod */
    protected void init()
    {
        NetworkRegistry.instance().registerGuiHandler(Creation.INSTANCE, this);
    }

    /** Called on post load state of the mod */
    protected void postInit()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        //Should always be null
        return null;
    }
}
