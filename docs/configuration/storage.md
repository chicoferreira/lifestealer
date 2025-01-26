# Configuring Storage

The storage configuration is used to define the storage backends that are used by the plugin.

## Database Error Kick

If the storage system encounters an error while loading a player profile, the plugin will disconnect the player with the
message defined in `storage.error kick message` to prevent inconsistent state. This situation typically arises only
if the storage system fails during runtime operations, as the plugin ensures the storage system is available before
initializing.

The default error message is:

```yaml
storage:
  error kick message: "<red>There was an error with the storage system, please contact an administrator."
```

## Moving to a new Storage Type

Follow the steps at [Migrating to a new Storage Type](/usage/migrating-to-a-new-storage-type) to move to a new storage type.

::: warning
The storage type will not be changed with `/lifestealer reload`. You must restart the server to change the storage type.
:::

## Storage Types

The plugin supports the following storage backends:

- H2
- SQLite
- MySQL
- PostgreSQL
- MariaDB

The MySQL, PostgreSQL and MariaDB storage systems use HikariCP to manage a connection pool. If you wish to
configure [properties of HikariCP](https://github.com/brettwooldridge/HikariCP?tab=readme-ov-file#gear-configuration-knobs-baby),
add `storage.extra properties` to the configuration.

```yaml
storage:
  extra properties:
    maximumPoolSize: 20
    minimumIdle: 5
```

The H2 and SQLite storage systems keep a connection open to the database (file) until the plugin is stopped.

### Using H2 (Default)

```yaml
storage:
  type: "h2"
  path: "users.db"
```

### Using SQLite

```yaml
storage:
  type: "sqlite"
  path: "users.db"
```

### Using MySQL

```yaml
storage:
  type: "mysql"
  address: "localhost:3306"
  database: "minecraft"
  username: "root"
  password: "password"
```

MySQL storage type already includes
the [HikariCP recommended configuration](https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration).

### Using PostgreSQL (Recommended)

```yaml
storage:
  type: "postgresql"
  address: "localhost:5432"
  database: "minecraft"
  username: "root"
  password: "password"
```

### Using MariaDB

```yaml
storage:
  type: "mariadb"
  address: "localhost:3306"
  database: "minecraft"
  username: "root"
  password: "password"
```
