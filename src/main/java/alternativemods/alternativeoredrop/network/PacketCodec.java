package alternativemods.alternativeoredrop.network;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 11:21
 */
public class PacketCodec extends FMLIndexedMessageToMessageCodec<AODPacket> {

    public PacketCodec(){
        this.addDiscriminator(0, AODPacket.Client.OpenAODGui.class);
        this.addDiscriminator(1, AODPacket.Client.AdjustRegister.class);
        this.addDiscriminator(2, AODPacket.Client.AdjustOre.class);

        this.addDiscriminator(3, AODPacket.Server.UpdateIdentifiers.class);
        this.addDiscriminator(4, AODPacket.Server.RegenerateRegister.class);
        this.addDiscriminator(5, AODPacket.Server.AdjustRegister.class);
        this.addDiscriminator(6, AODPacket.Server.AdjustOre.class);
        this.addDiscriminator(7, AODPacket.Server.PreferOre.class);
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