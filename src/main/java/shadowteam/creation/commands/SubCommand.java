package shadowteam.creation.commands;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.ICommandSender;

/**
 * Sub command to allow easy additions to base commands without cluttering up
 * the processCommand method
 */
public abstract class SubCommand
{    
    /** Name of the command */
    public abstract String getCommandName();
    
    public final List<String> getHelpOutput(ICommandSender icommandsender)
    {       
        List<String> list = new LinkedList<String>();
        getHelpOutput(icommandsender, list);
        return list;
    }
    
    /** Gets list of stuff to output when the user types /help
     * 
     * @param icommandsender - sender of the command
     * @param list - list to add lines of help commands
     */
    public abstract void getHelpOutput(ICommandSender icommandsender, List<String> list);
   
    /** Called to process the command
     * 
     * @param icommandsender - sender of the command
     * @param args - command arguments
     * @return true if the command was processed
     */
    public boolean processCommand(ICommandSender icommandsender, String[] args)
    {
        return false;
    }
}
