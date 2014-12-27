package alternativemods.alternativeoredrop.gui.adjust.ore;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.events.ClientTickHandler;
import alternativemods.alternativeoredrop.gui.GuiAdjustOre;
import alternativemods.alternativeoredrop.gui.adjust.GuiListExt;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiAdjustOreScrollingList extends GuiListExt<GuiAdjustOreScrollingEntry> {

    private GuiAdjustOre parent;
    private List<AlternativeOreDrop.OreRegister> ores;
    public int selectedId;

    public GuiAdjustOreScrollingList(Minecraft mc, int xCoord, int yCoord, int width, int height, int elementHeight, List<AlternativeOreDrop.OreRegister> ores, GuiAdjustOre parent) {
        super(mc, xCoord, yCoord, width, height, elementHeight);
        this.ores = ores;
        this.parent = parent;
        updateEntries();
    }

    public void updateEntries() {
        entries.clear();
        for(AlternativeOreDrop.OreRegister reg : ores)
            entries.add(new GuiAdjustOreScrollingEntry(reg, parent));
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick, int p_148144_3_, int p_148144_4_) {
        super.elementClicked(index, doubleClick, p_148144_3_, p_148144_4_);
        selectedId = index;
        ClientTickHandler.clientTicks = 45;
    }

    @Override
    protected boolean isSelected(int index) {
        return index == selectedId;
    }
}
