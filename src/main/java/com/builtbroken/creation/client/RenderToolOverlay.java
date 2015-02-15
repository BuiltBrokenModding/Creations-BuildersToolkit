package com.builtbroken.creation.client;

import com.builtbroken.creation.Creation;
import com.builtbroken.mc.api.items.IModeItem;
import com.builtbroken.mc.lib.helper.LanguageUtility;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

public class RenderToolOverlay
{
    private ResourceLocation widgets = new ResourceLocation("minecraft:textures/gui/widgets.png");

    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent event)
    {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null)
            {
                ItemStack stack = mc.thePlayer.getCurrentEquippedItem();
                if (stack != null && stack.getItem() == Creation.glove)
                {
                    ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    FontRenderer fontRender = mc.fontRenderer;
                    int height = res.getScaledHeight();
                    int width = res.getScaledWidth();
                    int xPosition = width / 2 - 20 * 2;
                    int yPosition = height - 60;
                    int textColor = 0xFFFFFF;

                    // Draw slots
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(widgets);

                    //mc.ingameGUI.drawTexturedModalRect(xPosition, yPosition, 1, 1, 80, 20);

                    String mode = LanguageUtility.getLocal(stack.getUnlocalizedName() + ".mode.name") + ": " + LanguageUtility.getLocal(stack.getUnlocalizedName() + ".mode." + ((IModeItem) stack.getItem()).getMode(stack) + ".name");
                    mc.ingameGUI.drawCenteredString(fontRender, mode, xPosition + 30, yPosition + 25, textColor);
                }
            }
        }

    }

    public void drawRect(int xPosition, int yPosition, int width, int height, int color, int transparent)
    {
        int x1 = xPosition + width;
        int y1 = yPosition + height;
        int j1;
        if (xPosition < x1)
        {
            j1 = xPosition;
            xPosition = x1;
            x1 = j1;
        }
        if (yPosition < y1)
        {
            j1 = yPosition;
            yPosition = y1;
            y1 = j1;
        }
        float f3 = (float) (transparent) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(f, f1, f2, f3);
        tessellator.startDrawingQuads();
        tessellator.addVertex((double) xPosition, (double) y1, 0.0D);
        tessellator.addVertex((double) x1, (double) y1, 0.0D);
        tessellator.addVertex((double) x1, (double) yPosition, 0.0D);
        tessellator.addVertex((double) xPosition, (double) yPosition, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
