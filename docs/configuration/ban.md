# Configuring ban settings

You can configure the ban settings in the `ban settings` section of the configuration file.

## Example

```yaml
# Ban system configuration
# When a player loses more hearts than the minimum amount defined in the rules, they will be banned for the time defined in the rules
ban settings:
  # The message that will be sent to the player when they get banned and is online
  kick message: "<red>You have been banned for <white><duration:'extended'><red> for losing too many hearts.\nYou will be unbanned at <white><date:'yyyy-MM-dd HH:mm:ss'><red>."

  # The message that will be sent to the player when they try to join while banned
  join message: "<red>You are banned for <white><remaining:'extended'><red> for losing too many hearts.\nYou will be unbanned at <white><date:'yyyy-MM-dd HH:mm:ss'><red>."

  # The commands that will be executed by the console when a player gets bellow the minimum amount of hearts
  # The following placeholders can be used:
  # - <player>: the player's name that will be banned
  # - <duration>: the time in seconds the player will be banned for
  commands:
    - "say <player> has been eliminated for <duration>s for losing too many hearts (remove this message in the config)"
  #    - "ban <player> <duration> Hearts lost"

  # If you want to not use the ban system of Lifestealer and wish to use your own, set this to true
  # This will prevent the plugin from banning players or not allow them to join the server
  #
  # The commands in "commands" will still be executed even if this is set to true
  # and will also save the player ban status internally in the database but won't use
  # it for anything (you can lookup that data using the developer API)
  external: false
```

You can configure the format of the `<duration>` placeholders in the [Duration Format](/configuration/duration) section.

## Using an external ban system

If you do not want to use the built-in ban system of Lifestealer, you can set the `external` field to `true` and
Lifestealer will neither kick nor ban players when they lose more hearts than the minimum amount defined by their rules.

The commands in the `commands` field will be executed either way, so you can use it to trigger other ban systems.

You can still use the Developer API (TODO: Link to developer API) and the [PAPI Placeholders](/integrations/papi) to
check if a player is banned or not.

