package alternativemods.alternativeoredrop.network.handlers.server;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 15:17
 */
public class PreferOre_Server extends SimpleChannelInboundHandler<AODPacket.Server.PreferOre> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AODPacket.Server.PreferOre msg) throws Exception{
        AlternativeOreDrop.preferOre(msg.oreName, msg.reg, false);
        NetworkHandler.sendPacketToAllPlayers(new AODPacket.Client.PreferOre(msg.oreName, msg.reg, FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()));
        NetworkHandler.sendPacketToPlayer(new AODPacket.Client.AdjustRegister(), NetworkHandler.getPlayer(ctx));
    }
}
