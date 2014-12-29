package alternativemods.alternativeoredrop.network;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.*;

/**
 * Author: Lordmau5
 * Date: 09.02.14
 * Time: 11:18
 */
public abstract class AODPacket {
    public abstract void encode(ByteBuf buffer);

    public abstract void decode(ByteBuf buffer);

    public static abstract class Client {
        public static class OpenAODGui extends AODPacket {
            public String[] identifiers;

            public OpenAODGui(){
            }

            public OpenAODGui(String[] identifiers){
                this.identifiers = identifiers;
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(this.identifiers.length);
                for(String id : this.identifiers)
                    ByteBufUtils.writeUTF8String(buffer, id);
            }

            @Override
            public void decode(ByteBuf buffer){
                this.identifiers = new String[buffer.readInt()];
                for(int i = 0; i < this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
            }
        }

        public static class AdjustRegister extends AODPacket {
            public int size = 0;
            public TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>> returnList;

            public AdjustRegister(){
            }

            public AdjustRegister(TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>> oreMap){
                this.size = oreMap.size();
                this.returnList = oreMap;
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(this.size);
                if(returnList != null) {
                    for (Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry : this.returnList.entrySet()) {
                        ByteBufUtils.writeUTF8String(buffer, entry.getKey());
                        buffer.writeInt(entry.getValue().size());
                        for (AlternativeOreDrop.OreRegister reg : entry.getValue()) {
                            ByteBufUtils.writeUTF8String(buffer, reg.itemName);
                            buffer.writeInt(reg.damage);
                            ByteBufUtils.writeUTF8String(buffer, reg.modId);
                        }
                    }
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                this.size = buffer.readInt();
                if(size == 0)
                    return;

                returnList = new TreeMap<String, ArrayList<AlternativeOreDrop.OreRegister>>();

                for(int i = 0; i < this.size; i++) {
                    String map = ByteBufUtils.readUTF8String(buffer);
                    int arraySize = buffer.readInt();
                    ArrayList<AlternativeOreDrop.OreRegister> oreReg = new ArrayList<AlternativeOreDrop.OreRegister>();
                    for(int x = 0; x < arraySize; x++) {
                        oreReg.add(new AlternativeOreDrop.OreRegister(ByteBufUtils.readUTF8String(buffer), buffer.readInt(), ByteBufUtils.readUTF8String(buffer)));
                    }
                    returnList.put(map, oreReg);
                }
            }
        }

        public static class AdjustOre extends AODPacket {
            public String oreName;
            public List<AlternativeOreDrop.OreRegister> oreMap = new ArrayList<AlternativeOreDrop.OreRegister>();

            public AdjustOre(){
            }

            public AdjustOre(String oreName, List<AlternativeOreDrop.OreRegister> oreMap){
                this.oreName = oreName;
                this.oreMap = oreMap;
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, this.oreName);
                buffer.writeInt(this.oreMap.size());
                for(AlternativeOreDrop.OreRegister reg : oreMap){
                    ByteBufUtils.writeUTF8String(buffer, reg.itemName);
                    buffer.writeInt(reg.damage);
                    ByteBufUtils.writeUTF8String(buffer, reg.modId);
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                this.oreName = ByteBufUtils.readUTF8String(buffer);
                int size = buffer.readInt();

                for(int i = 0; i < size; i++){
                    String itemName = ByteBufUtils.readUTF8String(buffer);
                    int damage = buffer.readInt();
                    String modId = ByteBufUtils.readUTF8String(buffer);
                    this.oreMap.add(new AlternativeOreDrop.OreRegister(itemName, damage, modId));
                }
            }
        }

        public static class PreferOre extends AODPacket {
            public String oreName;
            private String modId;
            private String itemName;
            private int damage;
            public boolean isDedicated;

            public AlternativeOreDrop.OreRegister reg;

            public PreferOre(){
            }

            public PreferOre(String oreName, AlternativeOreDrop.OreRegister reg, boolean isDedicated){
                this.oreName = oreName;
                this.modId = reg.modId;
                this.itemName = reg.itemName;
                this.damage = reg.damage;
                this.isDedicated  = isDedicated;
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, oreName);
                ByteBufUtils.writeUTF8String(buffer, itemName);
                buffer.writeInt(damage);
                ByteBufUtils.writeUTF8String(buffer, modId);
                buffer.writeBoolean(isDedicated);
            }

            @Override
            public void decode(ByteBuf buffer){
                oreName = ByteBufUtils.readUTF8String(buffer);
                reg = new AlternativeOreDrop.OreRegister(ByteBufUtils.readUTF8String(buffer), buffer.readInt(), ByteBufUtils.readUTF8String(buffer));
                isDedicated = buffer.readBoolean();
            }
        }

        public static class SendServerIdentifiers extends AODPacket {
            public String[] identifiers;

            public SendServerIdentifiers(){
            }

            public SendServerIdentifiers(String[] identifiers){
                this.identifiers = identifiers;
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(identifiers.length);
                for(int i=0; i<identifiers.length; i++)
                    ByteBufUtils.writeUTF8String(buffer, identifiers[i]);
            }

            @Override
            public void decode(ByteBuf buffer){
                int size = buffer.readInt();
                identifiers = new String[size];
                for(int i=0; i<size; i++)
                    identifiers[i] = ByteBufUtils.readUTF8String(buffer);
            }
        }
    }

    public static class Server {
        public static class UpdateIdentifiers extends AODPacket {
            public String[] identifiers;

            public UpdateIdentifiers(){
            }

            public UpdateIdentifiers(String identifiers){
                this.identifiers = identifiers.split(",");
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(this.identifiers.length);
                for(String id : this.identifiers)
                    ByteBufUtils.writeUTF8String(buffer, id);
            }

            @Override
            public void decode(ByteBuf buffer){
                this.identifiers = new String[buffer.readInt()];
                for(int i = 0; i < this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
            }
        }

        public static class RegenerateRegister extends AODPacket {
            public RegenerateRegister(){
            }

            @Override
            public void encode(ByteBuf buffer){
            }

            @Override
            public void decode(ByteBuf buffer){
            }
        }

        public static class AdjustRegister extends AODPacket {
            public boolean init = false;

            public AdjustRegister(){}

            public AdjustRegister(boolean init){
                this.init = init;
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeBoolean(init);
            }

            @Override
            public void decode(ByteBuf buffer){
                init = buffer.readBoolean();
            }
        }

        public static class AdjustOre extends AODPacket {
            public String oreName;
            public List<AlternativeOreDrop.OreRegister> oreMap = new ArrayList<AlternativeOreDrop.OreRegister>();

            public AdjustOre(){
            }

            public AdjustOre(Map.Entry<String, ArrayList<AlternativeOreDrop.OreRegister>> entry){
                this.oreName = entry.getKey();
                this.oreMap = entry.getValue();
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, oreName);
                buffer.writeInt(this.oreMap.size());
                for(AlternativeOreDrop.OreRegister reg : this.oreMap){
                    ByteBufUtils.writeUTF8String(buffer, reg.itemName);
                    buffer.writeInt(reg.damage);
                    ByteBufUtils.writeUTF8String(buffer, reg.modId);
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                oreName = ByteBufUtils.readUTF8String(buffer);
                int size = buffer.readInt();

                for(int i = 0; i < size; i++){
                    String itemName = ByteBufUtils.readUTF8String(buffer);
                    int damage = buffer.readInt();
                    String modId = ByteBufUtils.readUTF8String(buffer);
                    this.oreMap.add(new AlternativeOreDrop.OreRegister(itemName, damage, modId));
                }
            }
        }

        public static class PreferOre extends AODPacket {
            public String oreName;
            private String modId;
            private String itemName;
            private int damage;

            public AlternativeOreDrop.OreRegister reg;

            public PreferOre(){
            }

            public PreferOre(String oreName, AlternativeOreDrop.OreRegister reg){
                this.oreName = oreName;
                this.modId = reg.modId;
                this.itemName = reg.itemName;
                this.damage = reg.damage;
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, oreName);
                ByteBufUtils.writeUTF8String(buffer, itemName);
                buffer.writeInt(damage);
                ByteBufUtils.writeUTF8String(buffer, modId);
            }

            @Override
            public void decode(ByteBuf buffer){
                oreName = ByteBufUtils.readUTF8String(buffer);
                reg = new AlternativeOreDrop.OreRegister(ByteBufUtils.readUTF8String(buffer), buffer.readInt(), ByteBufUtils.readUTF8String(buffer));
            }
        }
    }
}
