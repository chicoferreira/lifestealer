package dev.chicoferreira.lifestealer.user.rules;


import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.time.Duration;
import java.util.function.Function;

/**
 * Represents the rules the user has. This is calculated through the {@link LifestealerUserRulesController}
 * and the rules modifiers the suer has.
 * The rules are used to determine the maximum and minimum amount of hearts a player can have,
 * the ban time if they have less than the minimum amount of hearts, and the amount of hearts they will have when they return from a ban.
 * <p>
 * This is also used as a modifier for the user rules. A rules modifier is just a rule that will be added to the calculated rules based on the permissions.
 * The values of it can be negative, meaning that the modifier decreases the value of the rule. These modifiers are individual for each user and
 * can be retrieved through the {@link dev.chicoferreira.lifestealer.user.LifestealerUser#getRulesModifier()} method.
 *
 * @param maxHearts    The maximum amount of hearts a player can have. The player cannot consume more than this amount.
 * @param minHearts    The minimum amount of hearts a player can have. If a player has less than this amount, they will be banned temporarily.
 * @param banTime      The duration that a player will be banned if they have less than the minimum amount of hearts.
 * @param returnHearts The amount of hearts a player will have when they return from a ban.
 */
@ConfigSerializable
public record LifestealerUserRules(@Required int maxHearts,
                                   @Required int minHearts,
                                   @Required @NotNull Duration banTime,
                                   @Required int returnHearts) {

    public static LifestealerUserRules zeroed() {
        return new LifestealerUserRules(0, 0, Duration.ZERO, 0);
    }

    public static class Builder {
        private int maxHearts;
        private int minHearts;
        private Duration banTime;
        private int returnHearts;

        public Builder(LifestealerUserRules rules) {
            this.maxHearts = rules.maxHearts();
            this.minHearts = rules.minHearts();
            this.banTime = rules.banTime();
            this.returnHearts = rules.returnHearts();
        }

        public Builder() {
            this(zeroed());
        }

        public Builder maxHearts(int maxHearts) {
            this.maxHearts = maxHearts;
            return this;
        }

        public Builder minHearts(int minHearts) {
            this.minHearts = minHearts;
            return this;
        }

        public Builder banTime(@NotNull Duration banTime) {
            this.banTime = banTime;
            return this;
        }

        public Builder returnHearts(int returnHearts) {
            this.returnHearts = returnHearts;
            return this;
        }

        public LifestealerUserRules build() {
            return new LifestealerUserRules(maxHearts, minHearts, banTime, returnHearts);
        }
    }

    public LifestealerUserRules with(Function<Builder, Builder> function) {
        Builder builder = new Builder(this);
        function.apply(builder);
        return builder.build();
    }

    public LifestealerUserRules withSum(Function<Builder, Builder> function) {
        Builder builder = new Builder();
        function.apply(builder);
        LifestealerUserRules build = builder.build();

        return this.sum(build);
    }

    public LifestealerUserRules sum(@NotNull LifestealerUserRules modifierRules) {
        return new LifestealerUserRules(
                this.maxHearts() + modifierRules.maxHearts(),
                this.minHearts() + modifierRules.minHearts(),
                this.banTime().plus(modifierRules.banTime()),
                this.returnHearts() + modifierRules.returnHearts()
        );
    }
}
