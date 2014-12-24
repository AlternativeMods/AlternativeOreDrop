package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 14:57
 */
public class AdjustRegister_Server extends SimpleChannelInboundHandler<AODPacket.Server.AdjustRegister> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.AdjustRegister msg) throws Exception{
        NetworkHandler.sendPacketToPlayer(new AODPacket.Client.AdjustRegister(msg.identifiers, msg.returnList, msg.registerScrolled), NetworkHandler.getPlayer(ctx));
    }
}
