package dev.chicoferreira.lifestealer.events;

import dev.chicoferreira.lifestealer.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is triggered before a player consumes a heart item that you can cancel.
 */
public class LifestealerPreConsumeHeartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final LifestealerUser user;
    private final ItemStack itemStack;
    private int amount;
    private boolean cancelled;

    /**
     * Constructs a new LifestealerPreConsumeHeartEvent.
     *
     * @param player    the player who is consuming the heart item
     * @param user      the {@link LifestealerUser} associated with the player
     * @param itemStack the {@link ItemStack} representing the heart item
     * @param amount    the amount of hearts to be consumed (the amount of hearts the item contains)
     */
    public LifestealerPreConsumeHeartEvent(Player player, LifestealerUser user, ItemStack itemStack, int amount) {
        this.player = player;
        this.user = user;
        this.itemStack = itemStack;
        this.amount = amount;
        this.cancelled = false;
    }

    /**
     * Gets the player who is consuming the heart item.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link LifestealerUser} associated with the player.
     *
     * @return the {@link LifestealerUser} associated with the player
     */
    public LifestealerUser getUser() {
        return user;
    }

    /**
     * Gets the amount of hearts to be consumed.
     * Unless changed by {@link #setAmount(int)}, this is the amount of hearts the item contains.
     *
     * @return the amount of hearts
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of hearts that will be added to the player after consuming the heart item.
     * Initially this is the amount of hearts the item contains.
     *
     * @param amount the new amount of hearts
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets the heart {@link ItemStack} the player is consuming.
     *
     * @return the heart {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of the event.
     * If the event is cancelled, the item will not be deducted from the player's inventory
     * and the hearts will not be added to the player.
     *
     * @param cancel true to cancel the event, false to uncancel
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the HandlerList
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    /**
     * Gets the static HandlerList for this event.
     *
     * @return the HandlerList
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}