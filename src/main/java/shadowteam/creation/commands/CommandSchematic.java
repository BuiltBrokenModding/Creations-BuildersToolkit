package shadowteam.creation.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

public class CommandSchematic extends CommandBase
{
    List<SubCommand> commands = new ArrayList<SubCommand>();
    
    public CommandSchematic()
    {
        commands.add(new CommandCopy());
        commands.add(new CommandSave());
        commands.add(new CommandLS());
        commands.add(new CommandLoad());
        commands.add(new CommandPaste());
    }
    
    @Override
    public String getCommandName()
    {
        return "sch";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender)
    {       
        return "/" + getCommandName() +" help";
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring)
    {
        if(astring != null && astring.length > 0 && astring[0] != null)
        {
            String command = astring[0];
            if(command.equalsIgnoreCase("help"))
            {
                for(SubCommand com : commands)
                {
                    icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("/" 
                            + getCommandName() + " " + com.getCommandName()));
                }
            }
            else
            {
                String[] args = Arrays.copyOfRange(astring, 1, astring.length);
                for(SubCommand com : commands)
                {
                    if(com.getCommandName().equalsIgnoreCase(command))
                    {                       
                        if(com.processCommand(icommandsender, args))
                        {
                            return;
                        }
                    }
                }
                icommandsender.sendChatToPlayer(ChatMessageComponent.createFromText("Unknown command '" + command +"'"));
            }
        }        
    }

}
