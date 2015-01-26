package com.builtbroken.creation.commands;

import com.builtbroken.creation.SelectionHandler;
import com.builtbroken.creation.schematic.Schematic;
import com.builtbroken.creation.vec.Cube;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandCopy extends SubCommand
{
    @Override
    public String getCommandName()
    {
        return "copy";
    }

    @Override
    public void getHelpOutput(ICommandSender icommandsender, List<String> list)
    {
        list.add("copy - loads the selection into RAM");
    }

    @Override
    public boolean processCommand(ICommandSender icommandsender, String[] args)
    {
        String name = "schematic" + System.currentTimeMillis();
        if (args != null && args.length > 0 && args[0] != null)
        {
            name = args[0];
        }
        if (icommandsender.getEntityWorld() != null && icommandsender instanceof EntityPlayer)
        {
            Cube cube = SelectionHandler.getSelection(((EntityPlayer) icommandsender).getUniqueID());
            if (cube != null && cube.isValid())
            {
                Schematic sch = new Schematic();
                sch.setName(name);
                sch.load(icommandsender.getEntityWorld(), cube);
                SelectionHandler.setSchematic(((EntityPlayer) icommandsender).getUniqueID(), sch);
                icommandsender.addChatMessage(new ChatComponentText("Selection loaded into buffer"));
            }
            else
            {
                icommandsender.addChatMessage(new ChatComponentText("Invalid selection"));
            }
        }
        else
        {
            icommandsender.addChatMessage(new ChatComponentText("Invalid world"));

        }
        return true;
    }

}
