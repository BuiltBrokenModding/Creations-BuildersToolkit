package shadowteam.creation.schematic;

import java.util.ArrayList;

import shadowteam.creation.vec.Vec;

/** Information about missing blocks during schematic load time
 * 
 * @author Darkguardsman */
public class MissingBlock extends ArrayList<Vec>
{
    public final String modID;
    public final String blockName;
    
    public MissingBlock(String modID, String blockName)
    {
        this.modID = modID;
        this.blockName = blockName;
    }
}
