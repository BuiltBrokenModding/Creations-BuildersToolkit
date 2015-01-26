package com.builtbroken.creation.selection;

import com.builtbroken.creation.schematic.Schematic;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author AbrarSyed
 */
public final class SelectionHandler
{
    public static final SelectionHandler INSTANCE = new SelectionHandler();

    private final HashMap<UUID, Selection> selections = Maps.newHashMap();
    private final HashMap<UUID, Schematic> schematics = Maps.newHashMap();

    private SelectionHandler()
    {
    }

    /**
     * gets the selection of the player, or creates one if none exists.
     * This is mainly so we dont have to deal with NPEs later.
     */
    public static Selection getSelection(UUID id)
    {
        Selection out = INSTANCE.selections.get(id);

        if (out == null)
        {
            out = new Selection(null, null);
            INSTANCE.selections.put(id, out);
        }

        return out;
    }

    /**
     * Gets the current selected schematic
     *
     * @return schematic, null if user has not loaded a schematic
     */
    public static Schematic getSchematic(UUID id)
    {
        if (INSTANCE.schematics.containsKey(id))
        {
            return INSTANCE.schematics.get(id);
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
        Selection select = selections.get(id);
        if (select == null)
        {
            select = new Selection(null, null);
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
