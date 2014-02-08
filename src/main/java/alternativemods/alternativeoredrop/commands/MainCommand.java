package alternativemods.alternativeoredrop.commands;

import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Author: Lordmau5
 * Date: 02.02.14
 * Time: 17:22
 */
public class MainCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "aod";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "aod";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] astring) {
        NetworkHandler.sendPacketToPlayer(NetworkHandler.getProxyPacket(new AODPacket.GuiOpen(1, new String[]{"NONE"})), (EntityPlayer) sender);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}