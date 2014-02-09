package alternativemods.alternativeoredrop.proxy;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.GuiAdjustOre;
import alternativemods.alternativeoredrop.gui.GuiAdjustRegister;
import alternativemods.alternativeoredrop.gui.GuiConfigScreen;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 21:06
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void openConfigGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiConfigScreen());
        super.openConfigGui();
    }

    @Override
    public void openConfigGui(String identifiers) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiConfigScreen(identifiers));
        super.openConfigGui(identifiers);
    }

    @Override
    public void openAdjustingGui(String identifiers, Map<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMapJson) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustRegister(identifiers, oreMapJson));
        super.openAdjustingGui(identifiers, oreMapJson);
    }

    @Override
    public void openAdjustingOreGui(String oreName, List<AlternativeOreDrop.OreRegister> oreMapJson) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustOre(oreName, oreMapJson));
        super.openAdjustingOreGui(oreName, oreMapJson);
    }

}