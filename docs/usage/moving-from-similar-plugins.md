# Moving from Similar Plugins

Currently, Lifestealer **does not support** importing data from other plugins.

This is a feature that is not planned to be added in the future due to the differences in the way data is stored
and managed by each plugin.

Lifestealer mainly uses permissions to attribute [rules](/usage/rules) to players, while other plugins may not use
permissions at all.

For example, other plugin might have `PlayerA` with 15 max hearts and `PlayerB` with 20 max hearts. `PlayerA` purchased
a rank that gives them 20 max hearts but `PlayerB` obtained 15 max hearts by getting a specific item for example.
In this case, Lifestealer should have a group rule for the rank that gives 20 max hearts and a modifier rule for
`PlayerB` that gives them 15 max hearts.

This is not something that can be automatically converted from another plugin without knowing more specific rules
about the server environment.

My suggestion is to ask ChatGPT or other similar AI to generate a simple script that converts the data from your current
plugin to [Lifestealer's import format](/usage/migrating-to-a-new-storage-type.html#generated-export-file-format) by
your own migration rules. This ensures that the data is converted to your liking and the rules are correctly attributed
to the players.