# Configuring Heart Items

You are able to configure heart items as you like. You can configure the items in the `hearts` section of the
configuration file.

## Example

```yaml
items:
  - name: "customheart"
    heart amount: 1
    item:
      type: "paper"
      display name: "<color:#ff0000>Normal Heart</color>"
      lore:
        - "<gray>Right click to add <white>+1<heart></white> to your health"
      enchantments:
        - name: "unbreaking"
          level: 1
      glint: true
      custom model data: 144
      flags:
        - "hide enchants"
```

## Properties

| Property                 | Type                | Description                                                         | Example                                                              |
|--------------------------|---------------------|---------------------------------------------------------------------|----------------------------------------------------------------------| 
| `name`                   | String              | The name of the heart item. This is used to identify the item.      | `"customheart"`                                                      |
| `heart amount`           | Positive Integer    | The amount of hearts the player will gain when consuming the heart. | `1`                                                                  |
| `item.type`              | Item Type           | The type of heart item.                                             | `"paper"`                                                            |
| `item.display name`      | Component           | The display name of the heart item.                                 | `"<red>Basic Heart"`                                                 |
| `item.lore`              | List of Component   | The lore of the heart item.                                         | `"<gray>Right click to add <white>+1<heart></white> to your health"` |
| `item.glint`             | Boolean             | Whether the heart item should glow.                                 | `true`                                                               |
| `item.enchantments`      | List of Enchantment | The list of the item enchantments                                   | Check [example](#example)                                            |
| `item.flags`             | List of Flags       | The flags of the heart item.                                        | `["hide enchants"]`                                                  |
| `item.custom model data` | Integer             | The custom model data of the heart item.                            | `144`                                                                |

## Defining which heart items should be dropped when a player dies

The `item to drop when player dies` field is used to define which heart item should be dropped when a player dies.

This must exist in the `items` list above, otherwise the plugin will not load.

The amount of hearts the player will lose will be the same as the `heart amount` defined in the chosen item, unless
changed by other plugin that hooks into Lifestealer (See [Developer API](/developer/events)).

## Adding new heart items

You can add how many heart items you want to the list.

```yaml
items:
  - name: "basic"
    heart amount: 1
    item:
      type: "paper"
  - name: "advanced"
    heart amount: 3
    item:
      type: "apple"
```

The only required properties are `name`, `heart amount` and `item.type`.

You can give these heart items to players by using the `/lifestealer item give <heart> [player] [amount]` command.
