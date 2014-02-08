package alternativemods.alternativeoredrop.network.handlers;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: Lordmau5
 * Date: 08.02.14
 * Time: 11:58
 */
public class GUIOpenHandler extends SimpleChannelInboundHandler<AODPacket.GuiOpen> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.GuiOpen msg) throws Exception{
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            processClient(msg);
        else
            processServer(msg, NetworkHandler.getPlayer(ctx));
    }

    private void processClient(AODPacket.GuiOpen msg) {
        if(msg.id == 1) { // Open the GUI
            AlternativeOreDrop.proxy.openConfigGui(msg.additionalInformation[0]);
        }
        else if(msg.id == 2) { // Adjusting register screen
            AlternativeOreDrop.proxy.openAdjustingGui(msg.additionalInformation[0], msg.additionalInformation[1]);
        }
        else if(msg.id == 3) {
            AlternativeOreDrop.proxy.openAdjustingOreGui(msg.additionalInformation[0], msg.additionalInformation[1]);
        }
    }

    private void processServer(AODPacket.GuiOpen msg, EntityPlayerMP player) {
        if(msg.id == 1) { // Update Identifiers
            AlternativeOreDrop.identifiers = msg.additionalInformation[0].split(",");
            AlternativeOreDrop.saveConfig();
        }
        else if(msg.id == 2) { // Regenerate register
            AlternativeOreDrop.updateRegister();
        }
        else if(msg.id == 3) { // Adjusting register screen
            NetworkHandler.sendPacketToPlayer(NetworkHandler.getProxyPacket(new AODPacket.GuiOpen(2, new String[]{StringUtils.join(AlternativeOreDrop.identifiers), new GsonBuilder().setPrettyPrinting().create().toJson(AlternativeOreDrop.oreMap)})), player);
        }
        else if(msg.id == 4) { // Adjusting specific Ore screen
            NetworkHandler.sendPacketToPlayer(NetworkHandler.getProxyPacket(new AODPacket.GuiOpen(3, new String[]{msg.additionalInformation[0], new GsonBuilder().setPrettyPrinting().create().toJson(AlternativeOreDrop.oreMap)})), player);
        }
        else if(msg.id == 5) { // Getting new preferred ore
            AlternativeOreDrop.preferOre(msg.additionalInformation[0], msg.additionalInformation[1]);
        }
    }
}