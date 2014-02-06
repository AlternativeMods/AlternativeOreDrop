package alternativemods.alternativeoredrop.commands;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.PacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.apache.commons.lang3.StringUtils;

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
        PacketDispatcher.sendPacketToPlayer(PacketHandler.createIdPacket(1, new String[]{StringUtils.join(AlternativeOreDrop.identifiers, ",")}), (Player) sender);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}