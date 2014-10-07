package shadowteam.creation;

import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import shadowteam.creation.vec.Cube;
import shadowteam.creation.vec.Vec;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author AbrarSyed
 */
public final class SelectionHandler implements IPlayerTracker
{
    private static final SelectionHandler INSTANCE = new SelectionHandler();
    private static final Item WAND_ITEM = Creation.wand;
    
    private final HashMap<String, Cube> selections = Maps.newHashMap();
    
    private SelectionHandler() {};
    
    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }
    
    /**
     * gets the selection of the player, or creates one if none exists.
     * This is mainly so we dont have to deal with NPEs later.
     */
    public static Cube getSelection(String username)
    {
        Cube out = INSTANCE.selections.get(username);
        
        if (out == null)
        {
            out = new Cube(null, null);
            INSTANCE.selections.put(username, out);
        }
        
        return out;
    }
    
    /**
     * Resets the selection of the player to a cube with null components
     * @param playerId
     */
    private void clearSelection(String playerId)
    {
        Cube select = selections.get(playerId);
        if (select == null)
        {
            select = new Cube(null, null);
            selections.put(playerId, select);
        }
        else
        {
            select.setPointOne(null);
            select.setPointTwo(null);
        }
    }
    
    /**
     * handles the usage of the wand.
     */
    @ForgeSubscribe
    public void playerInteract(PlayerInteractEvent event)
    {
        if (event.action == Action.RIGHT_CLICK_AIR)
            return; // nothing here. dont care if they hit air.
        
        // check for item.
        if (event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() != WAND_ITEM)
            return; // not holding the wand? dont care.
        
        Cube select = getSelection(event.entityPlayer.username);
        Vec vec = new Vec(event.x, event.y, event.z);
        
        if (event.action == Action.LEFT_CLICK_BLOCK)
            select.setPointOne(vec);
        else if (event.action == Action.RIGHT_CLICK_BLOCK)
            select.setPointTwo(vec);
        
        // DEBUG CODE HERE.
        if (Creation.isDevEnv())
            event.entityPlayer.sendChatToPlayer(new ChatMessageComponent().addText("selection: " + select));
        
        // we did stuff, cancel it.
        event.setCanceled(true);
    }
    
    // ===========================================
    // player tracker things here.
    // they are to clear the selection of the player.
    // ===========================================

    @Override public void onPlayerLogin(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerLogout(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerChangedDimension(EntityPlayer player) { clearSelection(player.username); }
    @Override public void onPlayerRespawn(EntityPlayer player) { clearSelection(player.username); }
    
    // ===========================================
    // Rendering.
    // taken from: ForgeEssentials
    // ===========================================
    
    @SideOnly(Side.CLIENT)
    @ForgeSubscribe
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        
        if (player == null) // ya never know.
            return;
        
        Cube cube = getSelection(player.username);

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.instance;
        Tessellator.renderingWorldRenderer = false;

        // GL11.glLineWidth(20f);

        boolean render1 = false;

        // render p1
        if (cube.getPointOne() != null)
        {
            Vec vec1 = cube.getPointOne();
            GL11.glTranslated(vec1.xf() - RenderManager.renderPosX, vec1.yf() + 1 - RenderManager.renderPosY, vec1.zf() - RenderManager.renderPosZ);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(255, 0, 0);
            renderBlockBox(tess);
            render1 = true;
        }

        // render p2
        if (cube.getPointTwo() != null)
        {
            Vec p1 = cube.getPointOne();
            Vec p2 = cube.getPointTwo();

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

        if (cube.isValid())
        {
            float x = cube.getLowPoint().xf() - cube.getHighPoint().xf();
            float y = cube.getLowPoint().yf() - cube.getHighPoint().yf();
            float z = (float) (cube.getLowPoint().zf() - cube.getHighPoint().zf()) - 1;

            // translate to the low point..
            GL11.glTranslated(x, y, z);

            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(0, 5, 100);
            // renderBlockBox(tess);
            renderBlockBoxTo(tess, new Vec(
                     cube.getHighPoint().xf() - cube.getLowPoint().xf() + 1,
                    -(cube.getHighPoint().yf() - cube.getLowPoint().yf() + 1),
                    -(cube.getHighPoint().zf() - cube.getLowPoint().zf() + 1))
            );
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

    private void renderBlockBoxTo(Tessellator tess, Vec vec)
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
