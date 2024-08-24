package dev.chicoferreira.lifestealer.user.rules;

import java.time.Duration;
import java.util.Optional;

/**
 * Similar to {@link LifestealerUserRules}, but with an additional permission field and optional fields.
 * These will combine with the {@link LifestealerUserRules} to create a final set of rules for a player based on their permissions.
 *
 * @param permission   the permission the player needs to have for this group to be applied
 * @param maxHearts    the maximum amount of hearts a player can have
 * @param minHearts    the minimum amount of hearts a player can have
 * @param banTime      the duration that a player will be banned if they have less than the minimum amount of hearts
 * @param returnHearts the amount of hearts a player will have when they return from a ban
 */
public record LifestealerUserRulesGroup(String permission,
                                        Optional<Integer> maxHearts,
                                        Optional<Integer> minHearts,
                                        Optional<Duration> banTime,
                                        Optional<Integer> returnHearts) {
}