package com.builtbroken.creation.content.ender;

import com.builtbroken.mc.lib.helper.NBTUtility;

import java.io.File;
import java.util.HashMap;

/**
 * Created by robert on 5/2/2015.
 */
public class ChestMapLoader
{
    public static HashMap<String, ChestStorageMap> chestSets = new HashMap();


    public static void load()
    {
        File folder = new File(NBTUtility.getSaveDirectory(), "/bbm/creations/chests/");
        //TODO load all files in folder that match the data we are looking for
    }
}
