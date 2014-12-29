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

    int lastDiscriminator = 0;

    public PacketCodec(){
        addPacket(AODPacket.Client.OpenAODGui.class);
        addPacket(AODPacket.Client.AdjustRegister.class);
        addPacket(AODPacket.Client.AdjustOre.class);
        addPacket(AODPacket.Client.PreferOre.class);
        addPacket(AODPacket.Client.SendServerIdentifiers.class);

        addPacket(AODPacket.Server.UpdateIdentifiers.class);
        addPacket(AODPacket.Server.RegenerateRegister.class);
        addPacket(AODPacket.Server.AdjustRegister.class);
        addPacket(AODPacket.Server.AdjustOre.class);
        addPacket(AODPacket.Server.PreferOre.class);
    }

    void addPacket(Class<? extends AODPacket> type) {
        this.addDiscriminator(lastDiscriminator, type);
        lastDiscriminator++;
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
