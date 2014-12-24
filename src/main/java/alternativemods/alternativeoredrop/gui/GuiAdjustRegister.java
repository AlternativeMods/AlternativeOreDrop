package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import cpw.mods.fml.client.GuiScrollingList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 03.02.14
 * Time: 18:41
 */
public class GuiAdjustRegister extends GuiScreen {

    private String identifiersText;
    private GuiButton back;
    private GuiScrollingList scrollingList;
    private Map<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMap = new HashMap<String, ArrayList<AlternativeOreDrop.OreRegister>>();
    private int selected = -1;

    protected static RenderItem itemRenderer = new RenderItem();

    public GuiAdjustRegister(String identifiers, Map<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMapJson){
        this.identifiersText = identifiers;
        this.oreMap = oreMapJson;
    }

    public void initGui(){
        this.back = new GuiButton(0, this.width / 2 - 126, this.height - 55, 253, 20, "Back");
        this.buttonList.add(this.back);

        this.scrollingList = new GuiScrollingList(mc, 253, 150, this.height - 185, this.height - 65, this.width / 2 - 126, 25) {
            @Override
            protected int getSize(){
                return oreMap.size();
            }

            @Override
            protected void elementClicked(int index, boolean doubleClick){
                selected = index;
                if(doubleClick)
                    doubleClickOn(index);
            }

            @Override
            public void drawScreen(int mX, int mY, float field){
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(width / 2 - 126, listHeight - 20, width + listWidth, height);
                super.drawScreen(mX, mY, field);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
            }

            @Override
            protected boolean isSelected(int index){
                return index == selected;
            }

            @Override
            protected void drawBackground(){

            }

            @Override
            protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5){
                Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry = getIndexOfValue(listIndex);
                if(entry == null)
                    return;

                int color = 0xFFFFFF;
                if(entry.getValue() == null || entry.getValue().isEmpty())
                    color = 0xFF0000;
                drawOre(entry, this.left, var3, color);
            }
        };

    }

    public void doubleClickOn(int index){
        Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry = getIndexOfValue(index);
        if(entry == null)
            return;
        if(entry.getValue() == null || entry.getValue().isEmpty())
            return;

        NetworkHandler.sendPacketToServer(new AODPacket.Server.AdjustOre(entry.getKey()));
    }

    public void drawOre(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry, int x, int y, int color){
        mc.fontRenderer.drawString(entry.getKey(), x + 40, y + 6, color);

        if(entry.getValue().isEmpty())
            return;

        AlternativeOreDrop.OreRegister reg = entry.getValue().get(0);
        renderItemStack(new ItemStack((Item) Item.itemRegistry.getObject(reg.modId + ":" + reg.itemName), 1, reg.damage), x + 8, y + 2);
    }

    public void renderItemStack(ItemStack stack, int x, int y){
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        RenderHelper.enableGUIStandardItemLighting();
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
        RenderHelper.enableStandardItemLighting();
    }

    public Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> getIndexOfValue(int key){
        int i = 0;
        for(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entr : oreMap.entrySet()){
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
            this.mc.displayGuiScreen(new GuiConfigScreen(identifiersText));
        }
    }

    protected void keyTyped(char par1, int par2){
        if(par2 == Keyboard.KEY_ESCAPE){
            this.mc.displayGuiScreen(new GuiConfigScreen(identifiersText));
        }
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();

        this.scrollingList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, "AlternativeOreDrop - Adjusting Register", this.width / 2, 40, 16777215);

        super.drawScreen(par1, par2, par3);
    }

}
