package alternativemods.alternativeoredrop.commands;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import alternativemods.alternativeoredrop.PacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
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
        return "aod <gui/updateRegister/identifiers/setIdentifiers>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] astring) {
        if(astring.length == 0) {
            sender.sendChatToPlayer(new ChatMessageComponent().addText("/aod <gui/regenerateRegister/identifiers/setIdentifiers>"));
            return;
        }

        if(astring[0].equals("gui")) {
            PacketDispatcher.sendPacketToPlayer(PacketHandler.createGuiPacket(), (Player) sender);
        }
        else if(astring[0].equals("regenerateRegister")) {
            AlternativeOreDrop.updateRegister();
            sender.sendChatToPlayer(new ChatMessageComponent().addText("Succesfully regenerated the register!"));
        }
        else if(astring[0].equals("identifiers")) {
            sender.sendChatToPlayer(new ChatMessageComponent().addText("Identifiers:"));
            sender.sendChatToPlayer(new ChatMessageComponent().addText(StringUtils.join(AlternativeOreDrop.identifiers, ",")));
        }
        else if(astring[0].equals("setIdentifiers")) {
            if(astring.length <= 1 || !astring[1].contains(",")) {
                ChatMessageComponent msg = new ChatMessageComponent();
                msg.addText("/aod setIdentifiers <identifiers>\n");
                msg.addText("* Separated by commas *");
                sender.sendChatToPlayer(msg);
                return;
            }
            AlternativeOreDrop.identifiers = astring[1].split(",");
            AlternativeOreDrop.saveConfig();
            sender.sendChatToPlayer(new ChatMessageComponent().addText("Identifiers set to:"));
            sender.sendChatToPlayer(new ChatMessageComponent().addText(astring[1]));
        }
        else
            sender.sendChatToPlayer(new ChatMessageComponent().addText("/aod <gui/updateRegister/identifiers/setIdentifiers>"));
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}