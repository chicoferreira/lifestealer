# Configuring Duration Formats

You can configure the duration format in the `duration format` section of the configuration file.

## Example

```yaml
# The format for the duration placeholders
duration format:
  # You can add more formats and use them in the messages with <duration:'<format>'>
  extended:
    # The separator between the different units
    separator: ", "
    # The separator between the last two units
    last separator: " and "
    # The translations for the units
    translations:
      days:
        singular: " day"
        plural: " days"
      hours:
        singular: " hour"
        plural: " hours"
      minutes:
        singular: " minute"
        plural: " minutes"
      seconds:
        singular: " second"
        plural: " seconds"
    # The amount of units to show, 0 will show all units
    # Example: For the duration 1d 2h 3m 4s with 3 units, it will show "1 day, 2 hours and 3 minutes"
    amount of units to show: 3
    # If it should show zero values
    show zero values: false
```

## Properties

### `separator` and `last separator`

These properties control the delimiters between the time units.

#### Example

```yaml
duration format:
  example:
    separator: ", "
    last separator: " and "
```

**Output:**
`1d, 2h, 3m and 1s`

### `translations`

This section defines how each time unit should be labeled, both in singular and plural forms.

#### Example

```yaml
duration format:
  example:
    translations:
      days:
        singular: " day"
        plural: " days"
      hours:
        singular: " hour"
        plural: " hours"
      minutes:
        singular: " minute"
        plural: " minutes"
      seconds:
        singular: " second"
        plural: " seconds"
```

**Output:** `1 day 2 hours 3 minutes 1 second`

### `amount of units to show`

This setting specifies how many time units will appear in the output.

#### Example

```yaml
duration format:
  example:
    amount of units to show: 3
```

**Output:** `1 day, 2 hours and 3 minutes` 

> In this example, seconds are omitted because the largest unit is days and only three units are allowed.

---

```yaml
duration format:
  example:
    amount of units to show: 2
```

**Output:** `1 day and 2 hours`

### `show zero values`

This option determines whether units with a value of zero should be displayed.

#### Example 1: Showing Zero Values

```yaml
duration format:
  example:
    show zero values: true
    amount of units to show: 3
```

**Output:** `1 day, 0 hours and 3 minutes` 

> Here, seconds are omitted due to the maximum limit of three units.

#### Example 2: Hiding Zero Values

```yaml
duration format:
  example:
    show zero values: false
    amount of units to show: 3
```

**Output:** `1 day, 3 minutes and 1 second` 

> In this scenario, hours are omitted because their value is 0, but seconds
are displayed as the maximum unit count has not yet been reached.