# Lifestealer Command Documentation

Lifestealer provides a robust set of commands to manage players' hearts, heart items, bans, rule modifiers, and data
storage. All commands require the `lifestealer.admin` permission. When a player is not specified, many commands default
to acting upon the command sender.

| **Command**                                                          | **Description**                                                            | **Documentation**                                       |
|----------------------------------------------------------------------|----------------------------------------------------------------------------|---------------------------------------------------------|
| `/lifestealer hearts set <amount> [player]`                          | Sets the heart count of a player.                                          | [Documentation](/usage/commands#set-hearts)             |
| `/lifestealer hearts add <amount> [player]`                          | Adds hearts to a player's current count.                                   | [Documentation](/usage/commands#add-hearts)             |
| `/lifestealer hearts remove <amount> [player]`                       | Removes hearts from a player's current count.                              | [Documentation](/usage/commands#remove-hearts)          |
| `/lifestealer item give <item> [amount] [player]`                    | Gives heart items to a player.                                             | [Documentation](/usage/commands#give-heart-items)       |
| `/lifestealer item take <item> [amount] [player]`                    | Removes heart items from a player's inventory.                             | [Documentation](/usage/commands#take-heart-items)       |
| `/lifestealer item list`                                             | Lists all configured heart items.                                          | [Documentation](/usage/commands#list-heart-items)       |
| `/lifestealer user ban <player> [duration]`                          | Bans a player for a specified duration (or default) based on heart rules.  | [Documentation](/usage/commands#ban-a-user)             |
| `/lifestealer user unban <player>`                                   | Unbans a previously banned player.                                         | [Documentation](/usage/commands#unban-a-user)           |
| `/lifestealer user info [player]`                                    | Displays detailed information about a player's heart status and modifiers. | [Documentation](/usage/commands#user-information)       |
| `/lifestealer user rulemodifier set <rule> <value> [player]`         | Sets a specific rule modifier for a player's configuration values.         | [Documentation](/usage/commands#set-a-rule-modifier)    |
| `/lifestealer user rulemodifier adjust <rule> <adjustment> [player]` | Adjusts (adds or subtracts) a player's rule modifier.                      | [Documentation](/usage/commands#adjust-a-rule-modifier) |
| `/lifestealer user rulemodifier reset [player]`                      | Resets all rule modifiers for a player to their default state.             | [Documentation](/usage/commands#reset-rule-modifiers)   |
| `/lifestealer reload`                                                | Reloads the Lifestealer plugin configuration.                              | [Documentation](/usage/commands#reload-comand)          |
| `/lifestealer storage import <file>`                                 | Imports player data from the specified file.                               | [Documentation](/usage/commands#import-storage-data)    |
| `/lifestealer storage export [file]`                                 | Exports player data to a file; uses a generated name if none is provided.  | [Documentation](/usage/commands#export-storage-data)    |

Below is an overview of the available commands, their usage, and examples.

## Base Command

All Lifestealer commands are subcommands of the base command:

```bash
/lifestealer
```

## Hearts Commands

These commands allow you to modify a player's heart count.

### Set Hearts

**Usage:**

```bash
/lifestealer hearts set <amount> [player]
```

- **`<amount>`**: The new heart value to set (integer).
- **`[player]`**: *(Optional)* The target player. If omitted, the command applies to the sender.

**Example:**

- Set your hearts to 10:
  ```bash
  /lifestealer hearts set 10
  ```
- Set Steve's hearts to 8:
  ```bash
  /lifestealer hearts set 8 Steve
  ```

### Add Hearts

**Usage:**

```bash
/lifestealer hearts add <amount> [player]
```

- **`<amount>`**: The number of hearts to add (integer).
- **`[player]`**: *(Optional)* The target player. If omitted, the command applies to the sender.

**Example:**

- Add 2 hearts to your total:
  ```bash
  /lifestealer hearts add 2
  ```
- Add 3 hearts to Alex:
  ```bash
  /lifestealer hearts add 3 Alex
  ```

### Remove Hearts

**Usage:**

```bash
/lifestealer hearts remove <amount> [player]
```

- **`<amount>`**: The number of hearts to remove (integer).
- **`[player]`**: *(Optional)* The target player. If omitted, the command applies to the sender.

**Example:**

- Remove 1 heart from your total:
  ```bash
  /lifestealer hearts remove 1
  ```
- Remove 2 hearts from Steve:
  ```bash
  /lifestealer hearts remove 2 Steve
  ```

## Item Commands

These commands manage heart items which players can use to gain hearts.

### Give Heart Items

**Usage:**

```bash
/lifestealer item give <item> [amount] [player]
```

- **`<item>`**: The identifier (or type name) of the heart item.  
  *(Use `/lifestealer item list` to view available items.)*
- **`[amount]`**: *(Optional)* The number of items to give. Defaults to 1 if omitted.
- **`[player]`**: *(Optional)* The target player. If omitted, the command applies to the sender.

**Example:**

- Give yourself one "customheart":
  ```bash
  /lifestealer item give customheart
  ```
- Give Steve 3 "customheart" items:
  ```bash
  /lifestealer item give customheart 3 Steve
  ```

### Take Heart Items

**Usage:**

```bash
/lifestealer item take <item> [amount] [player]
```

- **`<item>`**: The identifier (or type name) of the heart item.
- **`[amount]`**: *(Optional)* The number of items to remove. Defaults to 1 if omitted.
- **`[player]`**: *(Optional)* The target player. If omitted, the command applies to the sender.

**Example:**

- Remove one "customheart" from your inventory:
  ```bash
  /lifestealer item take customheart
  ```
- Remove 2 "basic" heart items from Alex:
  ```bash
  /lifestealer item take basic 2 Alex
  ```

### List Heart Items

**Usage:**

```bash
/lifestealer item list
```

**Description:**  
Displays all configured heart items with details such as the heart amount and item properties.

## User Commands

These commands manage player bans, information, and rule modifiers.

### Ban a User

**Usage:**

```bash
/lifestealer user ban <player> [duration]
```

- **`<player>`**: The target player to ban.
- **`[duration]`**: *(Optional)* The ban duration. Accepts duration formats like `1d`, `2h`, `30m`, or `15s`. If
  omitted, a default duration is applied.

**Example:**

- Ban Steve with the default duration:
  ```bash
  /lifestealer user ban Steve
  ```
- Ban Alex for 1 hour:
  ```bash
  /lifestealer user ban Alex 1h
  ```

### Unban a User

**Usage:**

```bash
/lifestealer user unban <player>
```

- **`<player>`**: The target offline player to unban.

**Example:**

```bash
/lifestealer user unban Steve
```

### User Information

**Usage:**

```bash
/lifestealer user info [player]
```

- **`[player]`**: *(Optional)* The player to retrieve information about. If omitted, the information for the sender is
  displayed.

**Description:**  
Shows detailed data including:

- Current heart count
- Ban status and remaining ban time
- Configured maximum, minimum, and return hearts
- Ban time settings
- Applied rule modifiers and their effects

**Example:**

- View your own information:
  ```bash
  /lifestealer user info
  ```
- View information for Alex:
  ```bash
  /lifestealer user info Alex
  ```

### Rule Modifier Commands

Rule modifiers adjust a player’s base heart-related configuration values (such as max hearts, min hearts, ban time, and
return hearts).

#### Set a Rule Modifier

**Usage:**

```bash
/lifestealer user rulemodifier set <rule> <value> [player]
```

- **`<rule>`**: The rule to modify. Valid values are:
    - `maxhearts`
    - `minhearts`
    - `bantime` (value in seconds)
    - `returnhearts`
- **`<value>`**: The new modifier value.
- **`[player]`**: *(Optional)* The target player. Defaults to the sender if omitted.

**Example:**

- Set your maximum hearts modifier to 5:
  ```bash
  /lifestealer user rulemodifier set maxhearts 5
  ```
- Set Steve's ban time modifier to 30 seconds:
  ```bash
  /lifestealer user rulemodifier set bantime 30 Steve
  ```

#### Adjust a Rule Modifier

**Usage:**

```bash
/lifestealer user rulemodifier adjust <rule> <adjustment> [player]
```

- **`<rule>`**: The rule to adjust (same valid values as above).
- **`<adjustment>`**: The amount to add to (or subtract from) the current modifier (can be positive or negative).
- **`[player]`**: *(Optional)* The target player.

**Example:**

- Decrease your minimum hearts modifier by 2:
  ```bash
  /lifestealer user rulemodifier adjust minhearts -2
  ```
- Increase Alex's ban time modifier by 15 seconds:
  ```bash
  /lifestealer user rulemodifier adjust bantime 15 Alex
  ```

#### Reset Rule Modifiers

**Usage:**

```bash
/lifestealer user rulemodifier reset [player]
```

- **`[player]`**: *(Optional)* The target player. If omitted, resets the sender's rule modifiers.

**Description:**  
Resets all rule modifiers for the specified player to zero.

**Example:**

- Reset your rule modifiers:
  ```bash
  /lifestealer user rulemodifier reset
  ```
- Reset Steve's rule modifiers:
  ```bash
  /lifestealer user rulemodifier reset Steve
  ```

## Reload Command

Reload the plugin configuration without restarting the server.

**Usage:**

```bash
/lifestealer reload
```

## Storage Commands

These commands handle data import and export operations.

### Import Storage Data

**Usage:**

```bash
/lifestealer storage import <file>
```

- **`<file>`**: The relative path to the file in the exports folder containing the data to import.

**Example:**

```bash
/lifestealer storage import lifestealer_users_2025-02-06_12-30-00.json
```

### Export Storage Data

**Usage:**

```bash
/lifestealer storage export [file]
```

- **`[file]`**: *(Optional)* The desired file name for the export. If omitted, a default file name is generated based on
  the current date and time.

**Example:**

- Export with a default file name:
  ```bash
  /lifestealer storage export
  ```
- Export with a custom file name:
  ```bash
  /lifestealer storage export backup_data
  ```

This will export the data to a JSON file based
on [this format](/usage/migrating-to-a-new-storage-type.html#generated-export-file-format).