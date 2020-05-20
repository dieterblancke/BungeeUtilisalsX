/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals;

import com.dbsoftwares.bungeeutilisals.announcers.ActionBarAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.BossBarAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.ChatAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.TitleAnnouncer;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffJoinEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffLeaveEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.*;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.JarClassLoader;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.executors.*;
import com.dbsoftwares.bungeeutilisals.library.Library;
import com.dbsoftwares.bungeeutilisals.library.StandardLibrary;
import com.dbsoftwares.bungeeutilisals.listeners.MotdPingListener;
import com.dbsoftwares.bungeeutilisals.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.manager.CommandManager;
import com.dbsoftwares.bungeeutilisals.placeholders.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.InputPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.UserPlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.JavaScriptPlaceHolder;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.Script;
import com.dbsoftwares.bungeeutilisals.runnables.UserMessageQueueRunnable;
import com.dbsoftwares.bungeeutilisals.updater.Updatable;
import com.dbsoftwares.bungeeutilisals.updater.Update;
import com.dbsoftwares.bungeeutilisals.updater.Updater;
import com.dbsoftwares.bungeeutilisals.utils.EncryptionUtils;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import javax.script.ScriptException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Updatable(url = "https://api.dbsoftwares.eu/plugin/BungeeUtilisals/")
public class BungeeUtilisals extends Plugin
{

    @Getter
    private static BungeeUtilisals instance;

    @Getter
    private static BUtilisalsAPI api;

    @Getter
    private JarClassLoader jarClassLoader;

    @Getter
    private AbstractStorageManager databaseManagement;

    @Getter
    private CommandManager commandManager;

    @Getter
    private List<Script> scripts = Lists.newArrayList();

    @Getter
    private List<StaffUser> staffMembers = Lists.newArrayList();

    @Override
    public void onEnable()
    {
        if ( ReflectionUtils.getJavaVersion() < 8 )
        {
            BUCore.getLogger().warning( "You are running a Java version lower then Java 8." );
            BUCore.getLogger().warning( "Please upgrade to Java 8 or newer." );
            BUCore.getLogger().warning( "BungeeUtilisalsX is not able to start up on Java versions lower then Java 8." );
            return;
        }
        // Setting instance
        instance = this;

        // Creating datafolder if not exists.
        if ( !getDataFolder().exists() )
        {
            getDataFolder().mkdirs();
        }

        // Loading setting files ...
        ConfigFiles.loadAllConfigs( this );

        // Loading default PlaceHolders. Must be done BEFORE API / database loads.
        PlaceHolderAPI.loadPlaceHolderPack( new DefaultPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new InputPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new UserPlaceHolderPack() );

        new JavaScriptPlaceHolder().register();
        loadScripts();

        // Loading libraries
        loadLibraries();

        // Loading database
        loadDatabase();

        // Initializing API
        api = new BUtilisalsAPI( this );

        // Loading language chat
        api.getLanguageManager().addPlugin( getDescription().getName(), new File( getDataFolder(), "languages" ), FileStorageType.YAML );
        api.getLanguageManager().loadLanguages( getDescription().getName() );

        // Updating data from previous versions ...
        checkPreviousVersion();

        // Initialize metric system
        registerMetrics();

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener( this, new UserConnectionListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( this, new UserChatListener() );

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( this, new MotdPingListener() );
        }

        final IEventLoader loader = api.getEventLoader();

        final UserExecutor userExecutor = new UserExecutor();
        loader.register( UserLoadEvent.class, userExecutor );
        loader.register( UserUnloadEvent.class, userExecutor );
        loader.register( UserChatEvent.class, new UserChatExecutor() );
        loader.register( UserChatEvent.class, new StaffCharChatExecutor() );

        final StaffNetworkExecutor staffNetworkExecutor = new StaffNetworkExecutor();
        loader.register( NetworkStaffJoinEvent.class, staffNetworkExecutor );
        loader.register( NetworkStaffLeaveEvent.class, staffNetworkExecutor );

        final SpyEventExecutor spyEventExecutor = new SpyEventExecutor();
        loader.register( UserPrivateMessageEvent.class, spyEventExecutor );
        loader.register( UserCommandEvent.class, spyEventExecutor );

        // Loading Punishment system
        if ( ConfigFiles.PUNISHMENTS.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( this, new PunishmentListener() );

            loader.register( UserPunishmentFinishEvent.class, new UserPunishExecutor() );

            MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            loader.register( UserChatEvent.class, muteCheckExecutor );
            loader.register( UserCommandEvent.class, muteCheckExecutor );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            final FriendsExecutor executor = new FriendsExecutor();
            loader.register( UserLoadEvent.class, executor );
            loader.register( UserUnloadEvent.class, executor );
            ProxyServer.getInstance().getPluginManager().registerListener( this, executor );
        }

        // Loading Announcers
        Announcer.registerAnnouncers( ActionBarAnnouncer.class, ChatAnnouncer.class, TitleAnnouncer.class, BossBarAnnouncer.class );

        // Loading all (enabled) Commands
        commandManager = new CommandManager();
        commandManager.load();

        ProxyServer.getInstance().getScheduler().schedule( this, new UserMessageQueueRunnable(), 1, TimeUnit.MINUTES );

        if ( getConfig().getBoolean( "updater.enabled" ) )
        {
            Updater.initialize( this );
        }

        // Some more random stuff
        sendBuyerMessage();
    }

    private void checkPreviousVersion()
    {
        final String key = ";l-,-s`YZetApB!$}r|*<[84z9nLG06PoJtN,g877*D9ImW~|d9|Ax^lC+JTOsL";
        final File file = new File( getDataFolder(), "libraries/.update_util.data" );

        if ( !file.getParentFile().exists() )
        {
            file.getParentFile().mkdir();
        }
        boolean shouldUpdate = false;
        if ( !file.exists() )
        {
            try
            {
                file.createNewFile();

                final String encrypted = EncryptionUtils.encrypt( getDescription().getVersion(), key );
                Files.write( file.toPath(), encrypted.getBytes(), StandardOpenOption.TRUNCATE_EXISTING );

                shouldUpdate = getDescription().getVersion().equalsIgnoreCase( "1.0.5.0" );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        if ( !shouldUpdate )
        {
            try
            {
                final String version = EncryptionUtils.decrypt( new String( Files.readAllBytes( file.toPath() ) ), key );

                shouldUpdate = !version.equals( getDescription().getVersion() );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        if ( shouldUpdate )
        {
            // SEARCH FOR UPDATE CLASS, IF FOUND, EXECUTE IT
            try
            {
                final Class<? extends Update> updater = (Class<? extends Update>) Class.forName(
                        "com.dbsoftwares.bungeeutilisals.updater.UpdateTo"
                                + getDescription().getVersion().replace( ".", "_" )
                );

                BUCore.getLogger().info( "Updating data to support BungeeUtilisalsX v" + getDescription().getVersion() + " ..." );
                updater.newInstance().update();
                BUCore.getLogger().info( "Finished updating data!" );

                final String encrypted = EncryptionUtils.encrypt( getDescription().getVersion(), key );
                Files.write( file.toPath(), encrypted.getBytes(), StandardOpenOption.TRUNCATE_EXISTING );
            }
            catch ( ClassNotFoundException | IllegalAccessException | InstantiationException | IOException ignored )
            {
            }
        }
    }

    @Override
    public void onDisable()
    {
        Lists.newArrayList( BUCore.getApi().getUsers() ).forEach( User::unload );
        try
        {
            databaseManagement.close();
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        scripts.forEach( Script::unload );
        api.getEventLoader().getHandlers().forEach( EventHandler::unregister );
        Updater.shutdownUpdaters();
    }

    public void sendBuyerMessage()
    {
        final String userid = "%%USER_ID%%";
        String username = "Unknown User";

        // retrieving username ...
        if ( !userid.equals( "%%__USER__%" ) )
        {
            try
            {
                URL url = new URL( "https://www.spigotmc.org/members/" + userid );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0" );

                try ( InputStream inputStream = connection.getInputStream();
                      InputStreamReader isr = new InputStreamReader( inputStream );
                      BufferedReader br = new BufferedReader( isr ) )
                {

                    String line;
                    final StringBuilder builder = new StringBuilder();
                    while ( ( line = br.readLine() ) != null )
                    {
                        builder.append( line );
                    }
                    username = builder.toString().split( "<title>" )[1].split( "</title>" )[0].split( " | " )[0];
                }
            }
            catch ( IOException e )
            {
                // do nothing
            }
        }

        if ( !username.equals( "Unknown User" ) )
        {
            api.getConsole().sendLangMessage( false, "general.startup-message", "{buyer_name}", username );
        }
    }

    public void reload()
    {
        ConfigFiles.reloadAllConfigs();

        if ( BUCore.getApi().getHubBalancer() != null )
        {
            BUCore.getApi().getHubBalancer().reload();
        }

        commandManager.load();

        Announcer.getAnnouncers().values().forEach( Announcer::reload );

        for ( Language language : BUCore.getApi().getLanguageManager().getLanguages() )
        {
            BUCore.getApi().getLanguageManager().reloadConfig( getDescription().getName(), language );
        }

        loadScripts();
        api.getChatManager().reload();
    }

    private void loadScripts()
    {
        scripts.forEach( Script::unload );
        scripts.clear();
        final File scriptsFolder = new File( getDataFolder(), "scripts" );

        if ( !scriptsFolder.exists() )
        {
            scriptsFolder.mkdir();

            IConfiguration.createDefaultFile(
                    getResourceAsStream( "scripts/hello.js" ), new File( scriptsFolder, "hello.js" )
            );
            IConfiguration.createDefaultFile(
                    getResourceAsStream( "scripts/coins.js" ), new File( scriptsFolder, "coins.js" )
            );
        }

        for ( final File file : scriptsFolder.listFiles() )
        {
            if ( file.isDirectory() )
            {
                continue;
            }
            try
            {
                final String code = new String( Files.readAllBytes( file.toPath() ) );
                final Script script = new Script( file.getName(), code );

                this.scripts.add( script );
            }
            catch ( IOException | ScriptException e )
            {
                BUCore.getLogger().log( Level.SEVERE, "Could not load script " + file.getName(), e );
            }
        }
    }

    private void loadDatabase()
    {
        StorageType type;
        try
        {
            type = StorageType.valueOf( getConfig().getString( "storage.type" ).toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            type = StorageType.MYSQL;
        }
        try
        {
            databaseManagement = type.getManager().getConstructor( Plugin.class ).newInstance( this );
            databaseManagement.initialize();
        }
        catch ( Exception e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    private void loadLibraries()
    {
        BUCore.getLogger().info( "Loading libraries ..." );
        jarClassLoader = new JarClassLoader( this );

        for ( StandardLibrary standardLibrary : StandardLibrary.values() )
        {
            final Library library = standardLibrary.getLibrary();

            if ( library.isToLoad() && !library.isPresent() )
            {
                library.load();
            }
        }
        BUCore.getLogger().info( "Libraries have been loaded." );
    }

    public IConfiguration getConfig()
    {
        return ConfigFiles.CONFIG.getConfig();
    }

    private void registerMetrics()
    {
        final Metrics metrics = new Metrics( this );

        metrics.addCustomChart( new Metrics.SimplePie(
                "punishments",
                () -> ConfigFiles.PUNISHMENTS.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "friends",
                () -> ConfigFiles.FRIENDS_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "actionbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.ACTIONBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "title_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TITLE ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "bossbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.BOSSBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "chat_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.CHAT ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "hubbalancer",
                () -> BUCore.getApi().getHubBalancer() != null ? "enabled" : "disabled"
        ) );
    }
}