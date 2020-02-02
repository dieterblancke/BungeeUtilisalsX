# Table of contents
{:toc}

# Legenda
- ( argument ) = a required argument
- [ argument ] = an optional argument

# General Commands
## /bungeeutilisals
**Usage:** /bungeeutilisals<br>
**Aliases:** /bu, /butili, /butilisals <br>
**Permission:** bungeeutilisals.admin <br>

### /bungeeutilisals version
**Permission:** bungeeutilisals.admin.version <br>
Shows you the version you are currently running on.

### /bungeeutilisals reload
**Permission:** bungeeutilisals.admin.reload <br>
Reloads the plugin

### /bungeeutilisals dump
**Permission:** bungeeutilisals.admin.dump <br>

Creates a dump on https://paste.dbsoftwares.eu with information that can be useful for an issue.

### /bungeeutilisals convert (oldtype) [properties]
**Permission:** bungeeutilisals.admin.convert <br>
Allows you to convert plugin data when you switch storage type.

Example: /bungeeutilisals convert MYSQL host:127.0.0.1,port:3306,database:bu_old,username:bungeeutilisals,password:test <br>
This example will convert from MySQL (with the given properties) to the current storage implementation.

### /bungeeutilisals import (plugin) [properties]
**Permission:** bungeeutilisals.admin.import <br>
Allows you to import plugin data when you switch storage type. <br>
**Supported plugins:** BungeeUtilisals (old version), BungeeAdminTools

Example: /bungeeutilisals import BAT host:127.0.0.1,port:3306,database:bu_old,username:bungeeutilisals,password:test <br>
This example will import data from BungeeAdminTools (with the given properties) to the current storage implementation.

## /glist
This is a customizable alternative for the default /glist command, with redis support. **Note:** Make sure to remove the glist module.

**Usage:** /glist <br>
**Aliases:** /blist, /globallist <br>
**Default Permission:** bungeeutilisals.commands.glist <br>

## /announce (p/b/c/a/t) (message)
This is an 'upgraded' alternative for the default /alert command, as this allows you to broadcast a chat, title, bossbar and / or actionbar message throughout your server.

**Types explanation:**
- p: preconfigured, this is the path to the preconfigured message in the languages file (this also allows an alert to be multilingual), by default, */announce p welcome* should work
- c: chat
- t: title, to split a title into a title and subtitle you can use %sub%, so for example: &c&lBungeeUtilisalsX %sub%&eThank you for using our plugin!
- b: bossbar
- a: actionbar

**Usage:** /announce (p/b/c/a/t) (message) <br>
**Aliases:** /blist, /globallist <br>
**Default Permission:** bungeeutilisals.commands.glist <br>

## /find (user)
This is a customizable alternative for the default /find command, with redis support. **Note:** Make sure to remove the find module.

**Usage:** /find (user) <br>
**Aliases:** /search <br>
**Default Permission:** bungeeutilisals.commands.find <br>

## /server (server)
This is a customizable alternative for the default /server command. **Note:** Make sure to remove the server module.

**Usage:** /server (server) <br>
**Aliases:** /switch <br>
**Default Permission:** bungeeutilisals.commands.server <br>

## /clearchat (local / global)
This command allows you to clear the chat of the entire network, or of the server you are in.

**Usage:** /clearchat (local / global) <br>
**Aliases:** /cc <br>
**Default Permission:** bungeeutilisals.commands.clearchat <br>

## /chatlock (local / global)
This command allows you to lock the chat of the entire network, or of the server you are in.

**Usage:** /chatlock (local / global) <br>
**Aliases:** /cl, /lock, /lockchat <br>
**Default Permission:** bungeeutilisals.commands.chatlock <br>
**Default Bypass Permission:** bungeeutilisals.chatlock.bypass <br>

## /glag
This command shows you basic statistics of your server, such as tps, max, free and total memory and last startup timestamp.

**Usage:** /glag <br>
**Aliases:** /bgc, /blag <br>
**Default Permission:** bungeeutilisals.commands.glag <br>

## /staffchat
This command allows staff to communicate with eachother in a separate staff chat.

**Usage:** /staffchat <br>
**Aliases:** /sc, /schat <br>
**Default Permission:** bungeeutilisals.commands.staffchat <br>

## /language (language)
This command allows people to change their language to one of the languages supported by the plugin.

**Usage:** /language (language) <br>
**Aliases:** /lang <br>
**Default Permission:** bungeeutilisals.commands.language <br>

## /staff
This is command shows the online staff per role. These roles can be installed in the commands/generalcommands.yml file.

**Usage:** /staff <br>
**Aliases:** /onlinestaff <br>
**Default Permission:** bungeeutilisals.commands.staff <br>

## /msg (user) (message)
This is command allows you to send a PM to anyone who is online in the network (redis supported) (target must ofcourse not have ignored you).

**Usage:** /msg (user) (message) <br>
**Aliases:** /m, /whisper <br>
**Default Permission:** bungeeutilisals.commands.msg <br>

## /reply (message)
This is command allows you to reply to the last person who sent you a PM.

**Usage:** /reply (message) <br>
**Aliases:** /r <br>
**Default Permission:** bungeeutilisals.commands.reply <br>

## /ignore (add / remove / list) [user]
This is command allows you to add / remove someone to / from your ignored members list. Or it lists the people you are ignoring.

**Usage:** /ignore (add / remove / list) [user] <br>
**Aliases:** /bignore <br>
**Default Permission:** bungeeutilisals.commands.ignore <br>

## /ping [user]
Shows your ping, or the ping of another user towards the **BungeeCord**.

**Usage:** /ping [user] <br>
**Aliases:** /gping, /bping <br>
**Default Permission:** bungeeutilisals.commands.ping <br>
**Default Other Permission:** bungeeutilisals.commands.ping.other <br>

# Addon Commands
## /addon enable (name)
Enables an addon that has a JAR file present, but isn't enabled yet.

**Permission:** bungeeutilisals.admin.addons.enable

## /addon disable (name)
Disables an addon, but doesn't remove the files it has.

**Permission:** bungeeutilisals.admin.addons.disable

## /addon info (name)
Shows information about an addon.

**Permission:** bungeeutilisals.admin.addons.info

## /addon list
Shows all exising addons, and whether or not they are installed & enabled.

**Permission:** bungeeutilisals.admin.addons.list

## /addon install (addon)
Installs an addon from our web api, and enables it.

**Permission:** bungeeutilisals.admin.addons.install

## /addon uninstall (addon)
Uninstalls an addon (by removing the jar file) and disables it.

**Permission:** bungeeutilisals.admin.addons.uninstall

## /addon reload (addon)
Reloads an addon.

**Permission:** bungeeutilisals.admin.addons.reload

# Punishment Commands
Possible punishment formats:
- Year formats:
  + 1y
  + 1year
  + 2years
- Month formats:
  + 1mo
  + 1month
  + 2months
- Week formats:
  + 1w
  + 1week
  + 2weeks
- Day formats:
  + 1d
  + 1day
  + 2days
- Hour formats:
  + 1h
  + 1hour
  + 2hours
- Minute formats:
  + 1m
  + 1min
  + 2mins
- Second formats:
  + 1s
  + 1sec
  + 2secs

## /ban (user) (reason)
This command permanently bans someone from the BungeeCord network.

**Usage:** /ban (user) (reason) <br>
**Aliases:** /pban, /gban <br>
**Default Permission:** bungeeutilisals.punishments.ban <br>
**Default Broadcast permission:** bungeeutilisals.punishments.ban.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /ipban (user / IP) (reason)
This command permanently bans someone's ip from the BungeeCord network.

**Usage:** /ipban (user / IP) (reason) <br>
**Aliases:** /banip, /gbanip, /gipban <br>
**Default Permission:** bungeeutilisals.punishments.ipban <br>
**Default Broadcast permission:** bungeeutilisals.punishments.ipban.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /tempban (user) (timeformat) (reason)
This command temporarily bans someone from the BungeeCord network.

**Usage:** /tempban (user) (timeformat) (reason) <br>
**Aliases:** /tban, /gtban <br>
**Default Permission:** bungeeutilisals.punishments.tempban <br>
**Default Broadcast permission:** bungeeutilisals.punishments.tempban.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /iptempban (user / IP) (timeformat) (reason)
This command temporarily bans someone's ip from the BungeeCord network.

**Usage:** /iptempban (user / IP) (timeformat) (reason) <br>
**Aliases:** /iptempban, /tipban, /gtipban, /tbanip <br>
**Default Permission:** bungeeutilisals.punishments.tempbanip <br>
**Default Broadcast permission:** bungeeutilisals.punishments.tempbanip.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /mute (user) (reason)
This command permanently mutes someone from the BungeeCord network.

**Usage:** /mute (user) (reason) <br>
**Aliases:** /pmute, /gmute <br>
**Default Permission:** bungeeutilisals.punishments.mute <br>
**Default Broadcast permission:** bungeeutilisals.punishments.mute.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /ipmute (user / IP) (reason)
This command permanently mutes someone's ip from the BungeeCord network.

**Usage:** /ipmute (user / IP) (reason) <br>
**Aliases:** /muteip, /gmuteip, /gipmute <br>
**Default Permission:** bungeeutilisals.punishments.ipmute <br>
**Default Broadcast permission:** bungeeutilisals.punishments.ipmute.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /tempmute (user) (timeformat) (reason)
This command temporarily mutes someone from the BungeeCord network.

**Usage:** /tempmute (user) (timeformat) (reason) <br>
**Aliases:** /tmute, /gtmute <br>
**Default Permission:** bungeeutilisals.punishments.tempmute <br>
**Default Broadcast permission:** bungeeutilisals.punishments.tempmute.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /iptempmute (user / IP) (timeformat) (reason)
This command temporarily mutes someone's ip from the BungeeCord network.

**Usage:** /iptempmute (user / IP) (timeformat) (reason) <br>
**Aliases:** /iptempmute, /tipmute, /gtipmute, /tmuteip <br>
**Default Permission:** bungeeutilisals.punishments.tempmuteip <br>
**Default Broadcast permission:** bungeeutilisals.punishments.tempmuteip.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /kick (user) (reason)
This command kicks someone from the BungeeCord network.

**Usage:** /kick (user) (reason) <br>
**Default Permission:** bungeeutilisals.punishments.kick <br>
**Default Broadcast permission:** bungeeutilisals.punishments.kick.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /warn (user) (reason)
This command warns someone on the BungeeCord network.

**Usage:** /warn (user) (reason) <br>
**Default Permission:** bungeeutilisals.punishments.warn <br>
**Default Broadcast permission:** bungeeutilisals.punishments.warn.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /unban (user)
This command unbans someone from the BungeeCord network.

**Usage:** /unban (user) <br>
**Aliases:** /punban, /gunban, /unsetban, /removeban <br>
**Default Permission:** bungeeutilisals.punishments.unban <br>
**Default Broadcast permission:** bungeeutilisals.punishments.unban.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /unbanip (user / IP)
This command unbans someone's ip from the BungeeCord network.

**Usage:** /unbanip (user / IP) <br>
**Aliases:** /punbanip, /gunbanip, /unsetbanip, /removebanip <br>
**Default Permission:** bungeeutilisals.punishments.unbanip <br>
**Default Broadcast permission:** bungeeutilisals.punishments.unbanip.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /unmute (user)
This command unmutes someone from the BungeeCord network.

**Usage:** /unmute (user) <br>
**Aliases:** /punmute, /gunmute, /unsetmute, /removemute <br>
**Default Permission:** bungeeutilisals.punishments.unmute <br>
**Default Broadcast permission:** bungeeutilisals.punishments.unmute.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /unmuteip (user / IP)
This command unmutes someone's ip from the BungeeCord network.

**Usage:** /unmuteip (user / IP) <br>
**Aliases:** /punmuteip, /gunmuteip, /unsetmuteip, /removemuteip <br>
**Default Permission:** bungeeutilisals.punishments.unmuteip <br>
**Default Broadcast permission:** bungeeutilisals.punishments.unmuteip.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /punishmentinfo (user) [type / all]
This command shows info about the punishments a certain user currently has.

**Usage:** /punishmentinfo (user) [type / all] <br>
**Aliases:** /pinfo <br>
**Default Permission:** bungeeutilisals.punishments.punishmentinfo <br>
**Default Broadcast permission:** bungeeutilisals.punishments.punishmentinfo.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /punishmenthistory (user) [type / all] [page]
This command shows info about the punishment history of a certain user.

**Usage:** /punishmenthistory (user) [type / all] [page] <br>
**Aliases:** /phistory <br>
**Default Permission:** bungeeutilisals.punishments.punishmenthistory <br>
**Default Broadcast permission:** bungeeutilisals.punishments.punishmenthistory.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

## /punishmentdata (type) (id)
This command shows info about a certain punishment id.

**Usage:** /punishmentdata (type) (id) <br>
**Aliases:** /pdata <br>
**Default Permission:** bungeeutilisals.punishments.punishmentdata <br>
**Default Broadcast permission:** bungeeutilisals.punishments.punishmentdata.broadcast <br>

When someone has the default broadcast permission, they will get a message when someone gets punished.

# Friend Commands
## /friend add (user)
Adds someone to your friend list.

**Usage:** /friend add (user) <br>
**Aliases:** /friends send, /friends request <br>
**Default Permission:** bungeeutilisals.friends.add <br>

## /friend remove (user)
Removes someone from your friend list.

**Usage:** /friend remove (user) <br>
**Aliases:** /friends delete, /friends del <br>
**Default Permission:** bungeeutilisals.friends.remove <br>

## /friend accept (user)
Accepts someone into your friend list.

**Usage:** /friend accept (user) <br>
**Aliases:** /friends approve <br>
**Default Permission:** bungeeutilisals.friends.accept <br>

## /friend deny (user)
Denies someone from your friend list.

**Usage:** /friend deny (user) <br>
**Default Permission:** bungeeutilisals.friends.deny <br>

## /friend removerequest (user)
Removes a request that you sent to someone.

**Usage:** /friend removerequest (user) <br>
**Aliases:** /friends rr <br>
**Default Permission:** bungeeutilisals.friends.removerequest <br>

## /friend list [page]
Lists the friends that you have.

**Usage:** /friend list [page] <br>
**Aliases:** /friends fl <br>
**Default Permission:** bungeeutilisals.friends.list <br>

## /friend requests (in / out) [page]
Lists the incoming and outgoing friend requests that you have.

**Usage:** /friend requests (in / out) [page] <br>
**Aliases:** /friends req <br>
**Default Permission:** bungeeutilisals.friends.requests <br>

## /friend msg (user) (message)
Sends a private message to someone in your friends list.

**Usage:** /friend msg (user) (message) <br>
**Aliases:** /friends m, /friends tell, /friends w, /friends whisper, /friends message <br>
**Default Permission:** bungeeutilisals.friends.msg <br>

## /friend reply (message)
Replies to a private message by / to a friend.

**Usage:** /friend reply (message) <br>
**Aliases:** /friends r <br>
**Default Permission:** bungeeutilisals.friends.reply <br>

## /friend settings (setting) (enable / disable)
Changes user preference for certain settings.

**Usage:** /friend settings (setting) (enable / disable) <br>
**Default Permission:** bungeeutilisals.friends.settings <br>

# ReportCommands
## /report create (user) (reason)
Creates a report against a certain user.

**Usage:** /report create (user) (reason) <br>
**Aliases:** /report c, /report new <br>
**Default Permission:** bungeeutilisals.commands.report.create <br>

## /report list [all/active/accepted/denied] [page]
Lists the reports that are valid to the criteria (all, active, accepted or denied).

**Usage:** /report list [all/active/accepted/denied] [page] <br>
**Aliases:** /report list, /report l <br>
**Default Permission:** bungeeutilisals.commands.report.list <br>

## /report accept (id)
Accepts a report with a certain ID (the ID can be retrieved from /report list)

**Usage:** /report accept (id) <br>
**Default Permission:** bungeeutilisals.commands.report.accept <br>

## /report deny (id)
Denies a report with a certain ID (the ID can be retrieved from /report list)

**Usage:** /report deny (id) <br>
**Default Permission:** bungeeutilisals.commands.report.deny <br>

## /report history [page]
Shows your report history and the data of the report (and whether or not it was handled & accepted yet)

**Usage:** /report history [page] <br>
**Aliases:** /report hist <br>
**Default Permission:** bungeeutilisals.commands.report.history <br>