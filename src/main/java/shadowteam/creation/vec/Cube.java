package shadowteam.creation.vec;

import lombok.Data;
import net.minecraft.world.World;
import shadowteam.creation.schematic.Schematic;

/**
 * 3D box area
 * 
 * @author Darkguardsman
 */
@Data
public class Cube
{
    private Vec pointOne; // left click
    private Vec pointTwo; // right click
    
    public Cube(Vec one, Vec two)
    {
        pointOne = one;
        pointTwo = two;
    }
    
    /**
     * Loads up the cube area as a schematic
     * @param world - world to load from
     * @return new instance of the schematic matching the blocks in the area, only returns null if the world is null
     */
    public Schematic asSchematic(World world)
    {
        if(world != null)
        {
            Schematic sch = new Schematic();
            //TODO clone area
            return sch;
        }
        return null;
    }
    
    /**
     * Returns whether or not this Cube object is a valid cube.
     * Checks to ensure that neither internal vectors are null, and all Y values are above zero.
     * @return
     */
    public boolean isValid()
    {
        if (pointOne == null || pointTwo == null)
            return false;
        
        return pointTwo.yi() >= 0 && pointOne.yi() >= 0;
    }
}
