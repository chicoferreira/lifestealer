package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player dies, before the hearts have been removed from the player.
 */
public class LifestealerPrePlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PlayerDeathEvent deathEvent;
    private final Player player;
    private final LifestealerUser user;
    private ItemStack itemStackToDrop;
    private int heartsToRemove;
    private boolean cancelled;

    /**
     * Constructs a new LifestealerPrePlayerDeathEvent.
     *
     * @param deathEvent      the death event that originated this event
     * @param player          the player that died
     * @param user            the user related to the player
     * @param itemStackToDrop the heart item to drop when the player dies
     * @param heartsToRemove  the amount of hearts to remove from the player
     */
    public LifestealerPrePlayerDeathEvent(@NotNull PlayerDeathEvent deathEvent, @NotNull Player player, @NotNull LifestealerUser user, @NotNull ItemStack itemStackToDrop, int heartsToRemove) {
        this.deathEvent = deathEvent;
        this.player = player;
        this.user = user;
        this.itemStackToDrop = itemStackToDrop;
        this.heartsToRemove = heartsToRemove;
    }

    /**
     * Gets the death event that originated this event.
     * Changes to this event are not recommended. Please make sure to be sure of what you are doing.
     *
     * @return the death event that originated this event
     */
    public @NotNull PlayerDeathEvent getDeathEvent() {
        return deathEvent;
    }

    /**
     * Gets the player that died.
     *
     * @return the player that died
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the user related to the player.
     *
     * @return the user related to the player
     */
    public @NotNull LifestealerUser getUser() {
        return user;
    }

    /**
     * Returns the {@link ItemStack} to drop when the player dies.
     * Initially the heart item is valid for consumption and contains the amount of hearts that {@link #getHeartsToRemove()} returns.
     * <p>
     * This is a reference to the {@link ItemStack} that will be dropped, so you can modify it directly to change it.
     * <p>
     * If you wish to replace the {@link ItemStack} for other {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItem}
     * use {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager#getItem(String)},
     * {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager#generateItem(LifestealerHeartItem)} and
     * {@link #setItemStackToDrop(ItemStack)}.
     * <p>
     * This can return null if a plugin has set the item to drop to null.
     *
     * @return the heart {@link ItemStack} to drop when the player dies
     */
    public @Nullable ItemStack getItemStackToDrop() {
        return itemStackToDrop;
    }

    /**
     * Sets the {@link ItemStack} to drop when the player dies.
     * <p>
     * When setting a new {@link ItemStack}, you may need to set the amount of hearts to be removed from the player again if you
     * wish to keep the amount of hearts consistent and the new {@link ItemStack} contains a different amount of hearts.
     * <p>
     * If you wish to replace the {@link ItemStack} for other {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItem}
     * use {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager#getItem(String)},
     * {@link dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager#generateItem(LifestealerHeartItem)} to
     * generate a new {@link ItemStack} and set it here.
     * <p>
     * Setting this to null will make the player to not drop the heart item when they die.
     *
     * @param itemStackToDrop the heart {@link ItemStack} to drop when the player dies
     */
    public void setItemStackToDrop(@Nullable ItemStack itemStackToDrop) {
        this.itemStackToDrop = itemStackToDrop;
    }

    /**
     * Gets the amount of hearts to remove from the player when they die.
     * Unless changed, this is the amount of hearts contained in the {@link ItemStack} returned by {@link #getItemStackToDrop()}.
     *
     * @return the amount of hearts to remove from the player when they die
     */
    public int getHeartsToRemove() {
        return heartsToRemove;
    }

    /**
     * Sets the amount of hearts to remove from the player when they die.
     *
     * @param heartsToRemove the amount of hearts to remove from the player when they die
     */
    public void setHeartsToRemove(int heartsToRemove) {
        this.heartsToRemove = heartsToRemove;
    }

    /**
     * Returns if the event had been cancelled.
     *
     * @return if the event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets if the event should be cancelled.
     * If the event is cancelled, the player will not lose hearts, the {@link ItemStack} returned by {@link #getItemStackToDrop()}
     * will not be dropped. The player will also not be banned if they have less than the minimum amount of hearts.
     *
     * @param cancel true if you wish to cancel this event and prevent the player from losing hearts and dropping the heart item,
     *               false otherwise
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the handler list.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
