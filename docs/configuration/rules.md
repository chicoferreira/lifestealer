# Configuring Rules

You can configure [rules](/usage/rules) that change the values of the `max hearts`, `min hearts`, `ban time` and
`return hearts` fields
based on the permissions of the player.

## Example

```yaml
# The amount of hearts a player should start with when they first join the server, a heart consists of 2 health points
starting hearts: 10

# Define rules that will change values based on player's permissions
rules:
  # The rules that will be applied to players that don't have any of the groups below
  default:
    # The maximum amount of hearts a player can have
    max hearts: 10
    # The minimum amount of hearts a player can have, if they lose more than this amount they will be banned
    min hearts: 5
    # The amount of time a player will be banned for if they lose more than the minimum amount of hearts
    ban time: 1d 12h 30m
    # The amount of hearts the player will have when they return from a ban
    return hearts: 7
  # Rules overrides applied to players with specific permissions
  groups:
    # If the player has permission "vip"
    - permission: "vip"
      # Change the maximum amount of hearts to 15
      max hearts: 15
      # and the minimum amount of hearts to 3
      min hearts: 3
      # The ban time and return hearts will be the same as the default
    # If the player has permission "admin"
    - permission: "admin"
      # Change the maximum amount of hearts to 20
      max hearts: 20
      # and the ban time to 15 seconds
      ban time: 15s
    # If the player has multiple groups, the overrides will be applied in the order they are defined here
    # So if a player has both "vip" and "admin" permissions, they will have:
    # - 20 max hearts
    # - 3 min hearts
    # - 15 seconds of ban time
    # - 7 return hearts
```

## Changing starting hearts

The `starting hearts` field is used to define the amount of hearts a player should start with when they first join the
server. A heart consists of 2 health points, so if you want to keep the vanilla setting, you can set this to `10`.

## Example based on the configuration above

The rules will be applied in the order they are defined in the configuration file.

- The rules start with the default rule.
    - (`max hearts`: 10, `min hearts`: 5, `ban time`: 1d 12h 30m, `return hearts`: 7)
- If the player has the `vip` permission, the rules will be changed to:
    - (`max hearts`: **15**, `min hearts`: **3**, rest as before)
- If the player has the `admin` permission, the rules will be changed to:
    - (`max hearts`: **20**, `ban time`: **15s**, rest as before)

So if a player has both `vip` and `admin` permissions, they will have:
- (`max hearts`: **20**, `min hearts`: **3**, `ban time`: **15s**, `return hearts`: **7**)

These values will be then summed with the [player rule modifiers](/usage/rules#player-rule-modifiers) to get the final
values.