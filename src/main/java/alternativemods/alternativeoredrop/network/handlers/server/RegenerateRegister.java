package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 14:56
 */
public class RegenerateRegister extends SimpleChannelInboundHandler<AODPacket.Server.RegenerateRegister> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.RegenerateRegister msg) throws Exception{
        AlternativeOreDrop.updateRegister();
    }
}
