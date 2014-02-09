package alternativemods.alternativeoredrop.network.handlers.client;

import alternativemods.alternativeoredrop.gui.GuiAdjustOre;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 14:48
 */
public class AdjustOre_Client extends SimpleChannelInboundHandler<AODPacket.Client.AdjustOre> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Client.AdjustOre msg) throws Exception {
        Minecraft.getMinecraft().displayGuiScreen(new GuiAdjustOre(msg.oreName, msg.oreMap));
    }
}