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

package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessageBuilder
{

    public static TextComponent buildMessage( final User user,
                                              final ISection section,
                                              final Object... placeholders )
    {
        return buildMessage( user, section, null, null, placeholders );
    }

    public static TextComponent buildMessage( final User user,
                                              final ISection section,
                                              final Function<String, String> prePlaceholderFormatter,
                                              final Function<String, String> postPlaceholderFormatter,
                                              final Object... placeholders )
    {
        if ( section.isList( "text" ) )
        {
            final TextComponent component = new TextComponent();
            final List<ISection> sections = section.getSectionList( "text" );

            for ( ISection text : sections )
            {
                component.addExtra(
                        buildMessage( user, text, prePlaceholderFormatter, postPlaceholderFormatter, placeholders )
                );
            }
            return component;
        }
        final BaseComponent[] text = searchAndFormat(
                user,
                user.getLanguageConfig().getConfig(),
                section.getString( "text" ),
                prePlaceholderFormatter,
                postPlaceholderFormatter,
                placeholders
        );
        final TextComponent component = new TextComponent( text );

        if ( section.exists( "hover" ) )
        {
            final BaseComponent[] components = searchHoverMessageAndFormat(
                    user,
                    section,
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );

            if ( components != null )
            {
                component.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, components ) );
            }
        }
        if ( section.exists( "click" ) )
        {
            component.setClickEvent( new ClickEvent(
                    ClickEvent.Action.valueOf( section.getString( "click.type" ) ),
                    PlaceHolderAPI.formatMessage( user, Utils.c( format(
                            section.getString( "click.action" ),
                            prePlaceholderFormatter,
                            postPlaceholderFormatter,
                            placeholders
                    ) ) )
            ) );
        }

        return component;
    }

    public static List<TextComponent> buildMessage( final User user, final List<ISection> sections, final Object... placeholders )
    {
        final List<TextComponent> components = Lists.newArrayList();

        sections.forEach( section -> components.add( buildMessage( user, section, placeholders ) ) );
        return components;
    }

    private static String searchAndFormat( final IConfiguration config,
                                           final String str,
                                           final Function<String, String> prePlaceholderFormatter,
                                           final Function<String, String> postPlaceholderFormatter,
                                           final Object... placeholders )
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

    private static BaseComponent[] searchAndFormat( final User user,
                                                    final IConfiguration config,
                                                    final String str,
                                                    final Function<String, String> prePlaceholderFormatter,
                                                    final Function<String, String> postPlaceholderFormatter,
                                                    final Object... placeholders )
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
                                        final Object... placeholders )
    {
        return list.stream()
                .map( str -> format( str, prePlaceholderFormatter, postPlaceholderFormatter, placeholders ) )
                .collect( Collectors.toList() );
    }

    private static String format( String str,
                                  final Function<String, String> prePlaceholderFormatter,
                                  final Function<String, String> postPlaceholderFormatter,
                                  final Object... placeholders )
    {
        if ( prePlaceholderFormatter != null )
        {
            str = prePlaceholderFormatter.apply( str );
        }
        str = formatLine( str );

        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            str = str.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }

        if ( postPlaceholderFormatter != null )
        {
            str = postPlaceholderFormatter.apply( str );
        }
        return str;
    }

    private static BaseComponent[] searchHoverMessageAndFormat( final User user,
                                                                final ISection section,
                                                                final Function<String, String> prePlaceholderFormatter,
                                                                final Function<String, String> postPlaceholderFormatter,
                                                                final Object... placeholders )
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