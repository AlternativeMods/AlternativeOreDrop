package alternativemods.alternativeoredrop;

import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import org.apache.commons.lang3.StringUtils;

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

        if(player.worldObj.isRemote) {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

            int id;
            int addiInfoLength;
            String[] additionalInformation;
            try {
                id = inputStream.readInt();
                addiInfoLength = inputStream.readInt();
                additionalInformation = new String[addiInfoLength];
                for(int i=0; i<addiInfoLength; i++)
                    additionalInformation[i] = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            workClientPackage(id, additionalInformation);
        }
        else {
            DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));

            int id;
            int addiInfoLength;
            String[] additionalInformation;
            try {
                id = inputStream.readInt();
                addiInfoLength = inputStream.readInt();
                additionalInformation = new String[addiInfoLength];
                for(int i=0; i<addiInfoLength; i++)
                    additionalInformation[i] = inputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            workServerPackage(cpwPlayer, id, additionalInformation);
        }
    }

    private void workClientPackage(int id, String[] additionalInformation) {
        if(id == 1) { // Open the GUI
            AlternativeOreDrop.proxy.openConfigGui(additionalInformation[0]);
        }
        else if(id == 2) { // Adjusting register screen
            AlternativeOreDrop.proxy.openAdjustingGui(additionalInformation[0], additionalInformation[1]);
        }
        else if(id == 3) {
            AlternativeOreDrop.proxy.openAdjustingOreGui(additionalInformation[0], additionalInformation[1]);
        }
    }

    private void workServerPackage(Player player, int id, String[] additionalInformation) {
        if(id == 1) { // Update Identifiers
            AlternativeOreDrop.identifiers = additionalInformation[0].split(",");
            AlternativeOreDrop.saveConfig();
        }
        else if(id == 2) { // Regenerate register
            AlternativeOreDrop.updateRegister();
        }
        else if(id == 3) { // Adjusting register screen
            PacketDispatcher.sendPacketToPlayer(createIdPacket(2, new String[]{StringUtils.join(AlternativeOreDrop.identifiers), new GsonBuilder().setPrettyPrinting().create().toJson(AlternativeOreDrop.oreMap)}), player);
        }
        else if(id == 4) { // Adjusting specific Ore screen
            PacketDispatcher.sendPacketToPlayer(createIdPacket(3, new String[]{additionalInformation[0], new GsonBuilder().setPrettyPrinting().create().toJson(AlternativeOreDrop.oreMap)}), player);
        }
        else if(id == 5) { // Getting new preferred ore
            AlternativeOreDrop.preferOre(additionalInformation[0], additionalInformation[1]);
        }
    }

    public static Packet createIdPacket(int id, String[] additionalInformation) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try {
            outputStream.writeInt(id);
            outputStream.writeInt(additionalInformation.length);
            for(String str : additionalInformation)
                outputStream.writeUTF(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "AltOreDrop";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        return packet;
    }
}