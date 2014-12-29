package alternativemods.alternativeoredrop.network.handlers.client;

import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.variables.ClientVars;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Du5tin on 29.12.2014.
 */
public class SendServerIdentifiers_Client extends SimpleChannelInboundHandler<AODPacket.Client.SendServerIdentifiers> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Client.SendServerIdentifiers msg) throws Exception{
        ClientVars.serverIdentifiers = StringUtils.join(msg.identifiers, ",");
    }
}