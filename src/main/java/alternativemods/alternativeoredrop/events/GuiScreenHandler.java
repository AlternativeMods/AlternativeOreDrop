package alternativemods.alternativeoredrop.events;

import alternativemods.alternativeoredrop.gui.GuiConfigScreen;
import cpw.mods.fml.client.GuiIngameModOptions;
import cpw.mods.fml.client.GuiModOptionList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

/**
 * Created by Du5tin on 27.12.2014.
 */
public class GuiScreenHandler {

    private RenderBlocks renderBlocksRi = new RenderBlocks();

    private GuiButton aodButton;

    public GuiScreenHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void InitGuiEvent(GuiScreenEvent.InitGuiEvent event) {
        if(event.gui instanceof GuiIngameModOptions) {
            GuiIngameModOptions modOptions = (GuiIngameModOptions) event.gui;
            Class<?> c = GuiIngameModOptions.class;
            try {
                Field optionsList = c.getDeclaredField("optionList");
                optionsList.setAccessible(true);
                optionsList.set(modOptions, new GuiModOptionList(modOptions) {
                    @Override
                    protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5) {
                        // Nothing!
                    }

                    @Override
                    public void drawScreen(int mouseX, int mouseY, float p_22243_3_) {
                        // Nothing!
                    }
                });

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            aodButton = new GuiButton(event.buttonList.size() + 1, event.gui.width - 45, 5, 40, 20, "AOD");
            event.buttonList.add(aodButton);
            ClientTickHandler.clientTicks = 45;
        }
    }

    @SubscribeEvent
    public void drawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
        if(event.gui instanceof GuiIngameModOptions) {
            ItemStack demoItem = new ItemStack(Blocks.diamond_block, 1, 0);
            Block block = Block.getBlockFromItem(demoItem.getItem());
            renderIn3D(block, 0, event.gui.width - 65, 7, event.renderPartialTicks);
        }
    }

    private void renderIn3D(Block block, int damage, int x, int y, float partialTicks) {

        GL11.glPushMatrix();
        //Code is pretty much copy-pasted from renderItemIntoGUI
        //You probably forgot the translation bit & the texture
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

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
        renderBlocksRi.renderBlockAsItem(block, damage, 1);

        if (block.getRenderBlockPass() == 0) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        }

        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void actionPerformedEvent(GuiScreenEvent.ActionPerformedEvent event) {
        if(event.button == aodButton) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiConfigScreen());
        }
    }

}
