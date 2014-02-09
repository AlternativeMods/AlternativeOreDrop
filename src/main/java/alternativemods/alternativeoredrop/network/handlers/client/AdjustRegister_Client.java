package alternativemods.alternativeoredrop.network.handlers.client;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 14:25
 */
public class AdjustRegister_Client extends SimpleChannelInboundHandler<AODPacket.Client.AdjustRegister> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Client.AdjustRegister msg) throws Exception {
        AlternativeOreDrop.proxy.openAdjustingGui(StringUtils.join(msg.identifiers), msg.returnList);
    }
}