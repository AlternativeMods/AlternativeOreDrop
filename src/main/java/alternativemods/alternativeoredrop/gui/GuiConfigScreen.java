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
    private GuiButton regenerateRegister;
    private GuiButton changePreferredRegister;
    private String identifiers_text;

    public GuiConfigScreen() {
        identifiers_text = StringUtils.join(AlternativeOreDrop.identifiers, ",");
    }

    public GuiConfigScreen(String identifiers) {
        identifiers_text = identifiers;
    }

    public void initGui() {
        this.identifiers = new GuiTextField(this.fontRenderer, this.width / 2 - 125, 60, 250, 20);
        this.identifiers.setFocused(true);
        this.identifiers.setText(identifiers_text);

        this.applyIdentifiers = new GuiButton(0, this.width / 2 - 126, this.height - 155, 120, 20, "Apply identifiers");
        if(this.identifiers.getText().isEmpty())
            this.applyIdentifiers.enabled = false;
        this.buttonList.add(this.applyIdentifiers);

        this.regenerateRegister = new GuiButton(1, this.width / 2 + 6, this.height - 155, 120, 20, "Regenerate Register");
        this.buttonList.add(this.regenerateRegister);

        this.changePreferredRegister = new GuiButton(2, this.width / 2 - 126, this.height - 55, 253, 20, "Adjust preferred register");
        this.buttonList.add(this.changePreferredRegister);
    }

    protected void actionPerformed(GuiButton button)
    {
        if(!button.enabled)
            return;

        if(button == this.applyIdentifiers) {
            this.mc.thePlayer.addChatMessage("Identifiers set to:");
            this.mc.thePlayer.addChatMessage(identifiers.getText());

            PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(1, new String[]{identifiers.getText()}));
            this.mc.thePlayer.closeScreen();
        }
        if(button == this.regenerateRegister) {
            PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(2, new String[]{"NONE"}));
            this.mc.thePlayer.addChatMessage("Succesfully regenerated the register!");
            this.mc.thePlayer.closeScreen();
        }
        if(button == this.changePreferredRegister) {
            PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(3, new String[]{"NONE"}));
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
        this.drawCenteredString(this.fontRenderer, "AlternativeOreDrop - Main configuration", this.width / 2, 40, 16777215);

        this.identifiers.drawTextBox();

        super.drawScreen(par1, par2, par3);
    }

}