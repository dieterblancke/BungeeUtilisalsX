# PlaceHolders

**NOTE:** The placeholders displayed here are not all placeholders! There are many, message-specific placeholders available. For this, check out the [language files](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/resources/languages/).

# General PlaceHolders
| PlaceHolder | Explanation |
| --- | --- |
| {timeleft: targetTime} | Shows the time left untill a certain time. targetTime must be of format dd-MM-yyyy hh:mm:ss, for example 01-01-2020 00:00:00, which would be first of january 2020, midnight. |
| {getcount: server} | Shows the amount of players online on a server. server can be both a ServerGroup or a ServerName. |
| {user} | Gets replaced by the user's name |
| {ping} | Gets replaced by the user's ping |
| {proxy_online} | Gets replaced by the total amount of players online (redis is supported) |
| {proxy_max} | Gets replaced by the max players you have setup in your BungeeCord config |

# Punishment PlaceHolders
| PlaceHolder | Explanation |
| --- | --- |
| {reason} | Gets replaced with the punishment reason |
| {date} | Gets replaced with the punishment execution date |
| {by} | Gets replaced with the punisher name |
| {server} | Gets replaced with the server where it was executed on |
| {uuid} | Gets replaced with the punished person his uuid |
| {ip} | Gets replaced with the punished person his IP |
| {user} | Gets replaced with the punished person his name |
| {id} | Gets replaced with the punishment ID |
| {expire} |Gets replaced with the expiry date (if temporary punishment) |

# MOTD PlaceHolders
| PlaceHolder | Explanation |
| --- | --- |
| {user} | Gets replaced with the username, "Unknown" if username is not known. |
| {version} | Gets replaced with the client version, "Unknown" if version is not found / not known. |
| {domain} | Gets replaced with the used domain, "Unknown" if not found. |