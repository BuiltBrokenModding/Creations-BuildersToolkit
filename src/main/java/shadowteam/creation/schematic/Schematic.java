package shadowteam.creation.schematic;

import java.io.File;
import java.util.List;

import com.google.common.collect.Table;

import shadowteam.creation.vec.Cube;
import shadowteam.creation.vec.Vec;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/** Set of blocks that make up the instructions for building something
 * 
 * @author Darkguardsman
 */
public class Schematic
{
    private String _name = "Schematic";
    private Table<Block, Integer, List<Vec>> blocks;
    
    public Schematic()
    {
        
    }
    
    public Schematic(File file)
    {
        //TODO populate _name with file's name
    }
    
    public void load(World world, Cube cube)
    {
        
    }
    
    public void load(NBTTagCompound nbt)
    {
        
    }
    
    public void save(NBTTagCompound nbt)
    {
        
    }
}
