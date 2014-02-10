package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 15:17
 */
public class PreferOre extends SimpleChannelInboundHandler<AODPacket.Server.PreferOre> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.PreferOre msg) throws Exception {
        AlternativeOreDrop.preferOre(msg.oreName, msg.reg);
    }
}