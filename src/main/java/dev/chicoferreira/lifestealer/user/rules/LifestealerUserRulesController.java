package dev.chicoferreira.lifestealer.user.rules;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class LifestealerUserRulesController {

    public LifestealerUserRulesController(LifestealerUserRules defaultRules, List<LifestealerUserRulesGroup> groups) {
        this.defaultRule = defaultRules;
        this.groups = groups;
    }

    private final LifestealerUserRules defaultRule;
    private final List<LifestealerUserRulesGroup> groups;

    /**
     * Calculates the {@link LifestealerUserRules} that the permissions tested indicates.
     * <p>
     * If no group matches the permissions, the default cap is returned.
     * If multiple groups match the permissions, the last one is returned, falling back to previous ones when
     * it does not have a min or max hearts.
     * <p>
     * This will only compute the rules based on the permissions. If you want to compute the rules based on the user
     * and the permissions, use {@link dev.chicoferreira.lifestealer.user.LifestealerUserController#computeUserRules(Player, LifestealerUser)}
     * as it will also take into account the user's rules modifiers.
     *
     * @param permissionTester a function that tests if something has a permission (e.g. <pre>player::hasPermission</pre>)
     * @return the {@link LifestealerUserRules} that the permissions tested indicates
     */
    public LifestealerUserRules computeRulesByPermission(Function<String, Boolean> permissionTester) {
        LifestealerUserRules result = this.defaultRule;

        for (LifestealerUserRulesGroup group : groups) {
            if (permissionTester.apply(group.permission())) {
                result = new LifestealerUserRules(
                        group.maxHearts().orElse(result.maxHearts()),
                        group.minHearts().orElse(result.minHearts()),
                        group.banTime().orElse(result.banTime()),
                        group.returnHearts().orElse(result.returnHearts())
                );
            }
        }

        return result;
    }

}
