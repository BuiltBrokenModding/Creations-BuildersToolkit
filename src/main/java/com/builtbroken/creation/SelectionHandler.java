package com.builtbroken.creation;

import com.builtbroken.creation.schematic.Schematic;
import com.builtbroken.creation.vec.Cube;
import com.builtbroken.mc.lib.transform.vector.Pos;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author AbrarSyed
 */
public final class SelectionHandler
{
    public static final SelectionHandler INSTANCE = new SelectionHandler();
    private static final Item WAND_ITEM = Creation.wand;

    private final HashMap<UUID, Cube> selections = Maps.newHashMap();
    private final HashMap<UUID, Schematic> schematics = Maps.newHashMap();

    private SelectionHandler()
    {
    }

    /**
     * gets the selection of the player, or creates one if none exists.
     * This is mainly so we dont have to deal with NPEs later.
     */
    public static Cube getSelection(UUID id)
    {
        Cube out = INSTANCE.selections.get(id);

        if (out == null)
        {
            out = new Cube(null, null);
            INSTANCE.selections.put(id, out);
        }

        return out;
    }

    /**
     * Gets the current selected schematic
     *
     * @param username - user's name
     * @return schematic, null if user has not loaded a schematic
     */
    public static Schematic getSchematic(String username)
    {
        if (INSTANCE.schematics.containsKey(username))
        {
            return INSTANCE.schematics.get(username);
        }
        return null;
    }

    /**
     * Sets the player's loaded schematic
     *
     * @param schematic - instance of a schematic
     */
    public static void setSchematic(UUID id, Schematic schematic)
    {
        INSTANCE.schematics.put(id, schematic);
    }

    /**
     * Resets the selection of the player to a cube with null components
     */
    private void clearSelection(UUID id)
    {
        Cube select = selections.get(id);
        if (select == null)
        {
            select = new Cube(null, null);
            selections.put(id, select);
        }
        else
        {
            select.setPointOne(null);
            select.setPointTwo(null);
        }
    }

    /**
     * Clears the schematic currently loaded by the player
     *
     * @param playerId - player's username
     */
    private void clearSchematic(UUID playerId)
    {
        if (INSTANCE.schematics.containsKey(playerId))
        {
            INSTANCE.schematics.remove(playerId);
        }
    }

    /**
     * handles the usage of the wand.
     */
    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event)
    {
        if (event.action == Action.RIGHT_CLICK_AIR)
            return; // nothing here. dont care if they hit air.

        // check for item.
        if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() != WAND_ITEM)
            return; // not holding the wand? dont care.

        Cube select = getSelection(event.entityPlayer.getUniqueID());
        Pos vec = new Pos(event.x, event.y, event.z);

        if (event.action == Action.LEFT_CLICK_BLOCK)
            select.setPointOne(vec);
        else if (event.action == Action.RIGHT_CLICK_BLOCK)
            select.setPointTwo(vec);

        // DEBUG CODE HERE.
        if (Creation.isDevEnv())
            event.entityPlayer.addChatComponentMessage(new ChatComponentText(("selection: " + select)));

        // we did stuff, cancel it.
        event.setCanceled(true);
    }

    // ===========================================
    // player tracker things here.
    // they are to clear the selection of the player.
    // ===========================================


    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        clearSelection(event.player.getUniqueID());
        clearSchematic(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        clearSelection(event.player.getUniqueID());
        clearSchematic(event.player.getUniqueID());
    }


}
