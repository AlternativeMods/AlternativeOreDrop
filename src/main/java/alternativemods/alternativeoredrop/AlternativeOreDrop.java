package alternativemods.alternativeoredrop;

import alternativemods.alternativeoredrop.events.OreDropEventHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 25.01.14
 * Time: 18:44
 */
@Mod(modid = "AlternativeOreDrop", name = "Alternative Ore Drop")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
@SuppressWarnings("unused")
public class AlternativeOreDrop {

    private static Map<String, ItemStack> oreMap = new HashMap<String, ItemStack>();

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        oreMap.clear();

        for(String oreName : OreDictionary.getOreNames())
            if(oreName.startsWith("ore"))
                registerOre(oreName, OreDictionary.getOres(oreName).get(0));

        MinecraftForge.EVENT_BUS.register(new OreDropEventHandler());
    }

    public static void registerOre(String oreName, ItemStack stack) {
        if(!oreMap.containsKey(oreName))
            oreMap.put(oreName, stack);
    }

    public static boolean isOreRegistered(String oreName) {
        if(oreMap.isEmpty())
            return false;
        return oreMap.containsKey(oreName);
    }

    public static boolean isFirstRegisteredOre(String oreName, ItemStack oreItem) {
        if(oreMap.isEmpty())
            return false;
        ItemStack first = oreMap.get(oreName);
        return (first.itemID == oreItem.itemID && first.getItemDamage() == oreItem.getItemDamage());
    }

    public static ItemStack returnAlternativeOre(String oreName) {
        if(oreMap.isEmpty())
            return null;
        return oreMap.containsKey(oreName) ? oreMap.get(oreName) : null;
    }
}