package shadowteam.creation.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class HandlerServer extends HandlerBase
{
	@Override
	protected void doAction(EntityPlayer player, PacketBase packet)
	{
		World world = player.worldObj;
		packet.actionServer(world, (EntityPlayerMP) player);
	}
}
