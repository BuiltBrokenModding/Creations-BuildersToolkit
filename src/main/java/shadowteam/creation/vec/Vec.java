package shadowteam.creation.vec;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Location wrapper
 * @author Darkguardsman
 */
public class Vec
{
    private double x = 0;
    private double y = 0;
    private double z = 0;
    
    public Vec(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.y = y;
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
    
    //==============================
    // Get methods for the location data
    //==============================
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
}
