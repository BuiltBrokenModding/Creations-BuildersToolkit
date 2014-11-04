package shadowteam.creation.commands;

import java.util.List;

import shadowteam.creation.SelectionHandler;
import shadowteam.creation.schematic.Schematic;
import shadowteam.creation.vec.Cube;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

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
        String username = icommandsender.getCommandSenderName();
        String name = "schematic" + System.currentTimeMillis();
        if (args != null && args.length > 0 && args[0] != null)
        {
            name = args[0];
        }
        if (icommandsender.getEntityWorld() != null)
        {
            Cube cube = SelectionHandler.getSelection(username);
            if (cube != null && cube.isValid())
            {
                Schematic sch = new Schematic();
                sch.setName(name);
                sch.load(icommandsender.getEntityWorld(), cube);
                SelectionHandler.setSchematic(username, sch);
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Selection loaded into buffer"));
            }
            else
            {
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid selection"));
            }
        }
        else
        {
            icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid world"));

        }
        return true;
    }

}
