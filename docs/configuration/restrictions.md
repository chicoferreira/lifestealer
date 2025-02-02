# Configuring Item Drop Restrictions

You are able to configure the behavior of the plugin when a player dies for specific reasons.

## Example

```yaml
heart drop restrictions:
  - type: "death cause"
    cause: "fall"
    # If the cause is fall (player dies by falling)
    action: "not drop"
    # They won't drop the item but will still lose the hearts
  - type: "same ip"
    # If the player is killed by someone with the same IP address
    action: "not drop"
    # They won't drop the item but will still lose the hearts
  - type: "world"
    world: "end"
    # If the player dies in a world named "end"
    action: "not remove hearts"
    # They won't lose the hearts and won't drop the item
```

## Restriction Types

| Type          | Description                                                                                                                          | Additional Required Properties                                                                                                                                |
|---------------|--------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `death cause` | Restrict based on last damage prior to death (fall, drowning, etc.).                                                                 | `cause`: The cause of the death. Check [here](https://jd.papermc.io/paper/1.21.1/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html) for more causes. |
| `same ip`     | Restrict based on the player's IP address. If the player is killed by someone with the same IP address, the action will be executed. | None                                                                                                                                                          |
| `world`       | Restrict based on the world the player is in.                                                                                        | `world`: The targeted world name.                                                                                                                             |

## Restriction Actions

| Action              | Description                                                          |
|---------------------|----------------------------------------------------------------------|
| `drop`              | The player will drop the heart item and lose the hearts (default).   |
| `not drop`          | The player won't drop the heart item but will still lose the hearts. |
| `not remove hearts` | The player won't lose the hearts and won't drop the item.            |

## Behavior

The executed action will be the first (from top to bottom) that matches the restriction. If no action matches, the
default action (`drop`) will be executed.