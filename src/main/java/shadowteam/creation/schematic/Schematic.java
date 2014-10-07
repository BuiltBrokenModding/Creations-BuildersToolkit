package shadowteam.creation.schematic;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import shadowteam.creation.vec.Cube;
import shadowteam.creation.vec.Vec;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

/** Set of blocks that make up the instructions for building something
 * 
 * @author Darkguardsman */
public class Schematic
{
    private @Getter
    String name = "Schematic";
    private @Getter
    BiMap<Vec, BlockMeta> blocks;
    private @Getter
    Vec size;
    private @Getter
    Vec center;

    public Schematic()
    {

    }

    public Schematic(File file)
    {
        //TODO populate _name with file's name
    }

    /** Loads the selection from the world
     * 
     * @param world - world to load from
     * @param cube - area to load from */
    public Schematic load(World world, Cube cube)
    {
        blocks = HashBiMap.create();
        size = cube.getSize();
        center = new Vec(cube.getXLength() / 2, 0, cube.getYLength() / 2);

        for (int y = cube.getLowPoint().yi(); y < cube.getYLength(); y++)
        {
            for (int x = cube.getLowPoint().xi(); x < cube.getXLength(); x++)
            {
                for (int z = cube.getLowPoint().zi(); z < cube.getZLength(); z++)
                {
                    Vec vec = new Vec(x, y, z).sub(cube.getLowPoint());
                    Block block = vec.getBlock(world);
                    if (block != null && !block.isAirBlock(world, x, y, z) && !(block instanceof IFluidBlock))
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
        byte[] loadedIDs = nbt.getByteArray("Blocks");
        byte[] metaLoaded = nbt.getByteArray("Data");
        int index = 0;
        for (int y = 0; y < size.yi(); y++)
        {
            for (int z = 0; z < size.zi(); z++)
            {
                for (int x = 0; x < size.xi(); x++)
                {                
                    int id = loadedIDs[index];
                    int meta = metaLoaded[index];
                    index++;
                
                }
            }
        }
    }

    public void save(NBTTagCompound nbt)
    {
        nbt.setShort("sizeX", (short) size.xi());
        nbt.setShort("sizeY", (short) size.yi());
        nbt.setShort("sizeZ", (short) size.zi());

        nbt.setShort("centerX", (short) center.xi());
        nbt.setShort("centerY", (short) center.yi());
        nbt.setShort("centerZ", (short) center.zi());

        // create arrays of IDs and and metadata + populate
        byte[] setIDs = new byte[size.xi() * size.yi() * size.zi()];
        byte[] setMetas = new byte[size.xi() * size.yi() * size.zi()];
        int index = 0;

        HashMap<Integer, String> idToName = new HashMap<Integer, String>();
        
        for (int y = 0; y < size.yi(); y++)
        {
            for (int z = 0; z < size.zi(); z++)
            {
                for (int x = 0; x < size.xi(); x++)
                {
                    Vec vec = new Vec(x, y, z);
                    BlockMeta block = blocks.get(vec);
                    if (block != null)
                    {
                        if(!idToName.containsKey(block.getBlock().blockID))
                        {
                            UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(block.getBlock());
                            idToName.put(block.getBlock().blockID, id.modId + ":" + id.name);
                        }
                        setIDs[index] = (byte) (block.getBlock().blockID & 0xff);
                        setMetas[index] = (byte) (block.getMeta() & 0xff);
                    }
                    index++;
                }
            }
        }
        nbt.setByteArray("Blocks", setIDs);
        nbt.setByteArray("Data", setMetas);
        
        //TODO save idToName map

    }

    public void save(File file)
    {

    }
}
