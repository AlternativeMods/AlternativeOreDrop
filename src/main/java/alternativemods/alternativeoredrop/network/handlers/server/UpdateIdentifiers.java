package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 14:52
 */
public class UpdateIdentifiers extends SimpleChannelInboundHandler<AODPacket.Server.UpdateIdentifiers> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.UpdateIdentifiers msg) throws Exception{
        AlternativeOreDrop.identifiers = msg.identifiers;
        AlternativeOreDrop.saveConfig();
    }
}
