package alternativemods.alternativeoredrop.network.handlers.client;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Du5tin on 25.12.2014.
 */
public class PreferOre_Client extends SimpleChannelInboundHandler<AODPacket.Client.PreferOre> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Client.PreferOre msg) throws Exception{
        AlternativeOreDrop.preferOre(msg.oreName, msg.reg, msg.isDedicated);
    }
}
