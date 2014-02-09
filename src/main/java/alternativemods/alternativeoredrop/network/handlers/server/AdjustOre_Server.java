package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 15:09
 */
public class AdjustOre_Server extends SimpleChannelInboundHandler<AODPacket.Server.AdjustOre> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.AdjustOre msg) throws Exception {
        NetworkHandler.sendPacketToPlayer(new AODPacket.Client.AdjustOre(msg.oreName, msg.oreMap), NetworkHandler.getPlayer(ctx));
    }
}