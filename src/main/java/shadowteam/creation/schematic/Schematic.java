package shadowteam.creation.schematic;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;

/** Set of blocks that make up the instructions for building something
 * 
 * @author Darkguardsman
 */
public class Schematic
{
    private String _name = "Schematic";
    
    public Schematic()
    {
        
    }
    
    public Schematic(File file)
    {
        //TODO populate _name with file's name
    }
    
    public void load(NBTTagCompound nbt)
    {
        
    }
    
    public void save(NBTTagCompound nbt)
    {
        
    }
}
