package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.adjust.register.GuiAdjustRegisterScrollingList;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import alternativemods.alternativeoredrop.variables.ClientVars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Author: Lordmau5
 * Date: 03.02.14
 * Time: 18:41
 */
public class GuiAdjustRegister extends GuiScreen {

    private GuiButton back;
    private GuiAdjustRegisterScrollingList scrollingList;
    private GuiTextField search;
    public float partialTicks;

    protected static RenderItem itemRenderer = new RenderItem();

    public GuiAdjustRegister(TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMapJson){
        if(oreMapJson != null)
            ClientVars.oreMap = oreMapJson;
        ClientVars.sortMap = (TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>>) ClientVars.oreMap.clone();
    }

    @Override
    public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_) {
        if(search != null)
            ClientVars.registerSearch = search.getText();
        super.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
    }

    public void initGui(){
        this.back = new GuiButton(0, this.width / 2 - 126, this.height - 25, 253, 20, "Back");
        this.buttonList.add(this.back);

        this.search = new GuiTextField(fontRendererObj, this.width / 2 - 124, this.height - 55, 249, 20);
        this.search.setText(ClientVars.registerSearch);

        int topPos = 60;
        int height = this.height - topPos - 8;

        this.scrollingList = new GuiAdjustRegisterScrollingList(mc, this.width / 2 - 124, 60, 250, height - topPos, 25, this) {

            @Override
            protected void elementClicked(int index, boolean doubleClick, int p_148144_3_, int p_148144_4_){
                super.elementClicked(index, doubleClick, p_148144_3_, p_148144_4_);
                if(doubleClick)
                    doubleClickOn(index);
            }
        };

        Class<?> c = GuiSlot.class;
        try {
            Field scrolled = c.getDeclaredField("amountScrolled");
            scrolled.setAccessible(true);
            scrolled.setFloat(scrollingList, ClientVars.scrollDistance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        updateSortMap();
    }

    public void doubleClickOn(int index){
        Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry = getIndexOfValue(index);
        if(entry == null)
            return;
        if(entry.getValue() == null || entry.getValue().isEmpty())
            return;

        Class<?> c = GuiSlot.class;
        try {
            Field scrolled = c.getDeclaredField("amountScrolled");
            scrolled.setAccessible(true);
            ClientVars.scrollDistance = scrolled.getFloat(scrollingList);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ClientVars.registerSearch = search.getText();
        NetworkHandler.sendPacketToServer(new AODPacket.Server.AdjustOre(entry));
    }

    public Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> getIndexOfValue(int key){
        int i = 0;
        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entr : ClientVars.sortMap.entrySet()){
            if(i == key){
                return entr;
            }
            i++;
        }
        return null;
    }

    protected void actionPerformed(GuiButton button){
        if(!button.enabled)
            return;

        if(button == this.back){
            this.mc.displayGuiScreen(new GuiConfigScreen(ClientVars.serverIdentifiers));
        }
    }

    protected void keyTyped(char par1, int par2){
        if(par2 == Keyboard.KEY_ESCAPE){
            this.mc.displayGuiScreen(new GuiConfigScreen(ClientVars.serverIdentifiers));
        }
        search.textboxKeyTyped(par1, par2);
        updateSortMap();
    }

    private void updateSortMap() {
        ClientVars.registerSearch = search.getText();
        ClientVars.sortMap.clear();

        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> ent : ClientVars.oreMap.entrySet())
            if (ent.getKey().toLowerCase().contains(ClientVars.registerSearch.toLowerCase()))
                ClientVars.sortMap.put(ent.getKey(), ent.getValue());

        scrollingList.updateEntries();
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();
        
        this.partialTicks = par3;
        this.scrollingList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, "AlternativeOreDrop - Adjusting Register", this.width / 2, 40, 16777215);
        this.search.drawTextBox();
        
        super.drawScreen(par1, par2, par3);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        this.search.mouseClicked(x, y, button);
        if(button != 0 || !scrollingList.mouseClicked(x, y, button))
            super.mouseClicked(x, y, button);
    }
}
