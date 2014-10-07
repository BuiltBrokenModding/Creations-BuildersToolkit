package shadowteam.creation;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import shadowteam.creation.vec.Cube;
import shadowteam.creation.vec.Vec;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.IPlayerTracker;

/**
 * @author AbrarSyed
 */
public final class SelectionHandler implements IPlayerTracker
{
    private static final SelectionHandler INSTANCE = new SelectionHandler();
    private static final Item WAND_ITEM = Creation.wand;
    
    private final HashMap<String, Cube> selections = Maps.newHashMap();
    
    private SelectionHandler() {};
    
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }
    
    /**
     * gets the selection of the player, or creates one if none exists.
     * This is mainly so we dont have to deal with NPEs later.
     */
    public static Cube getSelection(String username)
    {
        Cube out = INSTANCE.selections.get(username);
        
        if (out == null)
        {
            out = new Cube(null, null);
            INSTANCE.selections.put(username, out);
        }
        
        System.out.println("SELECT IS NULL!");
        
        return out;
    }
    
    /**
     * Resets the selection of the player to a cube with null components
     * @param playerId
     */
    private void clearSelection(String playerId)
    {
        Cube select = selections.get(playerId);
        if (select == null)
        {
            select = new Cube(null, null);
            selections.put(playerId, select);
        }
        else
        {
            select.setPointOne(null);
            select.setPointTwo(null);
        }
    }
    
    /**
     * handles the usage of the wand.
     */
    @ForgeSubscribe
    public void playerInteract(PlayerInteractEvent event)
    {
        if (event.action == Action.RIGHT_CLICK_AIR)
            return; // nothing here. dont care if they hit air.
        
        // check for item.
        if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() != WAND_ITEM)
            return; // not holding the wand? dont care.
        
        String username = event.entityPlayer.username;
        Cube select = getSelection(event.entityPlayer.username);
        Vec vec = new Vec(event.x, event.y, event.z);
        
        if (event.action == Action.LEFT_CLICK_BLOCK)
            select.setPointOne(vec);
        else if (event.action == Action.RIGHT_CLICK_BLOCK)
            select.setPointTwo(vec);
        
        // DEBUG CODE HERE.
        if (Creation.isDevEnv())
            event.entityPlayer.sendChatToPlayer(new ChatMessageComponent().addText("selection: " + select));;
        
        // we did stuff, cancel it.
        event.setCanceled(true);
    }
    
    // ===========================================
    // player tracker things here.
    // they are to clear the selection of the player.
    // ===========================================

    @Override public void onPlayerLogin(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerLogout(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerChangedDimension(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerRespawn(EntityPlayer player) { clearSelection(player.username); }
}
