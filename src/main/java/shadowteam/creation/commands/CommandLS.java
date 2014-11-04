package shadowteam.creation.commands;

import java.io.File;
import java.util.List;

import shadowteam.creation.NBTUtility;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

public class CommandLS extends SubCommand
{
    @Override
    public String getCommandName()
    {
        return "ls";
    }

    @Override
    public void getHelpOutput(ICommandSender icommandsender, List<String> list)
    {
        list.add("ls [page] - lists all schematics");        
    }

    @Override
    public boolean processCommand(ICommandSender user, String[] args)
    {
        File[] files = SCHEMATICS_FOLDER.listFiles();
        
        final int page_limit = 20;
        int page = 0;
        if(args != null && args.length > 0 && args[0] != null)
        {
            page = Integer.parseInt(args[0], -1);
        }
        if(page < 0)
        {
            user.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid page number"));
            return true;
        }
        for(int i = page * page_limit; i < files.length && i < page * page_limit + page_limit; i++)
        {
            user.sendChatToPlayer(ChatMessageComponent.createFromText("#" + i +"  " + files[i].getName()));
        }
        
        return true;
    }
}
