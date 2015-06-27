package com.builtbroken.creation.content.glove;

import com.builtbroken.creation.content.glove.modes.GM_Delete;
import com.builtbroken.creation.content.glove.modes.GM_Harvest;
import com.builtbroken.creation.content.glove.modes.GM_Selection;
import com.builtbroken.creation.content.glove.modes.GloveMode;

/**
 * Created by Dark on 6/26/2015.
 */
public enum GloveModes
{
    NONE(new GloveMode()),
    SELECTION(new GM_Selection()),
    DELETE(new GM_Delete()),
    HARVEST(new GM_Harvest());

    public final GloveMode mode;

    GloveModes(GloveMode mode)
    {
        this.mode = mode;
    }
}
