# Configurations

# Information
All configuration files [can be found here](https://github.com/dieterblancke/BungeeUtilisalsX/tree/master/bungee/src/main/resources). Just ignore the schemas folder & the bungee.yml file.

# Customization
BungeeUtilisalsX is fully focussed on being customizable:
- all (sub)commands are fully customizable, meaning that:
    * The command can be enabled / disabled
    * The command name can be changed
    * The command aliases can be changed
    * The permissions for commands can be changed
    * The command can be set up with a cooldown
- all messages are configurable ([see languages folder](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/languages/))
- if anything is found that is not configurable (enough), I will change it

# config.yml
[Click here to see the default config.yml file](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/config.yml)

This configuration contains:
- whether or not debug mode, custom packets & addons should be enabled
- storage settings
- updater settings

# servergroups.yml
[Click here to see the default servergroups.yml file](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/servergroups.yml)

This configuration file allows you to create custom server groups. <br />
For example, if you have 2 lobbies, you can group them together in this file as following:

```yml
groups:
  - name: 'Lobbies'
    servers:
      - 'Lobby*'
```
Throughout all BungeeUtilisalsX configuration files, you will be able to use the servergroup 'Lobbies' (for example to send an announcement only to lobbies).

# punishments.yml
[Click here to see the default punishments.yml file](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/punishments.yml)

This configuration contains everything about the punishments system, you can:
- enable / disable punishment system
- change the date format
- setup punishment templates (customizable punishment reasons using a short identifier)
- punishment actions (when someone got warned X times in X period of time, another punishment can get executed (f.e. tempmute))
- blocked commands while muted

# motd.yml
[Click here to see the default motd.yml file](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/motd.yml)

For more info regarding how the MOTD system works, [please check out it's wiki page](motd.md).

# friends.yml
[Click here to see the default friends.yml file](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/friends.yml)

This configuration allows you to:
- enable / disable friends system
- create friend limit permissions
- set the default friend settings