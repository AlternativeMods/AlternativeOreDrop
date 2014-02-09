package alternativemods.alternativeoredrop.network;

import alternativemods.alternativeoredrop.network.handlers.client.AdjustOre_Client;
import alternativemods.alternativeoredrop.network.handlers.client.AdjustRegister_Client;
import alternativemods.alternativeoredrop.network.handlers.client.OpenAODGui;
import alternativemods.alternativeoredrop.network.handlers.server.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;

import java.util.EnumMap;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 11:17
 */
public class NetworkHandler {
    private static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static void registerChannels(Side side){
        channels = NetworkRegistry.INSTANCE.newChannel("AltOreDrop", new PacketCodec());

        ChannelPipeline pipeline = channels.get(Side.SERVER).pipeline();
        String targetName = channels.get(Side.SERVER).findChannelHandlerNameForType(PacketCodec.class);

        pipeline.addAfter(targetName, "UpdateIdentifies", new UpdateIdentifiers());
        pipeline.addAfter(targetName, "RegenerateRegister", new RegenerateRegister());
        pipeline.addAfter(targetName, "AdjustRegister_Server", new AdjustRegister_Server());
        pipeline.addAfter(targetName, "AdjustOre_Server", new AdjustOre_Server());
        pipeline.addAfter(targetName, "PreferOre", new PreferOre());

        if(side.isClient()){
            registerClientHandlers();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerClientHandlers(){
        ChannelPipeline pipeline = channels.get(Side.CLIENT).pipeline();
        String targetName = channels.get(Side.CLIENT).findChannelHandlerNameForType(PacketCodec.class);

        pipeline.addAfter(targetName, "OpenAODGui", new OpenAODGui());
        pipeline.addAfter(targetName, "AdjustRegister_Client", new AdjustRegister_Client());
        pipeline.addAfter(targetName, "AdjustOre_Client", new AdjustOre_Client());
    }

    public static Packet getProxyPacket(AODPacket packet){
        return channels.get(FMLCommonHandler.instance().getEffectiveSide()).generatePacketFrom(packet);
    }

    public static void sendPacketToPlayer(AODPacket packet, EntityPlayer player){
        FMLEmbeddedChannel channel = channels.get(Side.SERVER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channel.writeOutbound(packet);
    }

    public static void sendPacketToServer(AODPacket packet){
        FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channel.writeOutbound(packet);
    }

    public static EntityPlayerMP getPlayer(ChannelHandlerContext ctx){
        return ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
    }
}