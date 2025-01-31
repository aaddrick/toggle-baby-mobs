package aartcraft.togglebabymobs.items;

import aartcraft.togglebabymobs.ToggleBabyMobs;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(Item item, RegistryKey<Item> registryKey) {
        // Register the item.

        // Return the registered item!
        return Registry.register(Registries.ITEM, registryKey.getValue(), item);
    }

    public static final RegistryKey<Item> AGE_POTION_KEY = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ToggleBabyMobs.MOD_ID, "age_potion"));
    public static final Item AGE_POTION = register(
            new AgePotionItem(new Item.Settings().registryKey(AGE_POTION_KEY)),
            AGE_POTION_KEY
    );

    public static void registerItems() {
        // Get the event for modifying entries in the ingredients group.
        // And register an event handler that adds our baby food to the ingredients group.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK)
                .register((itemGroup) -> itemGroup.add(ModItems.AGE_POTION));
    }
}

