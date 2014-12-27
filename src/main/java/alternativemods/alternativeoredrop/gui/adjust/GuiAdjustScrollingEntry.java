package alternativemods.alternativeoredrop.gui.adjust;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.gui.GuiAdjustRegister;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Random;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiAdjustScrollingEntry implements GuiListExtended.IGuiListEntry {

    private RenderBlocks renderBlocksRi = new RenderBlocks();
    private String oreName;
    private List<AlternativeOreDrop.OreRegister> ores;
    private GuiAdjustRegister parent;
    private boolean selected;

    public GuiAdjustScrollingEntry(String oreName, List<AlternativeOreDrop.OreRegister> ores, GuiAdjustRegister parent) {
        this.oreName = oreName;
        this.ores = ores;
        this.parent = parent;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, Tessellator tes, int mouseX, int mouseY, boolean isSelected) {
        FontRenderer fr = parent.mc.fontRenderer;
        parent.mc.fontRenderer.drawString(oreName, x + 40, y + 6, 0xFFFFFF);

        AlternativeOreDrop.OreRegister reg = ores.get(0);
        ItemStack is = new ItemStack((Item) Item.itemRegistry.getObject(reg.modId + ":" + reg.itemName), 1, reg.damage);
        if(selected)
            renderIn3D(is);
        else {
            RenderHelper.enableGUIStandardItemLighting();
            RenderHelper.disableStandardItemLighting();
            RenderItem.getInstance().renderItemIntoGUI(fr, parent.mc.renderEngine, is, x + 8, y + slotHeight / 2 - 8, true);
            RenderHelper.disableStandardItemLighting();
        }
    }

    private void renderIn3D(ItemStack itemstack) {
        Block block = Block.getBlockFromItem(itemstack.getItem());

        GL11.glPushMatrix();

        GL11.glRotatef(0, 0.0F, 1.0F, 0.0F);

        /*if (1 == 1)
        {
            GL11.glScalef(1.25F, 1.25F, 1.25F);
            GL11.glTranslatef(0.0F, 0.05F, 0.0F);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        }*/

        float f9 = 0.25F;
        int k = block.getRenderType();

        if (k == 1 || k == 19 || k == 12 || k == 2)
        {
            f9 = 0.5F;
        }

        if (block.getRenderBlockPass() > 0)
        {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        GL11.glScalef(f9, f9, f9);

        Random random = new Random();

        GL11.glPushMatrix();

        //float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
        //float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
        //float f8 = (random.nextFloat() * 2.0F - 1.0F) * 0.2F / f9;
        //GL11.glTranslatef(f6, f7, f8);

        this.renderBlocksRi.renderBlockAsItem(block, itemstack.getItemDamage(), 1.0F);
        GL11.glPopMatrix();

        if (block.getRenderBlockPass() > 0)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glPopMatrix();
    }

    @Override
    public boolean mousePressed(int id, int x, int y, int mouseEvent, int relX, int relY) {
        return false;
    }

    @Override
    public void mouseReleased(int id, int x, int y, int mouseEvent, int relX, int relY) {

    }
}
