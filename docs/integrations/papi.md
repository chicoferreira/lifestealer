# Integration with PlaceholderAPI

Lifestealer plugin is highly compatible with [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI).

## List of placeholders

| Placeholder                            |                                        Description                                        | Available Offline | Output Example |
|----------------------------------------|:-----------------------------------------------------------------------------------------:|:-----------------:|:--------------:|
| `%lifestealer_max_hearts_modifier%`    |  [Max hearts](/usage/terminology#maximum-hearts) [modifier](/usage/terminology#modifier)  |        Yes        |      `0`       | 
| `%lifestealer_min_hearts_modifier%`    |  [Min hearts](/usage/terminology#minimum-hearts) [modifier](/usage/terminology#modifier)  |        Yes        |      `0`       | 
| `%lifestealer_ban_time_modifier%`      |                Ban time [modifier](/usage/terminology#modifier) in seconds                |        Yes        |      `0`       |  
| `%lifestealer_return_hearts_modifier%` | [Return hearts](/usage/terminology#return-hearts) [modifier](/usage/terminology#modifier) |        Yes        |      `0`       |
| `%lifestealer_hearts%`                 |                        The number of hearts the user currently has                        |        Yes        |      `10`      | 
| `%lifestealer_ban%`                    |                    Ban remaining time in seconds (null if not banned)                     |        Yes        |  null or `46`  |
| `%lifestealer_health%`                 |                                The current player's health                                |        No         |     `20.0`     |
| `%lifestealer_max_hearts%`             |                      [Max hearts](/usage/terminology#maximum-hearts)                      |        No         |      `20`      |
| `%lifestealer_min_hearts%`             |                      [Min hearts](/usage/terminology#minimum-hearts)                      |        No         |      `3`       |
| `%lifestealer_return_hearts%`          |                  [User return hearts](/usage/terminology#return-hearts)                   |        No         |      `7`       |
| `%lifestealer_ban_time%`               |                   User ban time in seconds if they lose too many hearts                   |        No         |      `15`      |
| `%lifestealer_inventory_<item>%`       |           The number of heart items of type `<item>` in the player's inventory            |        No         |      `1`       |

If you are in doubt of any terminology used in the placeholders, please refer to
the [Terminology](/usage/overview#terminology) section.

## Use PlaceholderAPI in Lifestealer Messages

You can use PlaceholderAPI in Lifestealer messages with the `<papi>` tag.

For example, if you want to prefix the player's name with their
LuckPerms prefix (`%luckperms_prefix%`), you can use the following configuration:

```yaml
messages:
  command unban success: "Unbanned <papi:luckperms_prefix> <target>."
```