package com.builtbroken.creation.content.ender;

import com.builtbroken.mc.api.IVirtualObject;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 5/1/2015.
 */
public class ChestStorageMap extends HashMap<Short, EnderInventory> implements IVirtualObject
{
    public final String set_name;

    public ChestStorageMap(String name)
    {
        this.set_name = name;
    }

    public EnderInventory getInventory(short s)
    {
        if(get(s) == null)
        {
            put(s, new EnderInventory(27));
        }
        return get(s);
    }


    @Override
    public File getSaveFile()
    {
        return new File(NBTUtility.getSaveDirectory(), "/bbm/creations/chests/" + set_name + ".dat");
    }

    @Override
    public void setSaveFile(File file)
    {

    }

    @Override
    public boolean shouldSaveForWorld(World world)
    {
        return world != null && world.provider.dimensionId == 0;
    }

    @Override
    public void load(NBTTagCompound nbt)
    {
        this.clear();
        if(nbt.hasKey("chests"))
        {
            NBTTagList list = nbt.getTagList("chests", 10);
            for(int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                if(tag.hasKey("id"))
                {
                    short id = tag.getShort("id");
                    EnderInventory inv = new EnderInventory(27);
                    inv.load(tag);
                    put(id, inv);
                }
            }
        }
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        for(Map.Entry<Short, EnderInventory> entry : entrySet())
        {
            if(entry.getValue() != null)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("id", entry.getKey());
                entry.getValue().save(tag);
                list.appendTag(tag);
            }
        }
        return nbt;
    }
}
