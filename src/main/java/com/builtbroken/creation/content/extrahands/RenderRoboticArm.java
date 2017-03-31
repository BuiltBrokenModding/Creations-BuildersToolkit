package com.builtbroken.creation.content.extrahands;

import com.builtbroken.mc.lib.render.RenderUtility;
import com.builtbroken.mc.imp.transform.vector.Pos;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Created by robert on 3/8/2015.
 */
public class RenderRoboticArm
{
    @SubscribeEvent
    public void playerRenderEvent(RenderPlayerEvent.Post e)
    {
        Pos localPos = new Pos(Minecraft.getMinecraft().thePlayer);
        Pos playerPos = new Pos(e.entityPlayer);
        float correction = 1.62f;
        if (e.entityPlayer.isSneaking()) correction -= 0.125f;


        //GL11.glScalef(0.3f, 0.3f, 0.3f);
        RenderUtility.renderFloatingText("Rendering", playerPos.sub(localPos).add(0, correction, 0));
    }
}
