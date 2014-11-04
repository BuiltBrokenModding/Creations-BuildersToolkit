package shadowteam.creation.vec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Location wrapper
 * @author Darkguardsman
 */
public class Vec implements Comparable<Vec> 
{
    public double x = 0;
    public double y = 0;
    public double z = 0;
    
    public Vec(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vec(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //==============================
    // World get methods
    //==============================
    /** Gets the blockID for the location
     * 
     * @param world - world instance to check the location against
     * @return blockID, or zero if the block at the location is null
     */
    public int getBlockId(World world)
    {
        return world.getBlockId(xi(), yi(), zi());
    }
    
    /** Gets the block's metadata for the location
     * 
     * @param world - world instance to check the location against
     * @return meta value
     */
    public int getBlockMeta(World world)
    {
        return world.getBlockMetadata(xi(), yi(), zi());
    }
    
    /** Gets the block's material for the location
     * 
     * @param world - world instance to check the location against
     * @return Material, may be null
     */
    public Material getBlockMeterial(World world)
    {
        return world.getBlockMaterial(xi(), yi(), zi());
    }
    
    /** Gets the block for the location, does not check for out of array
     * 
     * @param world - world instance to check the location against
     * @return block, or null if no block is found
     */
    public Block getBlock(World world)
    {
        return Block.blocksList[getBlockId(world)];
    }
    
    /** Is the block air
     * 
     * @param world - world instance to check the location against
     * @return true if air, false if not null && not air
     */
    public boolean isAir(World world)
    {
        Block block = getBlock(world);
        return block == null || block.isAirBlock(world, xi(), yi(), zi());
    }
    
    /** Is the block a fluid
     * 
     * @param world - world instance to check the location against
     * @return true if fluid
     */
    public boolean isFluid(World world)
    {
        Block block = getBlock(world);
        return block instanceof IFluidBlock || block instanceof BlockFluid;
    }
    
    
    ////////////////////////////////////////////
    ///  World Interaction                   ///
    ////////////////////////////////////////////
    
    /**
     * Sets the block to air at the location
     * 
     * @param world - world to manipulate
     * @return true if the block was turned to air
     */
    public boolean setAir(World world)
    {
        return world.setBlockToAir(xi(), yi(), zi());
    }
    
    /**
     * Sets the block at the location
     * @param world - world to manipulate
     * @param block - block to set as
     * @return true if the block was placed
     */
    public boolean setBlock(World world, Block block)
    {
        return setBlock(world, block, 0, 3);
    }
    
    /**
     * Sets the block at the location
     * @param world - world to manipulate
     * @param block - block to set as
     * @param meta - meta value to set
     * @return true if the block was placed
     */
    public boolean setBlock(World world, Block block, int meta)
    {
        return setBlock(world, block, meta, 3);
    }
    
    /**
     * Sets the block at the location
     * @param world - world to manipulate
     * @param block - block to set as
     * @param meta - meta value to set
     * @param notify - notify level to trigger, 2 for sync to client, 3 for update & sync
     * @return true if the block was placed
     */
    public boolean setBlock(World world, Block block, int meta, int notify)
    {
        return world.setBlock(xi(), yi(), zi(), block.blockID, meta, notify);
    }
    
    ////////////////////////////////////////////
    ///  Field Getters                       ///
    ///             & Setters                ///
    ////////////////////////////////////////////
    
    public int xi()
    {
        return (int)x;
    }
    
    public int yi()
    {
        return (int)y;
    }
    
    public int zi()
    {
        return (int)z;
    }
    
    public float xf()
    {
        return (float)x;
    }
    
    public float yf()
    {
        return (float)y;
    }
    
    public float zf()
    {
        return (float)z;
    }
    
    ////////////////////////////////////////////
    ///  Math helper methods                 ///
    ////////////////////////////////////////////
    public Vec sub(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    
    public Vec sub(Vec vec)
    {
        return sub(vec.x, vec.y, vec.z);
    }
    
    @Override
    public String toString()
    {
        return "Vec " + xi() + "x " + yi() + "y " + zi() + "z ";
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(object instanceof Vec)
        {
            return xi() == ((Vec)object).xi() && yi() == ((Vec)object).yi() && zi() == ((Vec)object).zi();
        }
        return super.equals(object);
    }
    
    @Override
    public int compareTo(Vec point)
    {
        if (equals(point))
        {
            return 0;
        }

        int positives = 0;
        int negatives = 0;


        if (y > point.y)
        {
            positives++;
        }
        else
        {
            negatives++;
        }
        
        if (x > point.x)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (z > point.z)
        {
            positives++;
        }
        else
        {
            negatives++;
        }

        if (positives > negatives)
        {
            return +1;
        }
        else if (negatives > positives)
        {
            return -1;
        }
        else
        {
            return (int) (x - point.x + y - point.y + z - point.z);
        }
    }
}
