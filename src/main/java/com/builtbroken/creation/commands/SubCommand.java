package com.builtbroken.creation.commands;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.builtbroken.creation.NBTUtility;
import net.minecraft.command.ICommandSender;

/**
 * Sub command to allow easy additions to base commands without cluttering up
 * the processCommand method
 */
public abstract class SubCommand
{    
    protected static final File SCHEMATICS_FOLDER = new File(NBTUtility.getBaseDirectory(), "schematics");
    
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
    
    /**
     * Checks if the argument is contained in the array, and that the array will not go out of bounds
     * @param args - string array
     * @param slot - index
     * @return true if args is not null, length is greater than slot, slot in the array is not null
     */
    protected boolean hasArg(String[] args, int slot)
    {
        return args != null && args.length > slot && args[slot] != null;
    }
}
