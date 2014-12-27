package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.adjust.GuiAdjustScrollingList;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import alternativemods.alternativeoredrop.variables.ClientVars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
    private GuiAdjustScrollingList scrollingList;
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

        this.scrollingList = new GuiAdjustScrollingList(mc, this.width / 2 - 124, 60, 249, height - topPos, 25, this) {

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

    public void drawOre(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry, int listIndex, int x, int y, int color){
        mc.fontRenderer.drawString(entry.getKey(), x + 40, y + 6, color);

        if(entry.getValue().isEmpty())
            return;

        AlternativeOreDrop.OreRegister reg = entry.getValue().get(0);
        renderItemStack(new ItemStack((Item) Item.itemRegistry.getObject(reg.modId + ":" + reg.itemName), 1, reg.damage), listIndex, x + 8, y + 2);
    }

    public void renderItemStack(ItemStack stack, int listIndex, int x, int y){
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        RenderHelper.disableStandardItemLighting();
        this.zLevel = 200.0F;
        itemRenderer.zLevel = 200.0F;
        FontRenderer font = null;
        if(stack != null && stack.getItem() != null){
            font = stack.getItem().getFontRenderer(stack);
        }
        if(font == null){
            font = fontRendererObj;
        }
        itemRenderer.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), stack, x, y);
        itemRenderer.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRenderer.zLevel = 0.0F;
        RenderHelper.enableGUIStandardItemLighting();
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
        typeTextbox(par1, par2);
    }

    private void typeTextbox(char par1, int par2) {
        this.search.textboxKeyTyped(par1, par2);

        updateSortMap();
    }

    private void updateSortMap() {
        ClientVars.sortMap.clear();

        if(this.search.getText().isEmpty()) {
            ClientVars.sortMap = (TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>>) ClientVars.oreMap.clone();
            return;
        }

        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> ent : ClientVars.oreMap.entrySet())
            if (ent.getKey().toLowerCase().contains(this.search.getText().toLowerCase()))
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
