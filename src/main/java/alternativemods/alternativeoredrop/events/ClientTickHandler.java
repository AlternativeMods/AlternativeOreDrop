package alternativemods.alternativeoredrop.events;

import cpw.mods.fml.client.GuiIngameModOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ClientTickHandler {

    public ClientTickHandler() {
        FMLCommonHandler.instance().bus().register(this);
    }

    public static int clientTicks;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == Phase.END) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;
            if (gui == null || !gui.doesGuiPauseGame() || gui instanceof GuiIngameModOptions)
                clientTicks++;
        }
    }
}
