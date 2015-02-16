package com.builtbroken.creation.client;

import com.builtbroken.creation.selection.Selection;
import com.builtbroken.creation.selection.SelectionHandler;
import com.builtbroken.mc.lib.transform.vector.Pos;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created by robert on 1/25/2015.
 */
public class RenderSelection
{
    // ===========================================
    // Rendering.
    // taken from: ForgeEssentials
    // ===========================================

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

        if (player == null) // ya never know.
            return;

        Selection selection = SelectionHandler.getSelection(player.getUniqueID());

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.instance;
        Tessellator.renderingWorldRenderer = false;

        // GL11.glLineWidth(20f);

        boolean render1 = false;

        // render p1
        if (selection.getPointOne() != null)
        {
            Pos vec1 = selection.getPointOne();
            GL11.glTranslated(vec1.xf() - RenderManager.renderPosX, vec1.yf() + 1 - RenderManager.renderPosY, vec1.zf() - RenderManager.renderPosZ);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(255, 0, 0);
            renderBlockBox(tess);
            render1 = true;
        }

        // render p2
        if (selection.getPointTwo() != null)
        {
            Pos p1 = selection.getPointOne();
            Pos p2 = selection.getPointTwo();

            if (render1)
            {
                float x = p2.xf() - p1.xf();
                float y = (float) (p1.yf() - p2.yf()) + 1;
                float z = (float) (p1.zf() - p2.zf()) - 1;

                GL11.glTranslated(x, y, z);
            }
            else
            {
                GL11.glTranslated(p2.xf() - RenderManager.renderPosX, p2.yf() + 1 - RenderManager.renderPosY, p2.zf() - RenderManager.renderPosZ);
            }

            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(0, 255, 0);
            renderBlockBox(tess);
        }

        if (selection.isValid())
        {
            float x = selection.getLowPoint().xf() - selection.getPointTwo().xf();
            float y = selection.getLowPoint().yf() - selection.getPointTwo().yf();
            float z = (float) (selection.getLowPoint().zf() - selection.getPointTwo().zf()) - 1;

            // translate to the low point..
            GL11.glTranslated(x, y, z);

            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(0, 5, 100);

            renderBlockBoxTo(tess, new Pos(selection.getXLength(), -selection.getYLength(), -selection.getZLength()));
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // tess.renderingWorldRenderer = true;
        GL11.glPopMatrix();
    }

    /**
     * must be translated to proper point before calling
     */
    private void renderBlockBox(Tessellator tess)
    {
        tess.startDrawing(GL11.GL_LINES);

        // FRONT
        tess.addVertex(0, 0, 0);
        tess.addVertex(0, 1, 0);

        tess.addVertex(0, 1, 0);
        tess.addVertex(1, 1, 0);

        tess.addVertex(1, 1, 0);
        tess.addVertex(1, 0, 0);

        tess.addVertex(1, 0, 0);
        tess.addVertex(0, 0, 0);

        // BACK
        tess.addVertex(0, 0, -1);
        tess.addVertex(0, 1, -1);
        tess.addVertex(0, 0, -1);
        tess.addVertex(1, 0, -1);
        tess.addVertex(1, 0, -1);
        tess.addVertex(1, 1, -1);
        tess.addVertex(0, 1, -1);
        tess.addVertex(1, 1, -1);

        // betweens.
        tess.addVertex(0, 0, 0);
        tess.addVertex(0, 0, -1);

        tess.addVertex(0, 1, 0);
        tess.addVertex(0, 1, -1);

        tess.addVertex(1, 0, 0);
        tess.addVertex(1, 0, -1);

        tess.addVertex(1, 1, 0);
        tess.addVertex(1, 1, -1);

        tess.draw();
    }

    private void renderBlockBoxTo(Tessellator tess, Pos vec)
    {
        tess.startDrawing(GL11.GL_LINES);

        // FRONT
        tess.addVertex(0, 0, 0);
        tess.addVertex(0, vec.yi(), 0);

        tess.addVertex(0, vec.yi(), 0);
        tess.addVertex(vec.xi(), vec.yi(), 0);

        tess.addVertex(vec.xi(), vec.yi(), 0);
        tess.addVertex(vec.xi(), 0, 0);

        tess.addVertex(vec.xi(), 0, 0);
        tess.addVertex(0, 0, 0);

        // BACK
        tess.addVertex(0, 0, vec.zi());
        tess.addVertex(0, vec.yi(), vec.zi());
        tess.addVertex(0, 0, vec.zi());
        tess.addVertex(vec.xi(), 0, vec.zi());
        tess.addVertex(vec.xi(), 0, vec.zi());
        tess.addVertex(vec.xi(), vec.yi(), vec.zi());
        tess.addVertex(0, vec.yi(), vec.zi());
        tess.addVertex(vec.xi(), vec.yi(), vec.zi());

        // betweens.
        tess.addVertex(0, 0, 0);
        tess.addVertex(0, 0, vec.zi());

        tess.addVertex(0, vec.yi(), 0);
        tess.addVertex(0, vec.yi(), vec.zi());

        tess.addVertex(vec.xi(), 0, 0);
        tess.addVertex(vec.xi(), 0, vec.zi());

        tess.addVertex(vec.xi(), vec.yi(), 0);
        tess.addVertex(vec.xi(), vec.yi(), vec.zi());

        tess.draw();
    }
}
