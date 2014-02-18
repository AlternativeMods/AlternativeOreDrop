package alternativemods.alternativeoredrop.commands;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.network.AODPacket;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
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
        if(sender instanceof EntityPlayer){
            NetworkHandler.sendPacketToPlayer(new AODPacket.Client.OpenAODGui(AlternativeOreDrop.identifiers), (EntityPlayer) sender);
        }else{
            throw new CommandException("This command can only be used by players");
        }
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ICommand){
            return ((ICommand) o).getCommandName().compareTo(this.getCommandName());
        }
        return 0;
    }
}
