package com.builtbroken.creation.schematic;

import com.builtbroken.creation.selection.Selection;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * Set of blocks that make up the instructions for building something
 *
 * @author Darkguardsman
 */
public class Schematic
{

    private String name = "Schematic";

    private TreeMap<Pos, BlockMeta> blocks;

    private Pos size;

    private Pos center;

    public Schematic()
    {

    }

    public Schematic(File file) throws FileNotFoundException, IOException
    {
        load(file);
    }

    public void build(World world, Pos center)
    {
        System.out.println("Paste debug");
        for (Entry<Pos, BlockMeta> entry : blocks.entrySet())
        {
            Pos vec = entry.getKey().clone().add(center);
            System.out.println(vec + "  " + entry.getValue());
            vec.setBlock(world, entry.getValue().getBlock(), entry.getValue().getMeta());
        }
    }

    ////////////////////////////////////////////
    ///  Save                                ///
    ///             & Load                   ///
    ////////////////////////////////////////////

    /**
     * Loads the selection from the world
     *
     * @param world     - world to load from
     * @param selection - area to load from
     */
    public Schematic load(World world, Selection selection)
    {
        blocks = new TreeMap<Pos, BlockMeta>();
        size = selection.getSize();
        center = new Pos(selection.getXLength() / 2, 0, selection.getYLength() / 2);

        for (int y = selection.getLowPoint().yi(); y <= selection.getHighPoint().yi(); y++)
        {
            for (int x = selection.getLowPoint().xi(); x <= selection.getHighPoint().xi(); x++)
            {
                for (int z = selection.getLowPoint().zi(); z <= selection.getHighPoint().zi(); z++)
                {
                    Pos vec = new Pos(x, y, z);
                    Block block = vec.getBlock(world);
                    System.out.println(vec);
                    vec = vec.sub(selection.getLowPoint());

                    if (block != null && !block.isAir(world, x, y, z) && !(block instanceof IFluidBlock))
                    {
                        BlockMeta blockMeta = new BlockMeta(block, vec.getBlockMetadata(world));
                        blocks.put(vec, blockMeta);
                    }
                }
            }
        }
        return this;
    }

    /**
     * Loads a schematic from a NBTTagCompound, auto converts block ids and catchs missing blocks
     *
     * @param nbt - NBTTagCompound to load from, must contain the correct data
     * @return list of missing blocks if they are not present in this instance of the game
     */
    public List<MissingBlock> load(NBTTagCompound nbt)
    {
        blocks = new TreeMap<Pos, BlockMeta>();

        //Save size
        this.size = new Pos(0, 0, 0);
        size = new Pos(nbt.getShort("sizeX"), nbt.getShort("sizeY"), nbt.getShort("sizeZ"));

        //Save center
        this.center = new Pos(nbt.getShort("centerX"), nbt.getShort("centerY"), nbt.getShort("centerZ"));

        HashMap<Integer, MissingBlock> missingBlocks = new HashMap<Integer, MissingBlock>();
        byte[] loadedIDs = nbt.getByteArray("Blocks");
        byte[] metaLoaded = nbt.getByteArray("Data");

        HashMap<Integer, Integer> idToNewId = new HashMap<Integer, Integer>();
        NBTTagCompound idTag = nbt.getCompoundTag("idMap");
        int mapSize = idTag.getInteger("size");
        for (int i = 0; i < mapSize; i++)
        {
            //"s" + o, id.modId + ":" + id.name + ":" + block.blockID
            String save = idTag.getString("s" + i);
            if (save == null || save.isEmpty())
                continue;
            String[] split = save.split(":");
            String modName = split[0];
            String blockName = split[1];
            int blockId = Integer.getInteger(split[2], -1);

            if (blockId == -1 || modName.equalsIgnoreCase("Minecraft") || blockName.equalsIgnoreCase("block"))
                continue;

            Block block = GameRegistry.findBlock(modName, blockName);
            if (block != null)
            {
                idToNewId.put(blockId, Block.getIdFromBlock(block));
            }
            else
            {
                for (ModContainer mod : Loader.instance().getActiveModList())
                {
                    block = GameRegistry.findBlock(mod.getModId(), blockName);
                    if (block != null)
                    {
                        idToNewId.put(blockId, Block.getIdFromBlock(block));
                        break;
                    }
                }
                if (block == null)
                {
                    missingBlocks.put(blockId, new MissingBlock(modName, blockName));
                }
            }
        }

        //Load ids & meta
        int index = 0;
        for (int y = 0; y < size.yi(); y++)
        {
            for (int z = 0; z < size.zi(); z++)
            {
                for (int x = 0; x < size.xi(); x++)
                {
                    Pos vec = new Pos(x, y, z);
                    int id = loadedIDs[index];
                    int meta = metaLoaded[index];

                    System.out.println("ID: " + id + "  Meta:" + meta);

                    if (idToNewId.containsKey(id))
                    {
                        blocks.put(vec, new BlockMeta(Block.getBlockById(idToNewId.get(id)), meta));
                    }
                    else if (missingBlocks.containsKey(id))
                    {
                        missingBlocks.get(id).add(vec);
                    }
                    else if (Block.getBlockById(id) != null)
                    {
                        blocks.put(vec, new BlockMeta(Block.getBlockById(id), meta));
                    }
                    index++;

                }
            }
        }
        return new ArrayList<MissingBlock>(missingBlocks.values());
    }

    public void save(NBTTagCompound nbt)
    {
        //Save size
        nbt.setShort("sizeX", (short) size.xi());
        nbt.setShort("sizeY", (short) size.yi());
        nbt.setShort("sizeZ", (short) size.zi());

        //Save center
        nbt.setShort("centerX", (short) center.xi());
        nbt.setShort("centerY", (short) center.yi());
        nbt.setShort("centerZ", (short) center.zi());

        //Save ids and meta
        byte[] setIDs = new byte[size.xi() * size.yi() * size.zi()];
        byte[] setMetas = new byte[size.xi() * size.yi() * size.zi()];
        int index = 0;

        List<Block> blockList = new LinkedList<Block>();

        for (BlockMeta block : blocks.values())
        {
            if (!blockList.contains(block.getBlock()))
            {
                blockList.add(block.getBlock());
            }
            setIDs[index] = (byte) (Block.getIdFromBlock(block.getBlock()) & 0xff);
            setMetas[index] = (byte) (block.getMeta() & 0xff);

            index++;
        }
        nbt.setByteArray("Blocks", setIDs);
        nbt.setByteArray("Data", setMetas);

        //Save ids to names for translating during load time
        NBTTagCompound idTag = new NBTTagCompound();
        idTag.setInteger("size", blockList.size());
        int o = 0;
        for (Block block : blockList)
        {
            UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(block);
            if (id != null)
            {
                idTag.setString("s" + o, id.modId + ":" + id.name + ":" + Block.getIdFromBlock(block));

            }
            else
            {
                idTag.setString("s" + o, "Minecraft:block:" + Block.getIdFromBlock(block));
            }
            o++;
        }
        nbt.setTag("idMap", idTag);

    }

    public void load(File file) throws FileNotFoundException, IOException
    {
        load(CompressedStreamTools.readCompressed(new FileInputStream(file)));
    }

    public void save(File file)
    {
        File tempFile = new File(file.getParent(), file.getName() + "_tmp.dat");

        NBTTagCompound tag = new NBTTagCompound();
        save(tag);

        try
        {
            CompressedStreamTools.writeCompressed(tag, new FileOutputStream(tempFile));

            if (file.exists())
            {
                file.delete();
            }

            tempFile.renameTo(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////
    ///  Manipulation                        ///
    ////////////////////////////////////////////

    public Schematic rotateClockwise()
    {
        Schematic sch = clone();
        TreeMap<Pos, BlockMeta> map = new TreeMap<Pos, BlockMeta>();
        for (Entry<Pos, BlockMeta> entry : blocks.entrySet())
        {
            map.put(new Pos(entry.getKey().z(), entry.getKey().y(), size.xi() - entry.getKey().x()), entry.getValue());
        }
        size = new Pos(size.z(), size.y(), size.x());
        return sch;
    }

    public Schematic rotateCounterClockwise()
    {
        Schematic sch = clone();
        TreeMap<Pos, BlockMeta> map = new TreeMap<Pos, BlockMeta>();
        for (Entry<Pos, BlockMeta> entry : blocks.entrySet())
        {
            map.put(new Pos(size.zi() - entry.getKey().z(), entry.getKey().y(), entry.getKey().x()), entry.getValue());
        }
        size = new Pos(size.z(), size.y(), size.x());
        return sch;
    }

    ////////////////////////////////////////////
    ///  Field Getters                       ///
    ///             & Setters                ///
    ////////////////////////////////////////////

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Pos getSize()
    {
        return size;
    }

    public void setSize(Pos size)
    {
        this.size = size;
    }

    public Pos getCenter()
    {
        return center;
    }

    public void setCenter(Pos center)
    {
        this.center = center;
    }

    public Map<Pos, BlockMeta> getBlocks()
    {
        return Collections.unmodifiableMap(blocks);
    }

    public Schematic clone()
    {
        Schematic schematic = new Schematic();
        schematic.blocks = (TreeMap<Pos, BlockMeta>) blocks.clone();
        schematic.size = size;
        schematic.center = center;
        schematic.name = name;
        return schematic;
    }
}
