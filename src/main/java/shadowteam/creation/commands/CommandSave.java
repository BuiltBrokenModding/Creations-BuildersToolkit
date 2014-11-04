package shadowteam.creation.commands;

import java.io.File;
import java.util.List;

import shadowteam.creation.NBTUtility;
import shadowteam.creation.SelectionHandler;
import shadowteam.creation.schematic.Schematic;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatMessageComponent;

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
        String username = user.getCommandSenderName(); 
        Schematic sch = SelectionHandler.getSchematic(username); 
        
        if(sch != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            sch.save(tag);
            
            File save = new File(NBTUtility.getBaseDirectory(), "schematics/" + sch.getName() +".dat");
            if(!save.exists())
            {
                save.mkdirs();
                NBTUtility.saveData(save, tag);
                user.sendChatToPlayer(new ChatMessageComponent().addText("Saved Schematic to " + save.getAbsolutePath()));
            }
            else
            {
                user.sendChatToPlayer(ChatMessageComponent.createFromText("Save already exists with that name"));
            }
        }
        else
        {
            user.sendChatToPlayer(ChatMessageComponent.createFromText("No schematic loaded to save"));
        }
        return true;
    }

}
