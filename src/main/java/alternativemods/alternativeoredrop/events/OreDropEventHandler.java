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

        EntityItem oldItem = (EntityItem) event.entity;
        int stackSize = oldItem.getEntityItem().stackSize;

        ItemStack item = ((EntityItem)event.entity).getEntityItem();
        item = new ItemStack(item.itemID, 1, item.getItemDamage());
        String name = OreDictionary.getOreName(OreDictionary.getOreID(item));
        if(AlternativeOreDrop.isOreRegistered(name) && !AlternativeOreDrop.isFirstRegisteredOre(name, item)) {
            ItemStack alternativeOreStack = AlternativeOreDrop.returnAlternativeOre(name);
            ItemStack alternativeOre = new ItemStack(alternativeOreStack.itemID, stackSize, alternativeOreStack.getItemDamage());
            event.entity.setDead();

            EntityItem newItem = new EntityItem(event.world, oldItem.posX, oldItem.posY, oldItem.posZ, alternativeOre);
            newItem.delayBeforeCanPickup = oldItem.delayBeforeCanPickup;
            newItem.motionX = oldItem.motionX;
            newItem.motionY = oldItem.motionY;
            newItem.motionZ = oldItem.motionZ;
            event.world.spawnEntityInWorld(newItem);
        }
    }

    @ForgeSubscribe
    public void oreRegister(OreDictionary.OreRegisterEvent event) {
        if(event.Name.startsWith("ore"))
            AlternativeOreDrop.registerOre(event.Name, event.Ore);
    }
}