package dev.chicoferreira.lifestealer.item;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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

    public static final NamespacedKey HEARTS_AMOUNT_KEY = new NamespacedKey("lifestealer", "hearts");
    public static final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey("lifestealer", "item_type");
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
     * Returns the @{@link LifestealerHeartItem} to drop when the player dies.
     * This method will never return null
     *
     * @return The @{@link LifestealerHeartItem} to drop when the player dies
     */
    public @NotNull LifestealerHeartItem getItemToDropWhenPlayerDies() {
        // This will never return null because of the invariant that the item type to drop when the player dies exists inside the map
        return items.get(this.itemToDropWhenPlayerDies);
    }

    /**
     * This method sets the item type name to drop when the player dies. This is intended to use when the plugin reloads.
     * The itemTypeToDropWhenPlayerDies must be contained in the items map otherwise {@link IllegalArgumentException}
     * will be thrown.
     *
     * @param itemTypeToDropWhenPlayerDies The item type to drop when the player dies
     * @throws IllegalArgumentException If the item type is not registered
     */
    public void setItemToDropWhenPlayerDies(@NotNull LifestealerHeartItem itemTypeToDropWhenPlayerDies) {
        if (!this.items.containsKey(itemTypeToDropWhenPlayerDies.typeName())) {
            throw new IllegalArgumentException("Item type to drop when player dies is not registered.");
        }
        this.itemToDropWhenPlayerDies = itemTypeToDropWhenPlayerDies.typeName();
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
        persistentDataContainer.set(HEARTS_AMOUNT_KEY, PersistentDataType.INTEGER, heartItem.heartAmount());
        persistentDataContainer.set(ITEM_TYPE_KEY, PersistentDataType.STRING, heartItem.typeName());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Registers the item in the item manager. After this method is called,
     * players can get this item from the /lifestealer item give command.
     * <p>
     * The {@link LifestealerHeartItem#baseItemStack()} doesn't need to have the
     * NBT data for the amount of hearts and the item type.
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
     * Internaly, this method looks up the {@link #HEARTS_AMOUNT_KEY} in the item meta's persistent data container.
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
        return persistentDataContainer.getOrDefault(HEARTS_AMOUNT_KEY, PersistentDataType.INTEGER, 0);
    }

    /**
     * Calculates the total amount of hearts given by the heart items in the player's inventory.
     * For example, if the player has 3 heart items that give 2 hearts each, this method will return 6.
     *
     * @param player The player to get the sum of heart items in the inventory
     * @return The total number of hearts given by the heart items in the player's inventory
     */
    public int calculateTotalHeartsInInventory(Player player) {
        int sum = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && getItemType(itemStack) != null) {
                sum += getHearts(itemStack) * itemStack.getAmount();
            }
        }
        return sum;
    }

    /**
     * Gets the item type stored in the item stack.
     * This method will return null if the item stack is not a lifestealer heart item.
     * <p>
     * Internaly, this method looks up the {@link #ITEM_TYPE_KEY} in the item meta's persistent data container.
     *
     * @param itemStack The item stack to get the type from
     * @return The item type stored in the ItemStack or null if the item stack is not a lifestealer heart item
     */
    public @Nullable String getItemType(@NotNull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return null;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        return persistentDataContainer.get(ITEM_TYPE_KEY, PersistentDataType.STRING);
    }

    /**
     * Counts the heart items with the given type name in the player's inventory.
     * Internally, this method looks up the {@link #ITEM_TYPE_KEY} in the item meta's persistent data container
     * to check the item type name.
     *
     * @param player   the player to count the heart items in the inventory
     * @param typeName the type name of the item to count
     * @return the amount of heart items in the player's inventory
     */
    public int countHeartItems(@NotNull Player player, @NotNull String typeName) {
        int count = 0;
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && typeName.equals(getItemType(itemStack))) {
                count += itemStack.getAmount();
            }
        }
        return count;
    }

    /**
     * Takes heart items from the player's inventory.
     * Internally, this method looks up the {@link #ITEM_TYPE_KEY} in the item meta's persistent data container
     *
     * @param player   the player to take the heart items from
     * @param typeName the heart item name type to take
     * @param amount   the amount of heart items to take
     * @return the amount of heart items that could not be taken (if the player does not have enough)
     */
    public int takeHeartItems(@NotNull Player player, @NotNull String typeName, int amount) {
        for (ItemStack itemStack : player.getInventory()) {
            if (itemStack != null && typeName.equals(getItemType(itemStack))) {
                if (itemStack.getAmount() >= amount) {
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    return 0;
                } else {
                    amount -= itemStack.getAmount();
                    itemStack.setAmount(0);
                }
            }
        }
        return amount;
    }

    /**
     * Gives heart items to the player.
     * The method will return the amount of heart items that could not be given, when the player's inventory becomes full.
     *
     * @param player    the player to give the heart items to
     * @param heartItem the heart item to give
     * @param amount    the amount of heart items to give
     * @return the amount of heart items that could not be given (if the player's inventory is full)
     */
    public int giveHeartItems(@NotNull Player player, @NotNull LifestealerHeartItem heartItem, int amount) {
        ItemStack itemStack = generateItem(heartItem);

        for (int i = 0; i < amount; i++) {
            if (!player.getInventory().addItem(itemStack).isEmpty()) {
                return amount - i;
            }
        }

        return 0;
    }

    /**
     * @return An immutable list of all the item types registered in the item manager.
     */
    public List<String> getItemTypes() {
        return List.copyOf(this.items.keySet());
    }
}
