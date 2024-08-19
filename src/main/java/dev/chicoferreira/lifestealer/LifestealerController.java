package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.heart.LifestealerUserRules;
import dev.chicoferreira.lifestealer.heart.LifestealerUserRulesController;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handling of the logic of the Lifestealer plugin.
 */
public class LifestealerController {

    private final LifestealerUserRulesController userRulesController;

    public LifestealerController(LifestealerUserRulesController userRulesController) {
        this.userRulesController = userRulesController;
    }

    public record ChangeHeartsResult(int previousHearts, int newHearts) {
        public boolean hasChanged() {
            return previousHearts != newHearts;
        }

        public int difference() {
            return newHearts - previousHearts;
        }
    }

    /**
     * Sets the amount of hearts a player health has. This also saves the new amount in the database asynchronously.
     * The amount of hearts is clamped between the minimum and maximum amount of hearts the player can have, based on
     * their {@link LifestealerUserRules}.
     *
     * @param player the player to set the hearts
     * @param user   the user related to the player
     * @param hearts the amount hearts to set
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult setHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        LifestealerUserRules rules = this.userRulesController.computeRules(player::hasPermission);
        hearts = Math.clamp(hearts, rules.minHearts(), rules.maxHearts());

        int currentHearts = user.getHearts();

        user.setHearts(hearts);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attribute != null) {
            attribute.setBaseValue(hearts * 2);
        }
        // TODO: save in database
        return new ChangeHeartsResult(currentHearts, hearts);
    }

    /**
     * Adds hearts to the player health bar and saves the new amount in the database asynchronously.
     * If the new amount exceeds the maximum amount of hearts the player can have, it will be clamped to the maximum value.
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to add
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult addHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() + hearts);
    }

    /**
     * Removes hearts from the player health bar and saves the new amount in the database asynchronously.
     * If the new amount is less than the minimum amount of hearts the player can have, it will be clamped the minimum value.
     *
     * @param player the player to add hearts
     * @param user   the user related to the player
     * @param hearts the amount of hearts to remove
     * @return a {@link ChangeHeartsResult} with the previous and new amount of hearts
     */
    public ChangeHeartsResult removeHearts(@NotNull Player player, @NotNull LifestealerUser user, int hearts) {
        return setHearts(player, user, user.getHearts() - hearts);
    }
}
