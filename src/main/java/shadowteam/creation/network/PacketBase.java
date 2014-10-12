package shadowteam.creation.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import shadowteam.creation.Creation;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class PacketBase
{
	public static final String	CHANNEL	= Creation.MODID;

	public final Packet250CustomPayload getPacket250()
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = CHANNEL;
		
		ByteArrayDataOutput array = ByteStreams.newDataOutput();
        array.writeByte(HandlerBase.getIdFor(getClass()));
        
        encode(array);
        packet.data = array.toByteArray();
        packet.length = packet.data.length;

		return packet;
	}

	public abstract void encode(ByteArrayDataOutput array);
	
	public abstract void decode(ByteArrayDataInput array);

	@SideOnly(Side.CLIENT)
	public abstract void actionClient(World world, EntityPlayer player);

	public abstract void actionServer(World world, EntityPlayerMP player);
}
