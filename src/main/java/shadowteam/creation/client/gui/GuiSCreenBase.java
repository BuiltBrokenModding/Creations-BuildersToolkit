package shadowteam.creation.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiSCreenBase extends GuiScreen
{
    public void initGui()
    {
//        this.buttonList.clear();
//
//        int i = -16;
//        int j = 98;
//        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + i, I18n.getString("menu.returnToMenu")));
//        if (!(this.mc.isIntegratedServerRunning()))
//        {
//            ((GuiButton) this.buttonList.get(0)).displayString = I18n.getString("menu.disconnect");
//        }
//
//        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + i, I18n.getString("menu.returnToGame")));
//        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + i, 98, 20, I18n.getString("menu.options")));
//        GuiButton localGuiButton;
//        this.buttonList.add(localGuiButton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, I18n.getString("menu.shareToLan")));
//
//        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, I18n.getString("gui.achievements")));
//        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, I18n.getString("gui.stats")));
//
//        localGuiButton.enabled = ((this.mc.isSingleplayer()) && (!(this.mc.getIntegratedServer().getPublic())));
    }

    protected void actionPerformed(GuiButton paramGuiButton)
    {
        // todo
    }

    public void drawScreen(int paramInt1, int paramInt2, float paramFloat)
    {
        drawDefaultBackground();

        drawCenteredString(this.fontRenderer, "THIS IS A GUI", this.width / 2, 40, 16777215);
//
//        super.drawScreen(paramInt1, paramInt2, paramFloat);
    }
}
