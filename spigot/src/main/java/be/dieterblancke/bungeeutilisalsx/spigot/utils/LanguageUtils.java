package be.dieterblancke.bungeeutilisalsx.spigot.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MessageBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class LanguageUtils
{

    public static void sendLangMessage( final Player player, final String path )
    {
        sendLangMessage( (CommandSender) player, path );
    }

    public static void sendLangMessage( final Player player, final String path, final Object... placeholders )
    {
        sendLangMessage( (CommandSender) player, path, placeholders );
    }

    public static void sendLangMessage( final CommandSender sender, final String path )
    {
        sendLangMessage( BuX.getApi().getLanguageManager(), BuX.getInstance().getName(), sender, path );
    }

    public static void sendLangMessage( final CommandSender sender, final String path, final Object... placeholders )
    {
        sendLangMessage( BuX.getApi().getLanguageManager(), BuX.getInstance().getName(), sender, path, placeholders );
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender.getName() ).getConfig();

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                sender.spigot().sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            sender.spigot().sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final CommandSender sender, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, sender.getName() ).getConfig();

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = Utils.replacePlaceHolders( null, message, placeholders );

                sender.spigot().sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = Utils.replacePlaceHolders( null, config.getString( path ), placeholders );

            sender.spigot().sendMessage( Utils.format( message ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user ).getConfig();

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                user.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            user.sendMessage( Utils.format( config.getString( path ) ) );
        }
    }

    public static void sendLangMessage( final ILanguageManager languageManager, final String plugin, final User user, final String path, final Object... placeholders )
    {
        final IConfiguration config = languageManager.getLanguageConfiguration( plugin, user ).getConfig();

        if ( !config.exists( path ) )
        {
            return;
        }

        if ( config.isSection( path ) )
        {
            // section detected, assuming this is a message to be handled by MessageBuilder (hover / focus events)
            final TextComponent component = MessageBuilder.buildMessage( user, config.getSection( path ), placeholders );

            user.sendMessage( component );
        }
        else if ( config.isList( path ) )
        {
            for ( String message : config.getStringList( path ) )
            {
                message = Utils.replacePlaceHolders( user, message, placeholders );
                user.sendMessage( Utils.format( message ) );
            }
        }
        else
        {
            if ( config.getString( path ).isEmpty() )
            {
                return;
            }
            String message = Utils.replacePlaceHolders( user, config.getString( path ), placeholders );
            user.sendMessage( Utils.format( message ) );
        }
    }

    public static String getLanguageString( final String path, final Player player )
    {
        final IConfiguration config = BuX.getApi().getLanguageManager().getLanguageConfiguration(
                BuX.getInstance().getName(),
                player.getName()
        ).getConfig();

        if ( config == null )
        {
            return path;
        }

        return config.exists( path ) ? config.getString( path ) : path;
    }

    public static List<String> getLanguageStringList( final String path, final Player player )
    {
        final IConfiguration config = BuX.getApi().getLanguageManager().getLanguageConfiguration(
                BuX.getInstance().getName(),
                player.getName()
        ).getConfig();

        if ( config == null )
        {
            return Collections.singletonList( path );
        }

        return config.exists( path ) ? config.getStringList( path ) : Collections.singletonList( path );
    }
}