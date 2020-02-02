# Motd System

# Conditions
> [!NOTE|style:flat]
> Conditions work from top to bottom, when a condition is met, it will use that certain MOTD.

> [!NOTE|style:flat]
> If multiple motd's with the same condition are found, BungeeUtilisals will take a random of these motds to be displayed.

> [!DANGER|style:flat]
> An exception on this is the default condition. The default condition will be executed when no specific condition is met.

## Available Conditions
- domain
- name
- version

## Available operators
### version
| Operator | Read as |
| --- | --- |
| < | _smaller than_ |
| <= | _smaller than or equals_ |
| == | _equals_ |
| != | _not equals_ |
| \>= | _greater than or equals_ |
| \> | _greater than_ |

### domain & name
| Operator | Read as |
| --- | --- |
| == | _equals_ |
| != | _not equals_ |

## Available values
### Domain
Any domain can be used as value, for example:

**For example:**
```yaml
condition: 'domain == play.example.com'
```

### Name
> [!NOTE|style:flat]
> When a name == null, it means that a player (with that IP) never joined before!

Any name or 'null' can be used as value, for example:

**For example:**
```yaml
condition: 'name == null'
```
```yaml
condition: 'name == didjee2'
```

### Version
The following values can be used to check on versions: <br />
1.8,
1.9,
1.9.1,
1.9.2,
1.9.3,
1.10,
1.11,
1.11.2,
1.12,
1.12.1,
1.12.2,
1.13,
1.13.1,
1.13.2,
1.14,
1.14.1,
1.14.2,
1.14.3,
1.14.4,
1.15,
1.15.1

**For example:**
```yaml
condition: 'version > 1.14'
```
```yaml
condition: 'version == 1.15.1'
```

# Example
This is an example configuration of a MOTD I used on my server before:

```yaml
enabled: true

# For more info about conditions, please go to https://docs.dbsoftwares.eu/bungeeutilisals/motd-manager#conditions
motd:
# Default, this one will show when all others do not apply.
  - condition: 'default'
    motd: |-
      &b&l>  &6&ki&r    &8-(&b&l+&8)-     &6&ki&c&lCentrix-Network&6&ki&r     &8-(&b&l+&8)-    &6&ki&r  &b&l<&r
               &b&lKITPVP RELEASE &a&l- &e&l1.15 SUPPORT
    player-hover:
      - '&8&m---------------------------------'
      - '&b&lGLOBAL &7» &f{proxy_online} players'
      - ''
      - '&d&lWEBSITE &7» &fhttps://centrixpvp.eu'
      - '&e&lSTORE &7» &fhttps://store.centrixpvp.eu'
      - '&a&lVOTE &7» &fhttps://centrixpvp.eu/vote'
      - '&8&m---------------------------------'
  - condition: 'version < 1.14'
    motd: |-
      &bIt seems like you are using &e{version}&b!
      &ePlease update to &a1.14+ &eas this is the recommended version.
    player-hover:
      - '&8&m---------------------------------'
      - '&b&lGLOBAL &7» &f{proxy_online} players'
      - ''
      - '&d&lWEBSITE &7» &fhttps://centrixpvp.eu'
      - '&e&lSTORE &7» &fhttps://store.centrixpvp.eu'
      - '&a&lVOTE &7» &fhttps://centrixpvp.eu/vote'
      - '&8&m---------------------------------'
```