package alternativemods.alternativeoredrop.events;

import alternativemods.alternativeoredrop.AlternativeOreDrop;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Author: Lordmau5
 * Date: 25.01.14
 * Time: 18:45
 */
@SuppressWarnings("unused")
public class OreDropEventHandler {

    @ForgeSubscribe
    public void entJoinWorld(EntityJoinWorldEvent event) {
        if(!(event.entity instanceof EntityItem))
            return;
        if(event.world.isRemote)
            return;

        EntityItem itemEnt = (EntityItem) event.entity;
        int stackSize = itemEnt.getEntityItem().stackSize;

        ItemStack item = ((EntityItem)event.entity).getEntityItem();
        item = new ItemStack(item.itemID, 1, item.getItemDamage());
        String name = OreDictionary.getOreName(OreDictionary.getOreID(item));
        if(AlternativeOreDrop.isOreRegistered(name) && !AlternativeOreDrop.isFirstRegisteredOre(name, item)) {
            AlternativeOreDrop.OreRegister oreReg = AlternativeOreDrop.returnAlternativeOre(name);
            ItemStack alternativeOre = new ItemStack(oreReg.itemID, stackSize, oreReg.damage);

            itemEnt.setEntityItemStack(alternativeOre);
        }
    }

    @ForgeSubscribe
    public void oreRegister(OreDictionary.OreRegisterEvent event) {
        for(String id : AlternativeOreDrop.identifiers)
            if(event.Name.startsWith(id))
                AlternativeOreDrop.addOrRegisterOre(event.Name, event.Ore);
    }
}