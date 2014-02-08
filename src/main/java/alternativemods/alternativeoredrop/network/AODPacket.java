package alternativemods.alternativeoredrop.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

/**
 * Author: Lordmau5
 * Date: 08.02.14
 * Time: 11:41
 */
public abstract class AODPacket {
    public abstract void encode(ByteBuf buffer);
    public abstract void decode(ByteBuf buffer);

    public static class GuiOpen extends AODPacket {

        public int id;
        public String[] additionalInformation;

        public GuiOpen(int id, String[] additionalInformation) {
            this.id = id;
            this.additionalInformation = additionalInformation;
        }

        @Override
        public void encode(ByteBuf buffer){
            buffer.writeInt(this.id);
            buffer.writeInt(this.additionalInformation.length);
            for(int i=0; i<this.additionalInformation.length; i++)
                ByteBufUtils.writeUTF8String(buffer, this.additionalInformation[i]);
        }

        @Override
        public void decode(ByteBuf buffer){
            this.id = buffer.readInt();
            int additionalLength = buffer.readInt();
            this.additionalInformation = new String[additionalLength];
            for(int i=0; i<additionalLength; i++)
                this.additionalInformation[i] = ByteBufUtils.readUTF8String(buffer);
        }
    }
}