# Getting Started

This is a guide to help you get started with the Lifestealer plugin. It will walk you through the process of installing
the plugin on your server and configuring it to your liking.

## Installation

To install the Lifestealer plugin on your server, you will need to download the latest version of the plugin from the
store you purchased it from. Once you have the plugin file, you can upload it to your server's `plugins` directory.

The plugin should be compatible with any Paper server running Minecraft 1.21 or later. If you see any errors or issues
when starting your server with the plugin installed, please check the console for error messages and report them here:
[Creating a bug report](https://github.com/chicoferreira/lifestealer/issues/new?template=bug_report.md).

## Configuration

If the plugin has been installed successfully, you can configure it by editing the `config.yml` file in the plugin's
directory. This file contains all the settings and options for the plugin. The plugin should work out of the box with
no database or additional configuration required.

A default `H2` database is created to store player data and settings. If you would like to use a different database,
check [Configuring Storage](/configuration/storage) for more information. You can change databases without losing any
data with the Import/Export feature.

## Missing Features

If you believe Lifestealer could benefit from an additional feature, please
consider [submitting a feature request](https://github.com/chicoferreira/lifestealer/issues/new?template=feature_request.md).

## Reporting a bug

If you encounter any issues with Lifestealer, please consider
[creating a bug report](https://github.com/chicoferreira/lifestealer/issues/new?template=bug_report.md).

## Currently supported versions

The plugin is developed and compiled against Paper 1.21.1 and API version 1.21.

### List of tested versions

- 1.21.4
- 1.21.3
- 1.21.1
- 1.21

Versions under 1.21 are not supported.