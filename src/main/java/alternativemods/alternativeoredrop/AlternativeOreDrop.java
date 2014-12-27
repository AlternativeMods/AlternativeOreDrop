package alternativemods.alternativeoredrop;

import alternativemods.alternativeoredrop.commands.MainCommand;
import alternativemods.alternativeoredrop.events.ClientTickHandler;
import alternativemods.alternativeoredrop.events.OreDropEventHandler;
import alternativemods.alternativeoredrop.network.NetworkHandler;
import alternativemods.alternativeoredrop.variables.ClientVars;

import com.google.common.collect.ArrayListMultimap;
import com.google.gson.*;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Author: Lordmau5
 * Date: 25.01.14
 * Time: 18:44
 */
@Mod(modid = "AlternativeOreDrop", name = "Alternative Ore Drop", version = "1.2")
@SuppressWarnings("unused")
public class AlternativeOreDrop {

    public static class OreRegister {
        public String itemName;
        public int damage;
        public String modId;

        public OreRegister(String itemName, int damage, String modId){
            this.itemName = itemName;
            this.damage = damage;
            this.modId = modId;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof OreRegister) {
                OreRegister other = (OreRegister) obj;
                return other.damage == damage && other.itemName.equals(itemName) && other.modId.equals(modId);
            }
            return super.equals(obj);
        }
    }

    public static TreeMap<String, ArrayList<OreRegister>> oreMap = new TreeMap<String, ArrayList<OreRegister>>();
    public static String[] identifiers = new String[]{};
    public static boolean recreateRegister = false;
    private static String fileDir;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        NetworkHandler.registerChannels(event.getSide());
        
        if(event.getSide().isClient()) new ClientTickHandler();
        
        fileDir = event.getModConfigurationDirectory() + "/AlternativeMods/AlternativeOreDrop";
    }

    public static void saveConfig(){
        new Thread() {
            public void run(){
                Configuration cfg = new Configuration(new File(fileDir + "/config.cfg"));
                cfg.load();

                Property prop = cfg.get(Configuration.CATEGORY_GENERAL, "Regenerate Register", false);
                prop.comment = "Set this to true to make the register regenerate itself.\n" +
                        "This is very useful, if you un/-installed one or more mods\n" +
                        "and have to update the register." +
                        "" +
                        "Note: Can also be done through the configuration-GUI.";
                prop.set(recreateRegister);

                prop = cfg.get(Configuration.CATEGORY_GENERAL, "Identifiers", "ore");
                prop.comment = "Define the valid identifiers separated by a comma.\n" +
                        "IMPORTANT: The mod checks, if an item / block starts with one of the identifiers.\n" +
                        "If you make any changes to this, setting \"Regenerate Register\" to true is advisable." +
                        "" +
                        "Note: Can also be done through the configuration-GUI.";
                prop.set(StringUtils.join(identifiers, ","));

                if(cfg.hasChanged()) cfg.save();
            }
        }.start();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new OreDropEventHandler());

        //---------------------------------------------------------------
        Configuration cfg = new Configuration(new File(fileDir + "/config.cfg"));
        cfg.load();

        Property prop = cfg.get(Configuration.CATEGORY_GENERAL, "Regenerate Register", false);
        prop.comment = "Set this to true to make the register regenerate itself.\n" +
                "This is very useful, if you un/-installed one or more mods\n" +
                "and have to update the register.";
        recreateRegister = prop.getBoolean(false);
        prop.set(false);

        prop = cfg.get(Configuration.CATEGORY_GENERAL, "Identifiers", "ore");
        prop.comment = "Define the valid identifiers separated by a comma.\n" +
                "IMPORTANT: The mod checks, if an item / block starts with one of the identifiers.\n" +
                "If you make any changes to this, setting \"Regenerate Register\" to true is advisable.";
        String ids = prop.getString();
        identifiers = ids.split(",");

        if(cfg.hasChanged()) cfg.save();
        //---------------------------------------------------------------

        File register = new File(fileDir + "/registrations.json");
        if(!register.exists() || recreateRegister){
            new File(fileDir).mkdirs();

            updateRegister();
        }else{
            Gson gson = new Gson();
            try{
                FileReader fr = new FileReader(fileDir + "/registrations.json");
                JsonObject jsonBase = (JsonObject) new JsonParser().parse(fr);
                for(Map.Entry<String, JsonElement> entry : jsonBase.entrySet()){
                    JsonArray ar = (JsonArray) entry.getValue();
                    for(JsonElement el : ar){
                        JsonObject obj = (JsonObject) el;

                        String itemName = obj.get("item").getAsString();
                        int damage = obj.get("damage").getAsInt();
                        String modId = obj.get("mod").getAsString();

                        OreRegister reg = new OreRegister(itemName, damage, modId);
                        ArrayList<OreRegister> oreReg = oreMap.get(entry.getKey());
                        if(oreReg == null)
                            oreReg = new ArrayList<OreRegister>();
                        oreReg.add(reg);
                        oreMap.put(entry.getKey(), oreReg);
                    }
                }
                fr.close();
            }catch(FileNotFoundException e){
            }catch(IOException e){
            }
        }
    }

    public static void updateRegister(){
        oreMap.clear();
        for(String oreName : OreDictionary.getOreNames()){
            for(String id : identifiers)
                if(!id.isEmpty())
                    if(oreName.startsWith(id))
                        registerOre(oreName, OreDictionary.getOres(oreName));
        }

        initiateFirstRegistrations(fileDir);
    }

    public static void initiateFirstRegistrations(String dir){
        final String dirx = dir;
        new Thread() {
            public void run(){
                try{
                    FileWriter writer = new FileWriter(new File(dirx + "/registrations.json"));

                    JsonObject base = new JsonObject();
                    for(String oreName : oreMap.keySet()){
                        JsonArray ar = new JsonArray();
                        for(OreRegister reg : oreMap.get(oreName)){
                            JsonObject ore = new JsonObject();
                            ore.addProperty("item", reg.itemName);
                            ore.addProperty("damage", reg.damage);
                            ore.addProperty("mod", reg.modId);
                            ar.add(ore);
                        }
                        base.add(oreName, ar);
                    }

                    String oreMap_String = new GsonBuilder().setPrettyPrinting().create().toJson(base);
                    writer.write(oreMap_String);
                    writer.close();
                }catch(IOException e){
                }
            }
        }.run();
    }

    private static String getModIdForItem(ItemStack is){
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(is.getItem());
        return id != null ? id.modId : "Minecraft";
    }

    public static void registerOre(String oreName, ArrayList<ItemStack> stack){
        for(ItemStack is : stack){
            int dmg = is.getItemDamage();
            if(is.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                dmg = 0;
            ArrayList<OreRegister> oreReg = oreMap.get(oreName);
            if(oreReg == null)
                oreReg = new ArrayList<OreRegister>();
            OreRegister reg = new OreRegister(Item.itemRegistry.getNameForObject(is.getItem()).split(":")[1], dmg, getModIdForItem(is));
            if(!oreReg.contains(reg))
                oreReg.add(new OreRegister(Item.itemRegistry.getNameForObject(is.getItem()).split(":")[1], dmg, getModIdForItem(is)));
            oreMap.put(oreName, oreReg);
        }
    }

    public static void addOrRegisterOre(String oreName, ItemStack stack){
        ArrayList<OreRegister> oreReg = oreMap.get(oreName);
        if(oreReg == null)
            oreReg = new ArrayList<OreRegister>();
        oreReg.add(new OreRegister(Item.itemRegistry.getNameForObject(stack.getItem()).split(":")[1], stack.getItemDamage(), getModIdForItem(stack)));
        oreMap.put(oreName, oreReg);
    }

    public static List<OreRegister> getOresForName(ArrayListMultimap<String, OreRegister> oreMap, String oreName){
        List<OreRegister> list = oreMap.get(oreName);
        if(list == null || list.isEmpty())
            return null;
        return list;
    }

    public static boolean isOreRegistered(String oreName){
        if(oreMap.isEmpty())
            return false;
        return oreMap.containsKey(oreName);
    }

    public static boolean isFirstRegisteredOre(String oreName, ItemStack oreItem){
        if(oreMap.isEmpty() || oreMap.get(oreName).isEmpty())
            return false;
        OreRegister first = oreMap.get(oreName).get(0);
        return (first.itemName == Item.itemRegistry.getNameForObject(oreItem.getItem()) && first.damage == oreItem.getItemDamage());
    }

    public static OreRegister returnAlternativeOre(String oreName){
        if(oreMap.isEmpty())
            return null;
        return oreMap.containsKey(oreName) ? oreMap.get(oreName).get(0) : null;
    }

    public static void preferOre(String oreName, OreRegister reg, boolean isDedicated){
        TreeMap<String, ArrayList<OreRegister>> usageMap = isDedicated ? ClientVars.oreMap : oreMap;

        preferOreOnMap(oreName, reg, usageMap);

        if(!isDedicated) {
            preferOreOnMap(oreName, reg, ClientVars.sortMap);
            initiateFirstRegistrations(fileDir);
        }
    }

    private static void preferOreOnMap(String oreName, OreRegister reg, TreeMap<String, ArrayList<OreRegister>> map) {
        List<OreRegister> list = map.get(oreName);
        if(list == null)
            return;

        for(OreRegister tempReg : list){
            if(tempReg.itemName.equals(reg.itemName) && tempReg.damage == reg.damage && tempReg.modId.equals(reg.modId)){
                map.get(oreName).remove(tempReg);
                break;
            }
        }
        map.get(oreName).add(0, reg);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event){
        event.registerServerCommand(new MainCommand());
    }
}
