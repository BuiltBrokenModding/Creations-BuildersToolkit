package shadowteam.creation.client;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import shadowteam.creation.CommonProxy;
import shadowteam.creation.client.gui.GuiSCreenBase;

/**
 * Created by robert on 10/1/2014.
 */
public class ClientProxy extends CommonProxy
{
    // in 1.7, there is a category.    new KeyBinding("key.secretroomsmod.oneWayface", Keyboard.KEY_BACKSLASH, "key.categories.gameplay");
    protected static KeyBinding keyWandGui = new KeyBinding("key.creation.wandGui", Keyboard.KEY_Y);
    protected static KeyBinding keyWandMode = new KeyBinding("key.creation.wandMode", Keyboard.KEY_U);

    @Override
    protected void init()
    {
        super.init();
        
        KeyBindingRegistry.registerKeyBinding(new CreationKeyHandler(new KeyBinding[] { keyWandGui, keyWandMode}, new boolean[] {false, false}));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch(ID)
        {
            case 0: return new GuiSCreenBase();
        }
        return null;
    }
}
