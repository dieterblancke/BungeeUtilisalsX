# Developer API

# Bossbar API
* For all IBossBar methods, please [check it out here](https://git.dbsoftwares.eu/dbsoftwares/BungeeUtilisalsX/blob/master/api/src/main/java/com/dbsoftwares/bungeeutilisals/api/bossbar/IBossBar.java)!
* BossBar Actions, Colors & Styles can [also be found in the same package](https://git.dbsoftwares.eu/dbsoftwares/BungeeUtilisalsX/tree/master/api/src/main/java/com/dbsoftwares/bungeeutilisals/api/bossbar).
* The different methods to [create a bossbar can be found here](https://git.dbsoftwares.eu/dbsoftwares/BungeeUtilisalsX/blob/master/api/src/main/java/com/dbsoftwares/bungeeutilisals/api/BUAPI.java#L236-258)!

## Creating a BossBar
```java
BUAPI api = BUCore.getApi();

// Creating BossBar: api.createBossBar(BarColor color, BarStyle style, float progress, BaseComponent[] message)
IBossBar bossBar = api.createBossBar(BarColor.YELLOW, BarStyle.SOLID, 0.0F, Utils.format("&aExample Message ..."));

// Adding all online users | this basically broadcasts the BossBar
api.getUsers().forEach(bossBar::addUser);
```

It is recommended to unregister Bossbar objects when they are not being used, as for every bossbar made, there is an event registered. <br>
This event listens for users leaving the network, so they can be removed from the bossbar (in order to prevent memory leaks).

```java
bossbar.unregister();
```

# Configuration API
BungeeUtilisalsX has a configuration API, similar to the one of Bukkit, implemented for both YAML & JSON files. <br>
This ConfigurationAPI is a separate project, which also [has it's own repository](https://git.dbsoftwares.eu/dbsoftwares/ConfigurationAPI/blob/master/README.md), here you can find more info about how to use it.

> [!WARNING|style:flat]
> An exception will be thrown if the file does not exist! In order to avoid this, make sure that the file exists (empty or not) before loading it.

## Loading a JSON file
```java
// Creating new configuration instance
IConfiguration jsonExample = IConfiguration.loadJsonConfiguration(new File(getDataFolder(), "jsonExample.json"));

// Loading configuration defaults from plugin resources
try {
    jsonExample.copyDefaults(IConfiguration.loadJsonConfiguration(getResourceAsStream("jsonExample.json")));
} catch (IOException e) {
    System.out.println("Could not load configuration defaults: ");
    e.printStackTrace();
}

// Get a String from the config
String test = jsonExample.getString("test");

// Set a value in the config
jsonExample.set("test.test2", 16);

// Save the config
try {
    jsonExample.save();
} catch (IOException e) {
    System.out.println("Could not save configuration: ");
    e.printStackTrace();
}

// Reload config (for when something manually got changed in the configuration)
// Reloading is NOT REQUIRED AFTER SAVING
try {
    jsonExample.reload();
} catch (IOException e) {
    System.out.println("Could not reload configuration: ");
    e.printStackTrace();
}
```

## Loading a YAML file
```java
// Creating new configuration instance
IConfiguration yamlExample = IConfiguration.loadYamlConfiguration(new File(getDataFolder(), "yamlExample.yml"));

// Loading configuration defaults from plugin resources
try {
    yamlExample.copyDefaults(IConfiguration.loadYamlConfiguration(getResourceAsStream("yamlExample.yml")));
} catch (IOException e) {
    System.out.println("Could not load configuration defaults: ");
    e.printStackTrace();
}

// Get a String from the config
String test = yamlExample.getString("test");

// Set a value in the config
yamlExample.set("test.test2", 16);

// Save the config
try {
    yamlExample.save();
} catch (IOException e) {
    System.out.println("Could not save configuration: ");
    e.printStackTrace();
}

// Reload config (for when something manually got changed in the configuration)
// Reloading is NOT REQUIRED AFTER SAVING
try {
    yamlExample.reload();
} catch (IOException e) {
    System.out.println("Could not reload configuration: ");
    e.printStackTrace();
}
```

# Custom Language Integration
By default, BungeeUtilisalsX uses it's own system to handle user languages, however, some servers have their own Language System / API that they use. For this, they could use this API.

## Registering Language Fetcher
This is by far the easiest implementation, this just gets called when a user's language is being requested.
```java
ILanguageManager langManager = BUCore.getApi().getLanguageManager();

langManager.setLanguageIntegration(uuid -> {
    Language language = null;

    try (Connection connection = database.getConnection()) {
        PreparedStatement pstmt = connection.prepareStatement("SELECT lang FROM users WHERE uuid = ?;");
        pstmt.setString(1, uuid.toString());

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                language =  langManager.getLangOrDefault(rs.getString("lang"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return language == null ? langManager.getDefaultLanguage() : language;
});
```

## Creating a custom language manager
You could also create a custom language manager, which you could base on the default language manager ([PluginLanguageManager](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/java/com/dbsoftwares/bungeeutilisals/language/PluginLanguageManager.java) and [AbstractLanguageManager](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/bungee/src/main/java/com/dbsoftwares/bungeeutilisals/language/AbstractLanguageManager.java)).

You could also just make a custom LanguageManager which extends PluginLanguageManager and override the methods you want to see changed.
**Note:** This is the more "hacky" way of implementing your custom language manager by using Reflection to do so. Please do not use this if you don't understand what you are doing.
```java
// Creating new ILanguageManager instance
final ILanguageManager languageManager = new YourCustomLanguageManager();

// Getting languageManager field.
Field languageManagerField = ReflectionUtils.getField(BUCore.getApi().getClass(), "languageManager");
try {
    // Attempting to update languageManager field to new, custom, ILanguageManager instance.
    languageManagerField.set(BUCore.getApi().getClass(), languageManager);
} catch (IllegalAccessException e) {
    e.printStackTrace();
}
```

# PlaceHolders
A list of placeholders can be [found here](placeholders.md).
PlaceHolderAPI path is [com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI](https://github.com/dieterblancke/BungeeUtilisalsX/blob/master/api/src/main/java/com/dbsoftwares/bungeeutilisals/api/placeholder/PlaceHolderAPI.java) 

## Formatting a message
To replace a placeholder, there are two methods, one for general placeholders:
```java
final String message = PlaceHolderAPI.formatMessage("Online Players in our lobby: {getcount: Lobby1}");
```
and one for both general and user related placeholders:
```java
final String message = PlaceHolderAPI.formatMessage("Hello {user}, there are {getcount: Lobby1} players in our lobby!");
```

## Create your own placeholder
### Normal PlaceHolder
An normal placeholder simply gets replaced by what you return in your handler.
The parameters this method takes is the placeholder to be replaced, a boolean whether or not the placeholder involves an user, and the placeholder handler.

```java
PlaceHolderAPI.addPlaceHolder("{user-server}", true, new PlaceHolderEventHandler() {
    @Override
    public String getReplacement(PlaceHolderEvent event) {
        return event.getUser().getServerName();
    }
});
```

### Input PlaceHolder
An input placeholder requests an argument which you can use to return your result.
The parameters this method takes is a boolean whether or not the placeholder involves an user, the placeholder prefix, and the placeholder handler.

In this example, the placeholder would be {upper: your text that has to be in upper case}
```java
PlaceHolderAPI.addPlaceHolder(false, "upper", new InputPlaceHolderEventHandler() {
    @Override
    public String getReplacement(InputPlaceHolderEvent event) {
        return event.getArgument().toUpperCase();
    }
});
```

# Addons
## Addon Example
You can find an example addon that I made [by clicking here](https://git.dbsoftwares.eu/dbsoftwares/bungeeutilisalsx-addons/antiafkaddon).

## Basic concept
### Importing BungeeUtilisals & BungeeCord.

You could import BungeeUtilisals and BungeeCord (for maven) like this:
```xml
<repositories>
    <repository>
        <id>dbsoftwares-repo</id>
        <url>http://nexus.diviwork.nl/repository/dbsoftwares/</url>
    </repository>
    <repository>
        <id>bungeecord-repo</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.md-5</groupId>
        <artifactId>bungeecord-api</artifactId>
        <version>1.14-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.dbsoftwares.bungeeutilisals</groupId>
        <artifactId>bungee</artifactId>
        <version>1.0.4.9</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Creating addon.yml
Once you've finished importing, you'll need to make an addon.yml file (located in /src/main/resources), so that BungeeUtilisals can get to know your addon (name, version, main class, description, ...)

An addon.yml file needs to look like the following:
```yml
name: YourAddonName
version: 1.0.0
main: 'package.to.your.MainClass'
author: AuthorOfThePlugin
description: 'Description of your plugin'
api-version: '1.0.4.0'
source: 'Link to the source of your project, leave empty if no link.'

dependencies:
  required:
    - 'RequiredDependencyName'
  optional:
    - 'OptionalDependencyName'
```
API version is the minimum version of BungeeUtilisalsX that your addon needs to be able to run.

An example of this can [be found here](https://git.dbsoftwares.eu/dbsoftwares/bungeeutilisalsx-addons/antiafkaddon/blob/master/src/main/resources/addon.yml).

### Creating your main class
I'll be using the AntiAFKAddon as an example here.

First you make your package structure and main class, which in my AntiAFKAddon looks like this (just focus on the parent package and main class (which is AntiAFKAddon)): <br />
![image](uploads/d64436301a5af42fd73379224b1bb2c0/image.png)

Once you made your main class, make it extend the "Addon" class and implement the abstract methods, the class will then be something like this:
```java
package com.dbsoftwares.bungeeutilisals.antiafk;

import com.dbsoftwares.bungeeutilisals.api.addon.Addon;

public class AntiAFKAddon extends Addon {
    
    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }
}
```

Here you can write your addon logic and use the BungeeUtilisalsX API.
### Important notes:
When registering Commands, Listeners or EventExecutors, please use the following methods to wrap them:

```java
registerEventHandlers(EventHandler.class);
registerListeners(Listener.class);
registerCommands(Command.class);
```

**For example:**
```java
registerEventHandlers(getApi().getEventLoader().register(UserLoadEvent.class, new UserExecutor()));
registerListeners(new PlayerListener());
registerCommand(new AfkCommand());
```

This is important because when an addon gets unloaded, BungeeUtilisals needs to find the commands, eventhandlers & listeners created by the Addon, and disable them.

**For Schedulers**: You'll have to store schedulers yourself, and cancel them when the addon is getting disabled in the onDisable method.