package alternativemods.alternativeoredrop;

import alternativemods.alternativeoredrop.commands.MainCommand;
import alternativemods.alternativeoredrop.events.OreDropEventHandler;
import alternativemods.alternativeoredrop.proxy.CommonProxy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 25.01.14
 * Time: 18:44
 */
@Mod(modid = "AlternativeOreDrop", name = "Alternative Ore Drop", version = "1.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = false, channels={"AltOreDrop"}, packetHandler = PacketHandler.class)
@SuppressWarnings("unused")
public class AlternativeOreDrop {

    public static class OreRegister {
        public int itemID;
        public int damage;
        public String modId;

        OreRegister(int itemID, int damage, String modId) {
            this.itemID = itemID;
            this.damage = damage;
            this.modId = modId;
        }
    }

    @Mod.Instance(value = "AlternativeOreDrop")
    public static AlternativeOreDrop instance;

    @SidedProxy(clientSide = "alternativemods.alternativeoredrop.proxy.ClientProxy", serverSide = "alternativemods.alternativeoredrop.proxy.CommonProxy")
    public static CommonProxy proxy;

    private static Map<String, ArrayList<OreRegister>> oreMap = new HashMap<String, ArrayList<OreRegister>>();
    public static String[] identifiers = new String[]{};
    public static boolean recreateRegister = false;
    private static String fileDir;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        proxy = new CommonProxy();

        fileDir = event.getModConfigurationDirectory() + "/AlternativeMods/AlternativeOreDrop";
    }

    public static void saveConfig() {
        Configuration cfg = new Configuration(new File(fileDir + "/config.cfg"));
        cfg.load();

        Property prop = cfg.get(Configuration.CATEGORY_GENERAL, "Regenerate Register", false);
        prop.comment = "Set this to true to make the register regenerate itself.\n" +
                "This is very useful, if you un/-installed one or more mods\n" +
                "and have to update the register.";
        prop.set(recreateRegister);

        prop = cfg.get(Configuration.CATEGORY_GENERAL, "Identifiers", "ore");
        prop.comment = "Define the valid identifiers separated by a comma.\n" +
                "IMPORTANT: The mod checks, if an item / block starts with one of the identifiers.\n" +
                "If you make any changes to this, setting \"Regenerate Register\" to true is advisable.";
        prop.set(StringUtils.join(identifiers, ","));

        cfg.save();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
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

        cfg.save();
        //---------------------------------------------------------------

        File register = new File(fileDir);
        if(!register.exists() || recreateRegister) {
            register.mkdirs();

            updateRegister();
        }
        else {
            Gson gson = new Gson();
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(fileDir + "/registrations.json"));
                Type strOreRegMap = new TypeToken<Map<String, ArrayList<OreRegister>>>(){}.getType();
                oreMap = gson.fromJson(br, strOreRegMap);
            }
            catch (FileNotFoundException e) {}
        }
    }

    public static void updateRegister() {
        oreMap.clear();
        System.out.println("Identifiers: " + StringUtils.join(identifiers, ","));
        for(String oreName : OreDictionary.getOreNames())
            for(String id : identifiers)
                if(oreName.startsWith(id))
                    registerOre(oreName, OreDictionary.getOres(oreName));

        initiateFirstRegistrations(fileDir);
    }

    public static void initiateFirstRegistrations(String dir) {
        try {
            FileWriter writer = new FileWriter(new File(dir + "/registrations.json"));

            String oreMap_String = new GsonBuilder().setPrettyPrinting().create().toJson(oreMap);
            writer.write(oreMap_String);
            writer.close();
        }
        catch (IOException e) {}
    }

    private static String getModIdForItem(ItemStack is) {
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(is.getItem());
        return id != null ? id.modId : "Minecraft";
    }

    public static void registerOre(String oreName, ArrayList<ItemStack> stack) {
        if(!oreMap.containsKey(oreName)) {
            ArrayList<OreRegister> regs = new ArrayList<OreRegister>();
            for(ItemStack is : stack) {
                regs.add(new OreRegister(is.itemID, is.getItemDamage(), getModIdForItem(is)));
            }
            oreMap.put(oreName, regs);
        }
    }

    public static void addOrRegisterOre(String oreName, ItemStack stack) {
        if(oreMap.containsKey(oreName)) {
            ArrayList<OreRegister> stList = oreMap.get(oreName);
            stList.add(new OreRegister(stack.itemID, stack.getItemDamage(), getModIdForItem(stack)));
            oreMap.put(oreName, stList);
        }
        else {
            ArrayList<OreRegister> stList = new ArrayList<OreRegister>();
            stList.add(new OreRegister(stack.itemID, stack.getItemDamage(), getModIdForItem(stack)));
            oreMap.put(oreName, stList);
        }
    }

    public static boolean isOreRegistered(String oreName) {
        if(oreMap.isEmpty())
            return false;
        return oreMap.containsKey(oreName);
    }

    public static boolean isFirstRegisteredOre(String oreName, ItemStack oreItem) {
        if(oreMap.isEmpty())
            return false;
        OreRegister first = oreMap.get(oreName).get(0);
        return (first.itemID == oreItem.itemID && first.damage == oreItem.getItemDamage());
    }

    public static OreRegister returnAlternativeOre(String oreName) {
        if(oreMap.isEmpty())
            return null;
        return oreMap.containsKey(oreName) ? oreMap.get(oreName).get(0) : null;
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new MainCommand());
    }
}