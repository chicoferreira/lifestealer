# Lifestealer Rules Usage

Lifestealer rules define the parameters that govern a player's heart-related status in the game. These rules control key
aspects such as the maximum and minimum hearts a player can have, the duration of bans, and the number of hearts a
player receives when returning from a ban.

The effective rules for a player are calculated at runtime by combining three elements:

1. **Default Rules:**  
   These are the baseline values configured for all players. They include:
    - **Maximum Hearts:** The upper limit a player can reach.
    - **Minimum Hearts:** The threshold below which a player will be banned.
    - **Ban Time:** The duration for which the player is banned if their hearts fall below the minimum.
    - **Return Hearts:** The number of hearts the player will have when they return from a ban.

2. **Group Rules (Overrides):**  
   These rules are applied if a player has specific permissions. The Lifestealer plugin checks the player's permissions
   against a list of rule groups defined in the configuration. If multiple groups match, the groups are applied in
   order, with later groups overriding earlier ones for any values that are specified. For example, a player with both
   `vip` and `admin` permissions may receive a combination of settings from both groups:
    - **Default:** `max hearts: 10`, `min hearts: 5`, `ban time: 1d 12h 30m`, `return hearts: 7`
    - **VIP Group:** Adjusts values to `max hearts: 15` and `min hearts: 3`
    - **Admin Group:** Adjusts values to `max hearts: 20` and `ban time: 15s`

   In this case, a player with both VIP and Admin permissions will end up with:
    - **Maximum Hearts:** 20 (from Admin)
    - **Minimum Hearts:** 3 (from VIP)
    - **Ban Time:** 15 seconds (from Admin)
    - **Return Hearts:** 7 (retained from Default)

3. **Rule Modifiers:**  
   These are individual adjustments that can be applied to a player's rules via commands. Rule modifiers are used to
   fine-tune the computed rules on a per-player basis. They can either increase or decrease the base values provided by
   the default and group rules. Lifestealer provides commands to:
    - **Set** a modifier:  
      `/lifestealer user rulemodifier set <rule> <value> [player]`
    - **Adjust** a modifier (add or subtract):  
      `/lifestealer user rulemodifier adjust <rule> <adjustment> [player]`
    - **Reset** all modifiers:  
      `/lifestealer user rulemodifier reset [player]`

   For example, if a player’s computed maximum hearts is 20 from the default rules and group rules and they have a
   modifier of `+5`, their effective maximum becomes 25.

## How Effective Rules Are Computed

When a player interacts with Lifestealer (e.g., gaining or losing hearts), the following steps are taken to compute
their effective rules:

1. **Start with the Default Rules:**  
   These provide the initial values for `max hearts`, `min hearts`, `ban time`, and `return hearts`.

2. **Apply Group Overrides:**  
   The plugin checks which groups (if any) match the player's permissions. For every matching group, any defined values
   override the corresponding default settings.  
   *Note:* If a group does not specify a value (for example, leaving `min hearts` undefined), the current value is
   retained.

3. **Add Rule Modifiers:**  
   Finally, any rule modifiers that have been applied to the player are added (or subtracted, if negative ) from the
   computed values. The result is the player’s effective rules that govern:
    - The maximum number of hearts they can have.
    - The minimum number of hearts they must maintain to not be banned.
    - The duration of the ban imposed for falling below the minimum.
    - The number of hearts they will have upon returning from a ban.

### Output of `/lifestealer user info` command

![`/lifestealer user info` output](/user_info_output.png)

For further configuration details, see [Configuring Rules](/configuration/rules).
