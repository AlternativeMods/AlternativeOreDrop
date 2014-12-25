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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Author: Lordmau5
 * Date: 06.02.14
 * Time: 13:42
 */
public class GuiAdjustOre extends GuiScreen {

    private List<AlternativeOreDrop.OreRegister> ores;
    private String oreName;
    private GuiButton back;
    private GuiButton prefer;
    private GuiScrollingList scrollingList;
    private int selected = 0;

    public GuiAdjustOre(String oreName, List<AlternativeOreDrop.OreRegister> oreMap){
        this.oreName = oreName;
        this.ores = oreMap;
    }

    public void drawHover(String text, int mX, int mY){
        drawCreativeTabHoveringText(text, mX, mY);
    }

    public void initGui(){
        this.back = new GuiButton(0, this.width / 2 + 6, this.height - 55, 120, 20, "Back");
        this.buttonList.add(this.back);
        this.prefer = new GuiButton(1, this.width / 2 - 126, this.height - 55, 120, 20, "Prefer");
        this.buttonList.add(this.prefer);

        this.scrollingList = new GuiScrollingList(mc, 253, 100, this.height - 145, this.height - 65, this.width / 2 - 126, 15) {
            @Override
            protected int getSize(){
                return ores.size();
            }

            @Override
            protected void elementClicked(int index, boolean doubleClick){
                selected = index;
            }

            public void drawSelected(){
                if(selected != -1 && !ores.isEmpty())
                    renderItemStack(ores.get(selected), this.left, this.top - 25, this.listWidth);
            }

            @Override
            public void drawScreen(int mX, int mY, float field){
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glScissor(width / 2 - 126, listHeight - 20, width + listWidth, height);
                super.drawScreen(mX, mY, field);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                drawSelected();
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
                AlternativeOreDrop.OreRegister register = ores.get(listIndex);
                if(register == null)
                    return;

                int color = listIndex == 0 ? 0x00FF00 : 0xFFFFFF;
                drawRegister(register, this.left, var3, color);
            }
        };
    }

    public void renderItemStack(AlternativeOreDrop.OreRegister stack, int x, int y, int width){
        drawRect(x, y - 5, x + width, y + 20, 0xAA000000);

        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        RenderHelper.enableGUIStandardItemLighting();
        RenderHelper.disableStandardItemLighting();
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        ItemStack is = null;
        FontRenderer font = null;
        if(stack != null){
            if(!Item.itemRegistry.containsKey(stack.modId + ":" + stack.itemName))
                return;
            Item tmpItem = (Item) Item.itemRegistry.getObject(stack.modId + ":" + stack.itemName);
            is = new ItemStack(tmpItem, 1, stack.damage);
            if(is.getItem() == null)
                return;
            if(tmpItem != null && tmpItem.getFontRenderer(is) != null)
                font = tmpItem.getFontRenderer(is);
        }
        if(font == null){
            font = fontRendererObj;
        }
        itemRender.renderItemAndEffectIntoGUI(font, this.mc.getTextureManager(), is, x + 5, y);
        itemRender.renderItemOverlayIntoGUI(font, this.mc.getTextureManager(), is, x + 5, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        RenderHelper.enableGUIStandardItemLighting();

        if(is == null) return;
        drawString(fontRendererObj, is.getDisplayName(), x + width - fontRendererObj.getStringWidth(is.getDisplayName()) - 5, y + 4, 0xFFFFFFFF);
    }

    private void drawRegister(AlternativeOreDrop.OreRegister register, int x, int y, int color){
        mc.fontRenderer.drawString(register.modId + ": " + register.itemName, x + 3, y + 2, color);
    }

    protected void actionPerformed(GuiButton button){
        if(!button.enabled)
            return;

        if(button == this.back){
            NetworkHandler.sendPacketToServer(new AODPacket.Server.AdjustRegister());
        }
        if(button == this.prefer){
            AlternativeOreDrop.OreRegister selReg = ores.get(selected);
            if(selReg != null){
                NetworkHandler.sendPacketToServer(new AODPacket.Server.PreferOre(oreName, selReg));
            }
            NetworkHandler.sendPacketToServer(new AODPacket.Server.AdjustRegister());
        }
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();

        this.scrollingList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, "AlternativeOreDrop - Adjusting " + oreName, this.width / 2, 40, 16777215);

        super.drawScreen(par1, par2, par3);
    }
}
