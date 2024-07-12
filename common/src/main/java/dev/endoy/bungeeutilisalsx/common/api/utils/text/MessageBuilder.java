package dev.endoy.bungeeutilisalsx.common.api.utils.text;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.protocolize.ProtocolizeManager.SoundData;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.api.ISection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageBuilder
{

    public static Component buildMessage( final User user,
                                          final ISection section )
    {
        return buildMessage( user, section, MessagePlaceholders.empty() );
    }

    public static Component buildMessage( final User user,
                                          final ISection section,
                                          final HasMessagePlaceholders placeholders )
    {
        return buildMessage( user, section, null, null, placeholders );
    }

    public static Component buildMessage( final User user,
                                          final ISection section,
                                          final Function<String, String> prePlaceholderFormatter,
                                          final Function<String, String> postPlaceholderFormatter )
    {
        return buildMessage( user, section, prePlaceholderFormatter, postPlaceholderFormatter, MessagePlaceholders.empty() );
    }

    public static Component buildMessage( final User user,
                                          final ISection section,
                                          final Function<String, String> prePlaceholderFormatter,
                                          final Function<String, String> postPlaceholderFormatter,
                                          final HasMessagePlaceholders placeholders )
    {
        if ( section.isList( "text" ) )
        {
            Component component = Component.empty();
            List<ISection> sections = section.getSectionList( "text" );

            for ( ISection text : sections )
            {
                component = component.append(
                        buildMessage( user, text, prePlaceholderFormatter, postPlaceholderFormatter, placeholders )
                );
            }
            return component;
        }
        Component text = searchAndFormat(
                user,
                user.getLanguageConfig().getConfig(),
                section.getString( "text" ),
                prePlaceholderFormatter,
                postPlaceholderFormatter,
                placeholders
        );

        if ( section.exists( "hover" ) )
        {
            Component component = searchHoverMessageAndFormat(
                    user,
                    section,
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );

            if ( component != null )
            {
                text = text.hoverEvent( HoverEvent.showText( component ) );
            }
        }
        if ( section.exists( "click" ) )
        {
            text = text.clickEvent( ClickEvent.clickEvent(
                    ClickEvent.Action.valueOf( section.getString( "click.type" ) ),
                    PlaceHolderAPI.formatMessage( user, format(
                            section.getString( "click.action" ),
                            prePlaceholderFormatter,
                            postPlaceholderFormatter,
                            placeholders
                    ) )
            ) );
        }
        if ( section.exists( "insertion" ) )
        {
            text = text.insertion( section.getString( "insertion" ) );
        }
        if ( section.exists( "sound" ) && BuX.getInstance().isProtocolizeEnabled() )
        {
            BuX.getInstance().getProtocolizeManager().sendSound( user, SoundData.fromSection( section, "sound" ) );
        }

        return text;
    }

    public static Component buildMessage( final User user, final List<ISection> sections )
    {
        return buildMessage( user, sections, MessagePlaceholders.empty() );
    }

    public static Component buildMessage( final User user, final List<ISection> sections, final HasMessagePlaceholders placeholders )
    {
        Component component = Component.empty();

        for ( ISection section : sections )
        {
            component = component.append( buildMessage( user, section, placeholders ) );
        }

        return component;
    }

    private static String searchAndFormat( final IConfiguration config,
                                           final String str,
                                           final Function<String, String> prePlaceholderFormatter,
                                           final Function<String, String> postPlaceholderFormatter,
                                           final HasMessagePlaceholders placeholders )
    {
        String text = str;

        if ( config.exists( str ) )
        {
            if ( config.isString( str ) )
            {
                text = config.getString( str );
            }
            else if ( config.isList( str ) )
            {
                text = Utils.formatList( config.getStringList( str ), System.lineSeparator() );
            }
        }
        return format( text, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );
    }

    private static Component searchAndFormat( final User user,
                                              final IConfiguration config,
                                              final String str,
                                              final Function<String, String> prePlaceholderFormatter,
                                              final Function<String, String> postPlaceholderFormatter,
                                              final HasMessagePlaceholders placeholders )
    {
        if ( config.exists( str ) )
        {
            if ( config.isString( str ) )
            {
                return Utils.format(
                        user,
                        format( config.getString( str ), prePlaceholderFormatter, postPlaceholderFormatter, placeholders )
                );
            }
            else if ( config.isList( str ) )
            {
                final List<String> list = format( config.getStringList( str ), prePlaceholderFormatter, postPlaceholderFormatter, placeholders );

                return Utils.format( user, list );
            }
        }

        return Utils.format(
                user,
                format( str, prePlaceholderFormatter, postPlaceholderFormatter, placeholders )
        );
    }

    private static String formatLine( final String line )
    {
        final String newLine = "\n";

        return line.replace( "%nl%", newLine )
                .replace( "%newline%", newLine )
                .replace( "{nl}", newLine )
                .replace( "{newline}", newLine )
                .replace( "\r\n", newLine )
                .replace( "\n", newLine );
    }

    private static List<String> format( final List<String> list,
                                        final Function<String, String> prePlaceholderFormatter,
                                        final Function<String, String> postPlaceholderFormatter,
                                        final HasMessagePlaceholders placeholders )
    {
        return list.stream()
                .map( str -> format( str, prePlaceholderFormatter, postPlaceholderFormatter, placeholders ) )
                .collect( Collectors.toList() );
    }

    private static String format( String str,
                                  final Function<String, String> prePlaceholderFormatter,
                                  final Function<String, String> postPlaceholderFormatter,
                                  final HasMessagePlaceholders placeholders )
    {
        if ( prePlaceholderFormatter != null )
        {
            str = prePlaceholderFormatter.apply( str );
        }

        str = formatLine( str );
        str = placeholders.getMessagePlaceholders().format( str );

        if ( postPlaceholderFormatter != null )
        {
            str = postPlaceholderFormatter.apply( str );
        }
        return str;
    }

    private static Component searchHoverMessageAndFormat( final User user,
                                                          final ISection section,
                                                          final Function<String, String> prePlaceholderFormatter,
                                                          final Function<String, String> postPlaceholderFormatter,
                                                          final HasMessagePlaceholders placeholders )
    {
        if ( section.isList( "hover" ) )
        {
            return Utils.format(
                    user,
                    format(
                            section.getStringList( "hover" ),
                            prePlaceholderFormatter,
                            postPlaceholderFormatter,
                            placeholders
                    )
            );
        }
        else
        {
            if ( section.getString( "hover" ).isEmpty() )
            {
                return null;
            }
            return searchAndFormat(
                    user,
                    user.getLanguageConfig().getConfig(),
                    section.getString( "hover" ),
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );
        }
    }
}