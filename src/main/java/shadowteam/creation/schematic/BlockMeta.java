package shadowteam.creation.schematic;

import lombok.Getter;
import net.minecraft.block.Block;

/** Wrapper class to store block and meta together
 * 
 * @author robert */
public class BlockMeta
{
    @Getter
    Block block;
    
    @Getter
    int meta;

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
            return ((BlockMeta) obj).getBlock() == block && ((BlockMeta) obj).getMeta() == meta;
        }
        else if(obj instanceof Block)
        {
            return ((BlockMeta) obj).getBlock() == block;
        }
        return super.equals(obj);
    }
}
