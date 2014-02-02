package alternativemods.alternativeoredrop;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 20:07
 */
public class PacketHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player cpwPlayer) {
        EntityPlayer player = (EntityPlayer) cpwPlayer;
        if(!packet.channel.equals("AltOreDrop"))
            return;

        if(player.worldObj.isRemote)
            AlternativeOreDrop.proxy.openConfigGui();
        else {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

            int id;
            String additionalInformation;
            try {
                id = inputStream.readInt();
                additionalInformation = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            workServerPackage(id, additionalInformation);
        }
    }

    private void workServerPackage(int id, String additionalInformation) {
        if(id == 1) { // Update Identifiers
            AlternativeOreDrop.identifiers = additionalInformation.split(",");
            AlternativeOreDrop.saveConfig();
        }
    }

    public static Packet createIdentifiersUpdatePacket(String identifiers) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeInt(1);
            outputStream.writeUTF(identifiers);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "AltOreDrop";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        return packet;
    }

    public static Packet createGuiPacket() {
        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "AltOreDrop";
        return packet;
    }
}