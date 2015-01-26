package com.builtbroken.creation.schematic;

import com.builtbroken.mc.lib.transform.vector.Pos;

import java.util.ArrayList;

/**
 * Information about missing blocks during schematic load time
 *
 * @author Darkguardsman
 */
public class MissingBlock extends ArrayList<Pos>
{
    public final String modID;
    public final String blockName;

    public MissingBlock(String modID, String blockName)
    {
        this.modID = modID;
        this.blockName = blockName;
    }
}
