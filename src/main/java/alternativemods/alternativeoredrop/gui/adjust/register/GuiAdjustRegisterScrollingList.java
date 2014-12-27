package alternativemods.alternativeoredrop.gui.adjust.register;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.events.ClientTickHandler;
import alternativemods.alternativeoredrop.gui.GuiAdjustRegister;
import alternativemods.alternativeoredrop.gui.adjust.GuiListExt;
import alternativemods.alternativeoredrop.variables.ClientVars;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiAdjustRegisterScrollingList extends GuiListExt<GuiAdjustRegisterScrollingEntry> {

    private GuiAdjustRegister parent;
    public GuiAdjustRegisterScrollingEntry selected;
    private int selectedId;

    public GuiAdjustRegisterScrollingList(Minecraft mc, int xCoord, int yCoord, int width, int height, int elementHeight, GuiAdjustRegister parent) {
        super(mc, xCoord, yCoord, width, height, elementHeight);
        this.parent = parent;
        updateEntries();
    }

    public void updateEntries() {
        entries.clear();
        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry : ClientVars.sortMap.entrySet())
            entries.add(new GuiAdjustRegisterScrollingEntry(entry.getKey(), entry.getValue(), parent));
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick, int p_148144_3_, int p_148144_4_) {
        super.elementClicked(index, doubleClick, p_148144_3_, p_148144_4_);
        if(selected != null)
            selected.setSelected(false);
        selected = entries.get(index);
        selected.setSelected(true);
        selectedId = index;
        ClientTickHandler.clientTicks = 45;
    }

    @Override
    protected boolean isSelected(int index) {
        return index == selectedId;
    }
}
