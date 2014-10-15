package shadowteam.creation.client;

import java.util.EnumSet;

import shadowteam.creation.Creation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class CreationKeyHandler extends KeyHandler
{

    public CreationKeyHandler(KeyBinding[] keyBindings, boolean[] bs)
    {
        super(keyBindings, bs);
    }

    @Override
    public String getLabel()
    {
        return "CreationKeyHandler";
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        if (!tickEnd)
            return; // do nothing. no repeats, and only on the tickEnd
        
        Minecraft mc = Minecraft.getMinecraft();
        
        // only do it when the game is up, and no other gui is open
        if (mc.currentScreen == null && mc.thePlayer != null && mc.theWorld != null)
        {
            // check item.
            ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
            
            if (stack == null)
                return; // empty hands
            else if (stack.getItem() == Creation.wand) // have the wand in-hand
            {
                // ACTUALLY DO STUFF!
                if (kb == ClientProxy.keyWandGui)
                {
                    mc.thePlayer.openGui(Creation.INSTANCE, 0, mc.theWorld, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) { /* nope */ }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

}
