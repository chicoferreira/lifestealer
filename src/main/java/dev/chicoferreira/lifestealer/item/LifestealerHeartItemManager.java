package dev.chicoferreira.lifestealer.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifestealerHeartItemManager {

    public static final NamespacedKey HEARTS_KEY = new NamespacedKey("lifestealer", "hearts");
    private final Map<String, LifestealerHeartItem> items;

    /**
     * The item type to drop when the player dies.
     * This must hold the invariant that the item type exists in the items map.
     */
    private String itemToDropWhenPlayerDies;

    public LifestealerHeartItemManager(List<LifestealerHeartItem> itemList, String itemToDropWhenPlayerDies) {
        this.items = new HashMap<>();
        itemList.forEach(this::registerItem);

        this.itemToDropWhenPlayerDies = itemToDropWhenPlayerDies;

        if (itemToDropWhenPlayerDies == null || !this.items.containsKey(itemToDropWhenPlayerDies)) {
            throw new IllegalArgumentException("Item type to drop when player dies does not exist.");
        }
    }

    /**
     * This method returns the item type name to drop when the player dies.
     * By the other invariants, this item type name must exist in the items map.
     *
     * @return The item type name to drop when the player dies
     */
    public @NotNull String getItemNameToDropWhenPlayerDies() {
        return this.itemToDropWhenPlayerDies;
    }

    /**
     * This item has the correct NBT data ready to be used by a player.
     *
     * @return The item stack to drop when the player dies
     */
    public @NotNull ItemStack getItemStackToDropWhenPlayerDies() {
        LifestealerHeartItem lifestealerHeartItem = items.get(this.itemToDropWhenPlayerDies);
        return generateItem(lifestealerHeartItem);
    }

    /**
     * This method sets the item type name to drop when the player dies. This is intended to use when the plugin reloads.
     * The itemTypeToDropWhenPlayerDies must be contained in the items map otherwise {@link IllegalArgumentException}
     * will be thrown.
     *
     * @param itemTypeToDropWhenPlayerDies The item type name to drop when the player dies
     * @throws IllegalArgumentException If the item type is not registered
     */
    public void setItemToDropWhenPlayerDies(@NotNull String itemTypeToDropWhenPlayerDies) {
        if (!this.items.containsKey(itemTypeToDropWhenPlayerDies)) {
            throw new IllegalArgumentException("Item type to drop when player dies is not registered.");
        }
        this.itemToDropWhenPlayerDies = itemTypeToDropWhenPlayerDies;
    }

    /**
     * Gets a {@link LifestealerHeartItem} from the item type name and calls {@link #generateItem(LifestealerHeartItem)}.
     *
     * @param heartItemType The type name of the heart item
     * @return The generated item stack or null if the item type does not exist
     */
    public @Nullable ItemStack generateItem(String heartItemType) {
        LifestealerHeartItem lifestealerHeartItem = this.items.get(heartItemType);
        if (lifestealerHeartItem == null) {
            return null;
        }
        return generateItem(lifestealerHeartItem);
    }

    /**
     * Generates an item stack from a {@link LifestealerHeartItem} with the correct properties from the item.
     * The item stack will have the amount of hearts in the NBT data.
     *
     * @param heartItem The item to generate the item stack from
     * @return The generated item stack
     */
    public @NotNull ItemStack generateItem(@NotNull LifestealerHeartItem heartItem) {
        ItemStack itemStack = heartItem.baseItemStack().clone();
        itemStack.setAmount(1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(HEARTS_KEY, PersistentDataType.INTEGER, heartItem.heartAmount());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Registers the item in the item manager. After this method is called,
     * players can get this item from the /lifestealer item give command.
     * <p>
     * The {@link LifestealerHeartItem#baseItemStack()} doesn't need to have the NBT data for the amount of hearts.
     * When generating the item stack with {@link #generateItem(LifestealerHeartItem)}, the manager will add the
     * correct NBT data to the item stack.
     *
     * @param item The item to register
     */
    public void registerItem(@NotNull LifestealerHeartItem item) {
        this.items.put(item.typeName(), item);
    }

    /**
     * Returns the registered item with the given type name.
     * Only use this method if you need this for type safety or getting the
     * amount of hearts without generating the item stack.
     * <p>
     * Use {@link #generateItem(String)} to get the item stack instead.
     *
     * @param typeName The type name of the item to search
     * @return The item with the given type name or null if the item does not exist
     */
    public @Nullable LifestealerHeartItem getItem(@NotNull String typeName) {
        return this.items.get(typeName);
    }

    /**
     * Gets the amount of hearts an item stack gives.
     * This method will return 0 if the item stack is not a lifestealer heart item.
     * <p>
     * Internaly, this method looks up the {@link #HEARTS_KEY} in the item meta's persistent data container.
     *
     * @param itemStack The item stack to get the hearts from
     * @return The amount of hearts the item stack gives
     */
    public int getHearts(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return 0;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.getOrDefault(HEARTS_KEY, PersistentDataType.INTEGER, 0);
    }

    /**
     * @return An immutable list of all the item types registered in the item manager.
     */
    public List<String> getItemTypes() {
        return List.copyOf(this.items.keySet());
    }
}
