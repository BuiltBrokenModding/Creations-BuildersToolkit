package shadowteam.creation.schematic;

import net.minecraft.block.Block;

/** Wrapper class to store block and meta together
 * 
 * @author robert */
public class BlockMeta
{
    private final Block block;
    
    private final int meta;

    public BlockMeta(Block block, int meta)
    {
        this.block = block;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof BlockMeta)
        {
            return ((BlockMeta) obj).getBlock() == getBlock() && ((BlockMeta) obj).getMeta() == meta;
        }
        else if(obj instanceof Block)
        {
            return ((BlockMeta) obj).getBlock() == getBlock();
        }
        return super.equals(obj);
    }
    
    public Block getBlock()
    {
        return this.block;
    }
    
    public int getMeta()
    {
        return this.meta;
    }
    
    @Override
    public String toString()
    {
        return "BlockMeta[" + (block != null ? block.getUnlocalizedName() : null) + "@" + meta + "]";
    }
}
