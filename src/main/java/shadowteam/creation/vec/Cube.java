package shadowteam.creation.vec;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import shadowteam.creation.schematic.Schematic;

/**
 * 3D box area
 * 
 * @author Darkguardsman
 */
public class Cube
{
    private Vec pointOne; // left click
    private Vec pointTwo; // right click
    private Vec lowPoint;
    private Vec highPoint;   

    public Cube(Vec one, Vec two)
    {
        pointOne = one;
        pointTwo = two;
        recalc();
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
            sch.load(world, this);
            return sch;
        }
        return null;
    }
    
    /** Gets the 3D area of the selection */
    public int getArea()
    {
        return getXLength() * getYLength() * getZLength();
    }
    
    /** Gets x length of the area */
    public int getXLength()
    {
        return highPoint.xi() - lowPoint.xi() + 1;
    }

    /** Gets y length of the area */
    public int getYLength()
    {
        return highPoint.yi() - lowPoint.yi() + 1;
    }

    /** Gets z length of the area */
    public int getZLength()
    {
        return highPoint.zi() - lowPoint.zi() + 1;
    }
    
    /** Gets the size of the cube as a Vec */
    public Vec getSize()
    {
        return new Vec(getXLength(), getYLength(), getZLength());
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
    
    private void recalc()
    {
        if (!isValid())
            return; // pointless if both arnt set.
        
        lowPoint = new Vec(Math.min(pointOne.xi(), pointTwo.xi()), Math.min(pointOne.yi(), pointTwo.yi()), Math.min(pointOne.zi(), pointTwo.zi()));
        highPoint = new Vec(Math.max(pointOne.xi(), pointTwo.xi()), Math.max(pointOne.yi(), pointTwo.yi()), Math.max(pointOne.zi(), pointTwo.zi()));
    }
    
    ////////////////////////////////////////////
    ///             Utility                  ///
    ////////////////////////////////////////////
    
    public List<Vec> getBlocks(World world, Block block, int meta)
    {
        List<Vec> list = new LinkedList<Vec>();
        for(int y = lowPoint.yi(); y <= highPoint.yi(); y++ )
        {
            for(int x = lowPoint.xi(); x <= highPoint.xi(); x++ )
            {
                for(int z = lowPoint.zi(); z <= highPoint.zi(); z++ )
                {
                    Vec vec = new Vec(x, y, z);
                    Block b = vec.getBlock(world);
                }
            }
        }
        return list;
    }
    
    ////////////////////////////////////////////
    ///  Field Getters                       ///
    ///             & Setters                ///
    ////////////////////////////////////////////
    
    public void setPointOne(Vec vec)
    {
        pointOne = vec;
        recalc();
    }
    
    public void setPointTwo(Vec vec)
    {
        pointTwo = vec;
        recalc();
    }    
    
    public Vec getLowPoint()
    {
        return lowPoint;
    }

    public void setLowPoint(Vec lowPoint)
    {
        this.lowPoint = lowPoint;
    }

    public Vec getHighPoint()
    {
        return highPoint;
    }

    public void setHighPoint(Vec highPoint)
    {
        this.highPoint = highPoint;
    }

    public Vec getPointOne()
    {
        return pointOne;
    }

    public Vec getPointTwo()
    {
        return pointTwo;
    }
}
