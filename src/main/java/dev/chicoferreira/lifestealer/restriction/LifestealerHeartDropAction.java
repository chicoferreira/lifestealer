package dev.chicoferreira.lifestealer.restriction;

/**
 * An enum that represents the action to take if a restriction is met.
 */
public enum LifestealerHeartDropAction {

    DROP,
    NOT_DROP,
    NOT_REMOVE_HEARTS;

    /**
     * Returns if the item should be dropped.
     *
     * @return true if the heart item should be dropped
     */
    public boolean shouldDropItem() {
        return this == DROP;
    }

    /**
     * Returns if the hearts should be removed from the player.
     *
     * @return true if the hearts should be removed from the player
     */
    public boolean shouldRemoveHearts() {
        return this != NOT_REMOVE_HEARTS;
    }
}
