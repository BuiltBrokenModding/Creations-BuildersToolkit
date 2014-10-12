package shadowteam.creation.network;

import java.lang.reflect.Constructor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class HandlerBase implements IPacketHandler
{
    /**
     * This is called in a static block.
     */
    private static void registerPackets()
    {
        // add the packets.
        //addPacket(0, SomePacket.class);
        //addPacket(1, SomeOtherPacket.class);
    }

	@Override
	public final void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		// no packet stuff
		if (!packet.channel.equals(PacketBase.CHANNEL))
			return;

		try
		{
		    // read out data and ID
		    ByteArrayDataInput array = ByteStreams.newDataInput(packet.data);
			int id = array.readInt();

			// get packet class
			Class<? extends PacketBase> packetClass = classMap.get(id);
			if (packetClass == null)
			{
			    throw new RuntimeException("Recieved unexpected packet ID "+id);
			}
			
			// parse into packet class
			PacketBase parsedPacket = packetClass.newInstance();
			parsedPacket.decode(array);
			
			// execute!
			doAction((EntityPlayer) player, parsedPacket);
		}
		catch (Throwable t)
		{
		    // just pass it on.. this should necver ever crash.
		    Throwables.propagate(t);
		}
	}

	protected abstract void doAction(EntityPlayer player, PacketBase packet);
	
	
	// ------------------
	// static things
	// -----------------
	
	private static final HashBiMap<Integer , Class<? extends PacketBase>> classMap = HashBiMap.create();
	static {
	    registerPackets();
	}
	
    protected static final int getIdFor(Class<? extends PacketBase> clazz)
    {
        Integer out = classMap.inverse().get(clazz);
        if (out == null)
            throw new IllegalArgumentException(""+clazz.getCanonicalName()+" isnt registerred with a packetID!");
        else
            return out;
    }
    
    private static void addPacketType(int id, Class<? extends PacketBase> clazz)
    {
        if (!hasEmptyContructor(clazz))
        {
            throw new IllegalArgumentException("You forgot an empty constructor in "+clazz.getCanonicalName());
        }
        else
        {
            classMap.put(id, clazz);
        }
    }
    
    @SuppressWarnings("rawtypes")
    private static boolean hasEmptyContructor(Class type)
    {
        try
        {
            for (Constructor c : type.getConstructors())
            {
                if (c.getParameterTypes().length == 0)
                {
                    return true;
                }
            }
        }
        catch (SecurityException e)
        {
            // really?
        }

        return false;
    }
}