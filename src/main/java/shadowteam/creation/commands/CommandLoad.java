package shadowteam.creation.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import shadowteam.creation.SelectionHandler;
import shadowteam.creation.schematic.Schematic;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

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
        if(hasArg(args, 0))
        {
            String filename = args[0];
            File file = new File(SCHEMATICS_FOLDER, filename);
            if(!file.exists())
                file = new File(SCHEMATICS_FOLDER, filename +".dat");
            
            try
            {
                Schematic schematic = new Schematic(file);
                SelectionHandler.setSchematic(user.getCommandSenderName(), schematic);
                user.sendChatToPlayer(ChatMessageComponent.createFromText("Schematic loaded into buffer"));
                
            }
            catch(ZipException e)
            {
                user.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid file see log for details"));
                e.printStackTrace();
            }
            catch (FileNotFoundException e)
            {
                user.sendChatToPlayer(ChatMessageComponent.createFromText("File not found"));
            }
            catch (IOException e)
            {
                user.sendChatToPlayer(ChatMessageComponent.createFromText("Error reading file see log for details"));
                e.printStackTrace();
            }
        }
        else
        {
            user.sendChatToPlayer(ChatMessageComponent.createFromText("File name is required"));
        }
        return true;
    }

}
