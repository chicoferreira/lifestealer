# Lifestealer Usage Overview

Lifestealer is a lifesteal hearts mechanic plugin for Minecraft servers. It introduces a gameplay element where players
can steal hearts from one another making temporary bans if a player's hearts drop too low.

## Key Features

- **Lifesteal Mechanics**  
  When a player gets killed, a heart item is dropped from them. If a player consumes that heart item, they gain
  the number of hearts specified in the item.

- **Maximum amount of hearts**  
  Players can only gain a maximum amount of hearts from consuming heart items. This maximum amount is configurable
  by player permissions and can be fine-tuned for each player.

- **Minimum amount of hearts**
  Players can only lose a minimum amount of hearts from consuming heart items. This minimum amount is configurable
  by player permissions and can be fine-tuned for each player.

- **Ban System**
  When a player’s heart count falls below a defined minimum, Lifestealer can automatically ban them for a specified
  period. You can:
    - Customize ban and join messages.
    - Execute custom console commands upon ban.
    - Optionally disable the built-in ban functionality in favor of an external ban system.

- **Flexible Rules and Restrictions**  
  Define rules that adjust players’ maximum hearts, minimum hearts, ban durations, and return hearts based on their
  permissions. Additionally, set restrictions on heart drops (e.g., based on death cause, same IP, or world) to tailor
  the gameplay experience.

- **Multiple Storage Backends**  
  Choose from H2, SQLite, MySQL, PostgreSQL, or MariaDB to store player data. The plugin also supports migration between
  storage types via export/import commands.

- **Rich Message Customization**  
  Utilize MiniMessage formatting to design custom notifications. You can combine chat messages, action bar messages,
  titles, sounds and particle effects for every message.

- **Extensibility and Integration**
    - **Developer API:** Extend and modify Lifestealer behavior through a Developer API.
    - **PlaceholderAPI Support:** Integrate PlaceholderAPI placeholders in messages to dynamically insert player data.

For more detailed information, please refer to the following guides:

- [Getting Started](../intro/getting-started.md)
- [Configuration Overview](../configuration/overview.md)
- [Developer Reference](../developer/reference.md)
- [Command Documentation](../usage/commands.md)