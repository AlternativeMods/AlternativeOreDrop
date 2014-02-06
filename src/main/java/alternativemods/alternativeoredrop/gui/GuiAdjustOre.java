package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.PacketHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 06.02.14
 * Time: 13:42
 */
public class GuiAdjustOre extends GuiScreen {

    private ArrayList<AlternativeOreDrop.OreRegister> ores;
    private String oreName;
    private GuiButton back;
    private GuiButton prefer;
    private GuiScrollingList scrollingList;
    private int selected;

    public GuiAdjustOre(String oreName, String oreMapJson) {
        this.oreName = oreName;
        Gson gson = new Gson();
        Type strOreRegMap = new TypeToken<Map<String, ArrayList<AlternativeOreDrop.OreRegister>>>(){}.getType();
        Map<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMap = gson.fromJson(oreMapJson, strOreRegMap);
        ores = AlternativeOreDrop.getOresForName(oreMap, oreName);
    }

    public void initGui() {
        this.back = new GuiButton(0, this.width / 2 + 6, this.height - 55, 120, 20, "Back");
        this.buttonList.add(this.back);
        this.prefer = new GuiButton(1, this.width / 2 - 126, this.height - 55, 120, 20, "Prefer");
        this.buttonList.add(this.prefer);

        this.scrollingList = new GuiScrollingList(mc, 253, 150, this.height - 185, this.height - 65, this.width / 2 - 126, 15) {
            @Override
            protected int getSize() {
                return ores.size();
            }

            @Override
            protected void elementClicked(int index, boolean doubleClick) {
                selected = index;
            }

            @Override
            public void drawScreen(int mX, int mY, float field) {
                super.drawScreen(mX, mY, field);
            }

            @Override
            protected boolean isSelected(int index) {
                return index == selected;
            }

            @Override
            protected void drawBackground() {

            }

            @Override
            protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
                AlternativeOreDrop.OreRegister register = ores.get(listIndex);
                if(register == null)
                    return;

                drawRegister(register, this.left, var3, 0xFFFFFF);
            }
        };
    }

    private void drawRegister(AlternativeOreDrop.OreRegister register, int x, int y, int color) {
        mc.fontRenderer.drawString(register.modId, x + 3, y + 2, color);
    }

    protected void actionPerformed(GuiButton button)
    {
        if(!button.enabled)
            return;

        if(button == this.back) {
            PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(3, new String[]{"NONE"}));
        }
        if(button == this.prefer) {
            AlternativeOreDrop.OreRegister selReg = ores.get(selected);
            if(selReg != null) {
                PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(5, new String[]{oreName, selReg.modId}));
            }
            PacketDispatcher.sendPacketToServer(PacketHandler.createIdPacket(3, new String[]{"NONE"}));
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();

        this.scrollingList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, "AlternativeOreDrop - Adjusting " + oreName, this.width / 2, 40, 16777215);

        super.drawScreen(par1, par2, par3);
    }
}