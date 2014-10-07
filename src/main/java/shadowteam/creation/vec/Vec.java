package shadowteam.creation.vec;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/**
 * Location wrapper
 * @author Darkguardsman
 */
@ToString
@EqualsAndHashCode
public class Vec
{
    private double x = 0;
    private double y = 0;
    private double z = 0;
    
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
    
    //==============================
    // Get methods for the location data
    //==============================
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
}
