package alternativemods.alternativeoredrop.network;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            public OpenAODGui() {}
            public OpenAODGui(String[] identifiers) {
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
                for(int i=0; i<this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
            }
        }

        public static class AdjustRegister extends AODPacket {
            public String[] identifiers;
            public int size;
            public Multimap<String, String> values = HashMultimap.create();
            public Map<String, ArrayList<AlternativeOreDrop.OreRegister>> returnList = new HashMap<String, ArrayList<AlternativeOreDrop.OreRegister>>();

            public AdjustRegister() {}
            public AdjustRegister(String[] identifiers, ArrayListMultimap<String, AlternativeOreDrop.OreRegister> oreMap) {
                this.identifiers = identifiers;
                this.size = oreMap.size();
                for(Map.Entry<String, AlternativeOreDrop.OreRegister> reg : oreMap.entries()) {
                    values.put(reg.getKey(), new GsonBuilder().setPrettyPrinting().create().toJson(reg.getValue()));
                }
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(this.identifiers.length);
                for(String id : this.identifiers)
                    ByteBufUtils.writeUTF8String(buffer, id);
                buffer.writeInt(this.size);
                for(Map.Entry<String, String> entry : this.values.entries()) {
                    ByteBufUtils.writeUTF8String(buffer, entry.getKey());
                    ByteBufUtils.writeUTF8String(buffer, entry.getValue());
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                this.identifiers = new String[buffer.readInt()];
                for(int i=0; i<this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
                this.size = buffer.readInt();

                Gson gson = new Gson();
                Type oreRegJson = new TypeToken<AlternativeOreDrop.OreRegister>(){}.getType();

                ArrayListMultimap<String, AlternativeOreDrop.OreRegister> tempList = ArrayListMultimap.create();

                for(int i=0; i<this.size; i++) {
                    tempList.put(ByteBufUtils.readUTF8String(buffer), (AlternativeOreDrop.OreRegister) gson.fromJson(ByteBufUtils.readUTF8String(buffer), oreRegJson));
                }

                for(Map.Entry<String, AlternativeOreDrop.OreRegister> ent : tempList.entries()) {
                    if(this.returnList.get(ent.getKey()) == null)
                        this.returnList.put(ent.getKey(), new ArrayList<AlternativeOreDrop.OreRegister>());
                    this.returnList.get(ent.getKey()).add(ent.getValue());
                }
            }
        }

        public static class AdjustOre extends AODPacket {
            public String oreName;
            public List<AlternativeOreDrop.OreRegister> oreMap = new ArrayList<AlternativeOreDrop.OreRegister>();

            public AdjustOre() {}
            public AdjustOre(String oreName, List<AlternativeOreDrop.OreRegister> oreMap) {
                this.oreName = oreName;
                this.oreMap = oreMap;
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, this.oreName);
                buffer.writeInt(this.oreMap.size());
                for(AlternativeOreDrop.OreRegister reg : oreMap) {
                    ByteBufUtils.writeUTF8String(buffer, reg.itemName);
                    buffer.writeInt(reg.damage);
                    ByteBufUtils.writeUTF8String(buffer, reg.modId);
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                this.oreName = ByteBufUtils.readUTF8String(buffer);
                int size = buffer.readInt();

                for(int i=0; i<size; i++) {
                    String itemName = ByteBufUtils.readUTF8String(buffer);
                    int damage = buffer.readInt();
                    String modId = ByteBufUtils.readUTF8String(buffer);
                    this.oreMap.add(new AlternativeOreDrop.OreRegister(itemName, damage, modId));
                }
            }
        }
    }

    public static class Server {
        public static class UpdateIdentifiers extends AODPacket {
            public String[] identifiers;

            public UpdateIdentifiers() {}
            public UpdateIdentifiers(String identifiers) {
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
                for(int i=0; i<this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
            }
        }

        public static class RegenerateRegister extends AODPacket {
            public RegenerateRegister() {}

            @Override
            public void encode(ByteBuf buffer){}

            @Override
            public void decode(ByteBuf buffer){}
        }

        public static class AdjustRegister extends AODPacket {
            public String[] identifiers;
            public int size;

            public Multimap<String, String> values = HashMultimap.create();
            public ArrayListMultimap<String, AlternativeOreDrop.OreRegister> returnList = ArrayListMultimap.create();

            public AdjustRegister() {
                this.size = AlternativeOreDrop.oreMap.size();
                for(Map.Entry<String, AlternativeOreDrop.OreRegister> reg : AlternativeOreDrop.oreMap.entries()) {
                    values.put(reg.getKey(), new GsonBuilder().setPrettyPrinting().create().toJson(reg.getValue()));
                }
            }

            @Override
            public void encode(ByteBuf buffer){
                buffer.writeInt(AlternativeOreDrop.identifiers.length);
                for(String id : AlternativeOreDrop.identifiers)
                    ByteBufUtils.writeUTF8String(buffer, id);
                buffer.writeInt(this.size);
                for(Map.Entry<String, String> entry : this.values.entries()) {
                    ByteBufUtils.writeUTF8String(buffer, entry.getKey());
                    ByteBufUtils.writeUTF8String(buffer, entry.getValue());
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                this.identifiers = new String[buffer.readInt()];
                for(int i=0; i<this.identifiers.length; i++)
                    this.identifiers[i] = ByteBufUtils.readUTF8String(buffer);
                this.size = buffer.readInt();

                Gson gson = new Gson();
                Type oreRegJson = new TypeToken<AlternativeOreDrop.OreRegister>(){}.getType();

                ArrayListMultimap<String, AlternativeOreDrop.OreRegister> tempList = ArrayListMultimap.create();

                for(int i=0; i<this.size; i++) {
                    tempList.put(ByteBufUtils.readUTF8String(buffer), (AlternativeOreDrop.OreRegister) gson.fromJson(ByteBufUtils.readUTF8String(buffer), oreRegJson));
                }

                for(Map.Entry<String, AlternativeOreDrop.OreRegister> ent : tempList.entries()) {
                    this.returnList.get(ent.getKey()).add(ent.getValue());
                }
            }
        }

        public static class AdjustOre extends AODPacket {
            public String oreName;

            public List<AlternativeOreDrop.OreRegister> oreMap = new ArrayList<AlternativeOreDrop.OreRegister>();

            public AdjustOre() {}
            public AdjustOre(String oreName) {
                this.oreName = oreName;
                for(AlternativeOreDrop.OreRegister reg : AlternativeOreDrop.oreMap.get(oreName)) {
                    oreMap.add(reg);
                }
            }

            @Override
            public void encode(ByteBuf buffer){
                ByteBufUtils.writeUTF8String(buffer, oreName);
                buffer.writeInt(this.oreMap.size());
                for(AlternativeOreDrop.OreRegister reg : this.oreMap) {
                    ByteBufUtils.writeUTF8String(buffer, reg.itemName);
                    buffer.writeInt(reg.damage);
                    ByteBufUtils.writeUTF8String(buffer, reg.modId);
                }
            }

            @Override
            public void decode(ByteBuf buffer){
                oreName = ByteBufUtils.readUTF8String(buffer);
                int size = buffer.readInt();

                for(int i=0; i<size; i++) {
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

            public PreferOre() {}
            public PreferOre(String oreName, AlternativeOreDrop.OreRegister reg) {
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