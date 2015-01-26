package com.builtbroken.creation.selection;

import java.util.LinkedList;
import java.util.List;

import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import com.builtbroken.creation.schematic.Schematic;

/**
 * 3D box area
 * 
 * @author Darkguardsman
 */
public class Selection
{
    private Pos pointOne; // left click
    private Pos pointTwo; // right click
    private Pos lowPoint;
    private Pos highPoint;   

    public Selection(Pos one, Pos two)
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
    
    /** Gets the size of the cube as a Pos */
    public Pos getSize()
    {
        return new Pos(getXLength(), getYLength(), getZLength());
    }
    
    /**
     * Returns whether or not this Cube object is a valid cube.
     * Checks to ensure that neither internal Postors are null, and all Y values are above zero.
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
        
        lowPoint = new Pos(Math.min(pointOne.xi(), pointTwo.xi()), Math.min(pointOne.yi(), pointTwo.yi()), Math.min(pointOne.zi(), pointTwo.zi()));
        highPoint = new Pos(Math.max(pointOne.xi(), pointTwo.xi()), Math.max(pointOne.yi(), pointTwo.yi()), Math.max(pointOne.zi(), pointTwo.zi()));
    }
    
    ////////////////////////////////////////////
    ///             Utility                  ///
    ////////////////////////////////////////////
    
    /**
     * Replaces all blocks in the cube with a different type
     * @param world - world to manipulate
     * @param block - block to place
     * @param meta - meta to place as
     * @return list of location that were changed, used for Undo commands
     */
    public List<Pos> replaceBlocks(World world, Block block, int meta)
    {
        List<Pos> list = getBlockLocations(world, block, meta);
        for(Pos Pos : list)
        {
            Pos.setBlock(world, block, meta);
        }
        return list;
    }
    
    /** Grabs all blocks in the cube
     *  
     * @param world - world to search in
     */
    public List<Pos> getBlockLocations(World world)
    {
        return getBlockLocations(world, null, -1, -1);
    }
    
    /**
     * Grabs all blocks in the cube that match 
     * @param world - world to search in
     * @param block - block instance to match against
     * @return list of blocks, never null but can be empty
     */
    public List<Pos> getBlockLocations(World world, Block block)
    {
        return getBlockLocations(world, block, -1, -1);
    }
    
    /**
     * Grabs all blocks in the cube that match 
     * @param world - world to search in
     * @param block - block instance to match against
     * @param meta - meta value to match
     * @return list of blocks, never null but can be empty
     */
    public List<Pos> getBlockLocations(World world, Block block, int meta)
    {
        return getBlockLocations(world, block, meta, -1);
    }
    
    /**
     * Grabs all blocks in the cube that match 
     * @param world - world to search in
     * @param block - block instance to match against, if null will match all
     * @param meta - meta value to match, if -1 will match all meta
     * @param size - limiter for the list in case only a few blocks are wanted. 
     *                  If zero or less will not limit size
     * @return list of blocks, never null but can be empty
     */
    public List<Pos> getBlockLocations(World world, Block block, int meta, int size)
    {
        List<Pos> list = new LinkedList<Pos>();
        for(int y = lowPoint.yi(); y <= highPoint.yi(); y++ )
        {
            for(int x = lowPoint.xi(); x <= highPoint.xi(); x++ )
            {
                for(int z = lowPoint.zi(); z <= highPoint.zi(); z++ )
                {
                    if(size > 0&& list.size() > size)
                        return list;
                    
                    Pos Pos = new Pos(x, y, z);
                    Block b = Pos.getBlock(world);
                    int m = Pos.getBlockMetadata(world);
                    if(block == null || b == block && (meta == -1 || m == meta))
                    {
                        list.add(Pos);
                    }
                }
            }
        }
        return list;
    }   
    
    ////////////////////////////////////////////
    ///  Field Getters                       ///
    ///             & Setters                ///
    ////////////////////////////////////////////
    
    public void setPointOne(Pos Pos)
    {
        pointOne = Pos;
        recalc();
    }
    
    public void setPointTwo(Pos Pos)
    {
        pointTwo = Pos;
        recalc();
    }    
    
    public Pos getLowPoint()
    {
        return lowPoint;
    }

    public void setLowPoint(Pos lowPoint)
    {
        this.lowPoint = lowPoint;
    }

    public Pos getHighPoint()
    {
        return highPoint;
    }

    public void setHighPoint(Pos highPoint)
    {
        this.highPoint = highPoint;
    }

    public Pos getPointOne()
    {
        return pointOne;
    }

    public Pos getPointTwo()
    {
        return pointTwo;
    }
}
