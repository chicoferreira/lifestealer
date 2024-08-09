package dev.chicoferreira.lifestealer;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handling of the logic of the Lifestealer plugin.
 */
public class LifestealerController {

    /**
     * Sets the amount of hearts a player health has. This also saves the new amount in the database asynchronously.
     *
     * @param player the player to set the hearts
     * @param user   the user related to the player
     * @param hearts the amount hearts to set
     * @return the new amount of hearts
     */
    public int setHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        user.setHearts(hearts);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(hearts * 2);
        }
        // TODO: save in database
        return hearts;
    }

    /**
     * Adds hearts to the player health bar and saves the new amount in the database asynchronously.
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to add
     * @return the new amount of hearts
     */
    public int addHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() + hearts);
    }

    /**
     * Removes hearts from the player health bar and saves the new amount in the database asynchronously.
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to remove
     * @return the new amount of hearts
     */
    public int removeHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() - hearts);
    }
}
