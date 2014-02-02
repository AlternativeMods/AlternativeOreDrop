package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.PacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 19:50
 */
public class GuiConfigScreen extends GuiScreen {

    private GuiTextField identifiers;
    private GuiButton applyIdentifiers;

    public void initGui() {
        this.identifiers = new GuiTextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.identifiers.setFocused(true);
        this.identifiers.setText(StringUtils.join(AlternativeOreDrop.identifiers, ","));

        this.applyIdentifiers = new GuiButton(0, this.width / 2 - 73, this.height - 155, 150, 20, "Apply identifiers");
        if(this.identifiers.getText().isEmpty())
            this.applyIdentifiers.enabled = false;
        this.buttonList.add(this.applyIdentifiers);
    }

    protected void actionPerformed(GuiButton button)
    {
        if(!button.enabled)
            return;

        if(button == applyIdentifiers) {
            this.mc.thePlayer.addChatMessage("Identifiers set to:");
            this.mc.thePlayer.addChatMessage(identifiers.getText());

            PacketDispatcher.sendPacketToServer(PacketHandler.createIdentifiersUpdatePacket(identifiers.getText()));
            this.mc.thePlayer.closeScreen();
        }
    }

    protected void keyTyped(char par1, int par2)
    {
        if(par2 == Keyboard.KEY_ESCAPE) {
            this.mc.thePlayer.closeScreen();
        }

        if (this.identifiers.isFocused())
        {
            this.identifiers.textboxKeyTyped(par1, par2);
        }

        if (par2 == 28 || par2 == 156)
        {
            this.actionPerformed(this.applyIdentifiers);
        }

        this.applyIdentifiers.enabled = this.identifiers.getText().length() > 0;
    }

    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);

        this.identifiers.mouseClicked(par1, par2, par3);
    }

    public void updateScreen()
    {
        this.identifiers.updateCursorCounter();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, "AlternativeOreDrop - Configuration screen", this.width / 2, 40, 16777215);

        this.identifiers.drawTextBox();

        super.drawScreen(par1, par2, par3);
    }

}