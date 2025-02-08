# Migrating to a new Storage Type

You can migrate storage types without losing any data.

## Steps

1. Make sure no one is using the plugin while you are changing the storage type.
2. Export the data from the current storage type using `/lifestealer export`.
    - A file named `lifestealer_users_<date>.json` will be created under the `exports` folder in the plugin folder.
    - You can also name this file by providing a name after the command (e.g. `/lifestealer export data-from-mysql`).
3. Stop the server.
4. Change the storage type in the `config.yml` file (see [Storage Options](/configuration/storage#storage-types)).
5. Start the server.
6. Check if the plugin started successfully (the plugin won't start if it couldn't connect to the database).
7. Import the data using `/lifestealer import <path>`, where `<path>` is the path to the exported data file, relative to
   the `exports` folder.
    - You should be able to autocomplete the path using tab completion.
8. Done.

## Generated Export File Format

```json
[
  {
    "uuid": "e0980be7-b682-3dce-af99-a5d35421e77c",
    "hearts": 10,
    "modifierRules": {
      "maxHearts": 0,
      "minHearts": 0,
      "banTime": {
        "seconds": 0,
        "nano": 0
      },
      "returnHearts": 0
    }
  },
  {
    "uuid": "57893787-d0d1-42e0-b7bd-becb1234743f",
    "hearts": 15,
    "modifierRules": {
      "maxHearts": 0,
      "minHearts": 0,
      "banTime": {
        "seconds": 0,
        "nano": 0
      },
      "returnHearts": 0
    }
  }
]
```