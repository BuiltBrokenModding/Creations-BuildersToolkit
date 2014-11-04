package shadowteam.creation.commands;

import java.util.List;

import shadowteam.creation.SelectionHandler;
import shadowteam.creation.schematic.Schematic;
import shadowteam.creation.vec.Vec;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

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
        if(user.getEntityWorld() != null)
        {
            Vec vec = null;
            if(hasArg(args, 0))
            {
                if(!hasArg(args, 1) || !hasArg(args, 2))
                {
                    user.sendChatToPlayer(ChatMessageComponent.createFromText("Missing arguments for location"));
                    return true;
                }
                try
                {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);
                    vec = new Vec(x, y, z);
                    
                }
                catch(NumberFormatException e)
                {
                    user.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid arguments"));
                    return true;
                }  
            }
            if(vec == null && user.getPlayerCoordinates() != null)
            {
                vec = new Vec(user.getPlayerCoordinates());
            }
            
            if(vec != null)
            {
                Schematic sch = SelectionHandler.getSchematic(user.getCommandSenderName());
                if(sch != null)
                {
                    long time = System.nanoTime();
                    sch.build(user.getEntityWorld(), vec);
                    long dif = System.nanoTime() - time;
                    user.sendChatToPlayer(ChatMessageComponent.createFromText("Built in " + dif +" nano secs"));
                }
                else
                {
                    user.sendChatToPlayer(ChatMessageComponent.createFromText("No schematic loaded to paste"));
                }
            }
            else
            {
                user.sendChatToPlayer(ChatMessageComponent.createFromText("Failed to get a location to paste to"));
            }
           
        }
        return true;
    }

}
