package shadowteam.creation.schematic;

import java.util.ArrayList;

import shadowteam.creation.vec.Vec;
import lombok.Getter;
/** Information about missing blocks during schematic load time
 * 
 * @author Darkguardsman */
public class MissingBlock extends ArrayList<Vec>
{
    @Getter final String modID;
    @Getter final String blockName;
    
    public MissingBlock(String modID, String blockName)
    {
        this.modID = modID;
        this.blockName = blockName;
    }
}
