package shadowteam.creation.schematic;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

import shadowteam.creation.vec.Cube;
import shadowteam.creation.vec.Vec;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/** Set of blocks that make up the instructions for building something
 * 
 * @author Darkguardsman
 */
public class Schematic
{
    private String _name = "Schematic";
    private BiMap<Vec, BlockMeta> blocks;
    
    public Schematic()
    {
        
    }
    
    public Schematic(File file)
    {
        //TODO populate _name with file's name
    }
    
    /**
     * Loads the selection from the world
     * @param world - world to load from
     * @param cube - area to load from
     */
    public Schematic load(World world, Cube cube)
    {
        blocks = HashBiMap.create();
        for(int y = cube.getLowPoint().yi();  y < cube.getYLength(); y++)
        {
            for(int x = cube.getLowPoint().xi();  x < cube.getXLength(); x++)
            {
                for(int z = cube.getLowPoint().zi();  z < cube.getZLength(); z++)
                {
                    Vec vec = new Vec(x,y,z);
                    Block block = vec.getBlock(world);
                    if(block != null && !block.isAirBlock(world, x, y, z) && !(block instanceof IFluidBlock))
                    {
                        BlockMeta blockMeta = new BlockMeta(block, vec.getBlockMeta(world));
                        blocks.put(vec, blockMeta);
                    }
                }
            }
        }
        return this;
    }
    
    public void load(NBTTagCompound nbt)
    {
        
    }
    
    public void save(NBTTagCompound nbt)
    {
        
    }
}
