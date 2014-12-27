package alternativemods.alternativeoredrop.gui.adjust;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.GuiAdjustRegister;
import alternativemods.alternativeoredrop.variables.ClientVars;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiAdjustScrollingList extends GuiListExt<GuiAdjustScrollingEntry> {

    private GuiAdjustRegister parent;
    public int selected;

    public GuiAdjustScrollingList(Minecraft mc, int xCoord, int yCoord, int width, int height, int elementHeight, GuiAdjustRegister parent) {
        super(mc, xCoord, yCoord, width, height, elementHeight);
        this.parent = parent;
        updateEntries();
    }

    public void updateEntries() {
        entries.clear();
        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry : ClientVars.sortMap.entrySet())
            entries.add(new GuiAdjustScrollingEntry(entry.getKey(), entry.getValue(), parent));
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick, int p_148144_3_, int p_148144_4_) {
        super.elementClicked(index, doubleClick, p_148144_3_, p_148144_4_);
        entries.get(selected).setSelected(false);
        selected = index;
        entries.get(selected).setSelected(true);
    }

    @Override
    protected boolean isSelected(int index) {
        return index == selected;
    }
}
