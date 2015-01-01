package alternativemods.alternativeoredrop.gui;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.events.ClientTickHandler;
import alternativemods.alternativeoredrop.gui.adjust.ore.GuiAdjustOreScrollingList;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Author: Lordmau5
 * Date: 06.02.14
 * Time: 13:42
 */
public class GuiAdjustOre extends GuiScreen {

    private RenderBlocks renderBlocksRi = new RenderBlocks();
    private List<AlternativeOreDrop.OreRegister> ores;
    private String oreName;
    private GuiButton back;
    private GuiButton prefer;
    private GuiAdjustOreScrollingList scrollingList;
    private int selected = 0;
    public float partialTicks;

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

        this.scrollingList = new GuiAdjustOreScrollingList(mc, this.width / 2 - 126, this. height / 2 - 20 , 251, 80, 15, ores, this) {
            @Override
            public void drawScreen(int mX, int mY, float field){
                super.drawScreen(mX, mY, field);
                drawSelected(xCoord, yCoord);
            }

            @Override
            protected void elementClicked(int index, boolean doubleClick, int p_148144_3_, int p_148144_4_) {
                super.elementClicked(index, doubleClick, p_148144_3_, p_148144_4_);
                selected = index;
            }

            /*@Override
            protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5){
                AlternativeOreDrop.OreRegister register = ores.get(listIndex);
                if(register == null)
                    return;

                int color = listIndex == 0 ? 0x00FF00 : 0xFFFFFF;
                drawRegister(register, this.left, var3, color);
            }*/
        };
    }

    private void drawSelected(int x, int y) {
        AlternativeOreDrop.OreRegister reg = ores.get(selected);
        ItemStack is = new ItemStack((Item) Item.itemRegistry.getObject(reg.modId + ":" + reg.itemName), 1, reg.damage);
        drawRect(x, y - 5, x + scrollingList.getListWidth(), y - 40, 0xAA000000);
        drawString(fontRendererObj, is.getDisplayName(), x + scrollingList.getListWidth() - fontRendererObj.getStringWidth(is.getDisplayName()) - 5, y - 26, 0xFFFFFFFF);

        RenderHelper.enableGUIStandardItemLighting();

        RenderHelper.disableStandardItemLighting();
        if(is == null || is.getItem() == null) {
            RenderHelper.enableGUIStandardItemLighting();
            return;
        }
        if(is.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(is.getItem());
            if(block.getRenderType() == 0)
                renderIn3D(block, is.getItemDamage(), x + 8, y - 30);
            else
                RenderItem.getInstance().renderItemIntoGUI(fontRendererObj, mc.renderEngine, is, x + 8, y - 30, true);
        }
        else
            RenderItem.getInstance().renderItemIntoGUI(fontRendererObj, mc.renderEngine, is, x + 8, y - 30, true);

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void renderIn3D(Block block, int damage, int x, int y) {

        GL11.glPushMatrix();
        //Code is pretty much copy-pasted from renderItemIntoGUI
        //You probably forgot the translation bit & the texture
        mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        if (block.getRenderBlockPass() != 0) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        } else {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glTranslatef(x - 2, y + 3, RenderItem.getInstance().zLevel - 3F);
        GL11.glScalef(10, 10, 10);
        GL11.glTranslatef(1F, 0.5F, 1F);
        GL11.glScalef(1, 1, -1);
        GL11.glRotatef(210, 1, 0, 0);

        //Alter the multiplier to change the speed
        float rotation = (ClientTickHandler.clientTicks + partialTicks) * 5;
        GL11.glRotatef(rotation, 0, 1, 0);

        GL11.glRotatef(-90, 0, 1, 0);
        this.renderBlocksRi.renderBlockAsItem(block, damage, 1);

        if (block.getRenderBlockPass() == 0) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        }

        GL11.glPopMatrix();
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
            Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustRegister(null));
        }
        if(button == this.prefer){
            AlternativeOreDrop.OreRegister selReg = ores.get(selected);
            if(selReg != null) {
                NetworkHandler.sendPacketToServer(new AODPacket.Server.PreferOre(oreName, selReg));
            }
        }
    }

    public boolean doesGuiPauseGame(){
        return false;
    }

    public void drawScreen(int par1, int par2, float par3){
        this.drawDefaultBackground();

        this.partialTicks = par3;
        this.scrollingList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, "AlternativeOreDrop - Adjusting " + oreName, this.width / 2, 40, 16777215);

        super.drawScreen(par1, par2, par3);
    }
}
