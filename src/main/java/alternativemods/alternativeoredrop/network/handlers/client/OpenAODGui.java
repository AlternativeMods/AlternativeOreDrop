package alternativemods.alternativeoredrop.network.handlers.client;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 11:45
 */
public class OpenAODGui extends SimpleChannelInboundHandler<AODPacket.Client.OpenAODGui> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Client.OpenAODGui msg) throws Exception {
        AlternativeOreDrop.proxy.openConfigGui(StringUtils.join(msg.identifiers, ","));
    }
}