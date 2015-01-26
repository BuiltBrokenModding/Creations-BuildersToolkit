package com.builtbroken.creation.selection.commands;

import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.creation.schematic.Schematic;
import com.builtbroken.mc.lib.transform.vector.Pos;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandPaste extends SubCommand
{

    @Override
    public String getCommandName()
    {
        return "paste";
    }

    @Override
    public void getHelpOutput(ICommandSender icommandsender, List<String> list)
    {
        list.add("paste - places the schematic where the player is standing");
        list.add("paste <x> <y> <z> - location based paste");
    }

    @Override
    public boolean processCommand(ICommandSender user, String[] args)
    {
        if (user.getEntityWorld() != null)
        {
            Pos vec = null;
            if (hasArg(args, 0))
            {
                if (!hasArg(args, 1) || !hasArg(args, 2))
                {
                    user.addChatMessage(new ChatComponentText("Missing arguments for location"));
                    return true;
                }
                try
                {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);
                    vec = new Pos(x, y, z);

                } catch (NumberFormatException e)
                {
                    user.addChatMessage(new ChatComponentText("Invalid arguments"));
                    return true;
                }
            }
            if (vec == null && user.getPlayerCoordinates() != null)
            {
                vec = new Pos(user.getPlayerCoordinates());
            }

            if (vec != null)
            {
                Schematic sch = SelectionHandler.getSchematic(user.getCommandSenderName());
                if (sch != null)
                {
                    long time = System.nanoTime();
                    sch.build(user.getEntityWorld(), vec);
                    long dif = System.nanoTime() - time;
                    user.addChatMessage(new ChatComponentText("Built in " + dif + " nano secs"));
                }
                else
                {
                    user.addChatMessage(new ChatComponentText("No schematic loaded to paste"));
                }
            }
            else
            {
                user.addChatMessage(new ChatComponentText("Failed to get a location to paste to"));
            }

        }
        return true;
    }

}
