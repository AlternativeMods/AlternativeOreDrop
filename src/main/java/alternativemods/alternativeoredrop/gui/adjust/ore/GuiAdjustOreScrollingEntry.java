package alternativemods.alternativeoredrop.gui.adjust.ore;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.GuiAdjustOre;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiAdjustOreScrollingEntry implements GuiListExtended.IGuiListEntry {

    private AlternativeOreDrop.OreRegister ore;
    private GuiAdjustOre parent;

    public GuiAdjustOreScrollingEntry(AlternativeOreDrop.OreRegister ore, GuiAdjustOre parent) {
        this.ore = ore;
        this.parent = parent;
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tes, int mouseX, int mouseY, boolean isSelected) {
        FontRenderer fr = parent.mc.fontRenderer;
        fr.drawString(ore.modId + ": " + ore.itemName, x + 4, y + 2, 0xFFFFFF);
    }

    @Override
    public boolean mousePressed(int id, int x, int y, int mouseEvent, int relX, int relY) {
        return false;
    }

    @Override
    public void mouseReleased(int id, int x, int y, int mouseEvent, int relX, int relY) {

    }
}
