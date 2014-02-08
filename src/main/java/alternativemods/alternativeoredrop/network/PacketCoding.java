package alternativemods.alternativeoredrop.network;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Author: Lordmau5
 * Date: 08.02.14
 * Time: 11:47
 */
public class PacketCoding extends FMLIndexedMessageToMessageCodec<AODPacket> {

    public PacketCoding(){
        this.addDiscriminator(0, AODPacket.GuiOpen.class);
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, AODPacket msg, ByteBuf target) throws Exception{
        msg.encode(target);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AODPacket msg){
        msg.decode(source);
    }
}