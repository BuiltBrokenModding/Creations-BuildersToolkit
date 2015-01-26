package com.builtbroken.creation.commands;

import com.builtbroken.creation.SelectionHandler;
import com.builtbroken.creation.schematic.Schematic;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

public class CommandLoad extends SubCommand
{

    @Override
    public String getCommandName()
    {
        return "load";
    }

    @Override
    public void getHelpOutput(ICommandSender icommandsender, List<String> list)
    {
        list.add("load <FileName> - loads a file from the schematics folder");
    }

    @Override
    public boolean processCommand(ICommandSender user, String[] args)
    {
        if (user instanceof EntityPlayer)
        {
            if (hasArg(args, 0))
            {
                String filename = args[0];
                File file = new File(SCHEMATICS_FOLDER, filename);
                if (!file.exists())
                    file = new File(SCHEMATICS_FOLDER, filename + ".dat");

                try
                {
                    Schematic schematic = new Schematic(file);
                    SelectionHandler.setSchematic(((EntityPlayer) user).getUniqueID(), schematic);
                    user.addChatMessage(new ChatComponentText("Schematic loaded into buffer"));

                } catch (ZipException e)
                {
                    user.addChatMessage(new ChatComponentText("Invalid file see log for details"));
                    e.printStackTrace();
                } catch (FileNotFoundException e)
                {
                    user.addChatMessage(new ChatComponentText("File not found"));
                } catch (IOException e)
                {
                    user.addChatMessage(new ChatComponentText("Error reading file see log for details"));
                    e.printStackTrace();
                }
            }
            else
            {
                user.addChatMessage(new ChatComponentText("File name is required"));
            }
        }
        else
        {
            user.addChatMessage(new ChatComponentText("This command can only be triggered by a player"));
        }
        return true;
    }

}
