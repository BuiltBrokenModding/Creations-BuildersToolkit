package shadowteam.creation.vec;

import net.minecraft.world.World;
import shadowteam.creation.schematic.Schematic;

/**
 * 3D box area
 * 
 * @author Darkguardsman
 */
public class Cube
{
    Vec pointOne;
    Vec pointTwo;
    
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
}
