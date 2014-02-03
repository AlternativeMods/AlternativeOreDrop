package alternativemods.alternativeoredrop.proxy;

import alternativemods.alternativeoredrop.gui.GuiConfigScreen;
import net.minecraft.client.Minecraft;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 21:06
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void openConfigGui() {
        System.out.println("Opening the gui!");
        Minecraft.getMinecraft().displayGuiScreen(new GuiConfigScreen());
        super.openConfigGui();
    }

}