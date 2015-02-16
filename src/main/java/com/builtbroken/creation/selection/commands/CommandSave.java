package com.builtbroken.creation.selection.commands;

import com.builtbroken.creation.schematic.Schematic;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.lib.helper.NBTUtility;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.util.List;

public class CommandSave extends SubCommand
{
    @Override
    public String getCommandName()
    {
        return "save";
    }

    @Override
    public void getHelpOutput(ICommandSender icommandsender, List<String> list)
    {
        list.add("save - writes the schematic to disk");
    }

    @Override
    public boolean processCommand(ICommandSender user, String[] args)
    {
        if (user instanceof EntityPlayer)
        {
            Schematic sch = SelectionHandler.getSchematic(((EntityPlayer) user).getUniqueID());

            if (sch != null)
            {
                NBTTagCompound tag = new NBTTagCompound();
                sch.save(tag);

                File save = new File(SCHEMATICS_FOLDER, sch.getName() + ".dat");
                if (!save.exists())
                {
                    save.mkdirs();
                    NBTUtility.saveData(save, tag);
                    user.addChatMessage(new ChatComponentText("Saved Schematic to " + save.getAbsolutePath()));
                }
                else
                {
                    user.addChatMessage(new ChatComponentText("Save already exists with that name"));
                }
            }
            else
            {
                user.addChatMessage(new ChatComponentText("No schematic loaded to save"));
            }
        }
        else
        {
            user.addChatMessage(new ChatComponentText("Need to be a player to use this command"));
        }
        return true;
    }

}
