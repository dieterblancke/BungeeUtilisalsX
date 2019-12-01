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

package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.stream.Collectors;

public class MessageBuilder
{

    public static TextComponent buildMessage( User user, ISection section, Object... placeholders )
    {
        if ( section.isList( "text" ) )
        {
            final TextComponent component = new TextComponent();

            section.getSectionList( "text" ).forEach( text -> component.addExtra( buildMessage( user, text, placeholders ) ) );
            return component;
        }
        final BaseComponent[] text = searchAndFormat( user, user.getLanguageConfig(), section.getString( "text" ), placeholders );
        final TextComponent component = new TextComponent( text );

        if ( section.exists( "hover" ) )
        {
            final BaseComponent[] components = searchHoverMessageAndFormat( user, section, placeholders );

            if ( components != null )
            {
                component.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, components ) );
            }
        }
        if ( section.exists( "click" ) )
        {
            component.setClickEvent( new ClickEvent(
                    ClickEvent.Action.valueOf( section.getString( "click.type" ) ),
                    PlaceHolderAPI.formatMessage( user, Utils.c( format( section.getString( "click.action" ), placeholders ) ) )
            ) );
        }

        return component;
    }

    public static List<TextComponent> buildMessage( User user, List<ISection> sections, Object... placeholders )
    {
        List<TextComponent> components = Lists.newArrayList();

        sections.forEach( section -> components.add( buildMessage( user, section, placeholders ) ) );
        return components;
    }

    private static String searchAndFormat( IConfiguration config, String str, Object... placeholders )
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
        return format( text, placeholders );
    }

    private static BaseComponent[] searchAndFormat( final User user, IConfiguration config, String str, Object... placeholders )
    {
        if ( config.exists( str ) )
        {
            if ( config.isString( str ) )
            {
                return Utils.format(
                        user,
                        format( config.getString( str ), placeholders )
                );
            }
            else if ( config.isList( str ) )
            {
                final List<String> list = format( config.getStringList( str ), placeholders );

                return Utils.format( user, list );
            }
        }

        return Utils.format(
                user,
                format( str, placeholders )
        );
    }

    private static String formatLine( final String line )
    {
        final String newLine = System.lineSeparator();

        return line.replace( "%nl%", newLine )
                .replace( "%newline%", newLine )
                .replace( "{nl}", newLine )
                .replace( "{newline}", newLine )
                .replace( "\r\n", newLine )
                .replace( "\n", newLine );
    }

    private static List<String> format( final List<String> list, Object... placeholders )
    {
        return list.stream().map( str -> format( str, placeholders ) ).collect( Collectors.toList() );
    }

    private static String format( String str, final Object... placeholders )
    {
        str = formatLine( str );

        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            str = str.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        return str;
    }

    private static BaseComponent[] searchHoverMessageAndFormat( final User user, final ISection section, final Object... placeholders )
    {
        if ( section.isList( "hover" ) )
        {
            return Utils.format(
                    user,
                    format( section.getStringList( "hover" ), placeholders )
            );
        }
        else
        {
            if ( section.getString( "hover" ).isEmpty() )
            {
                return null;
            }
            return searchAndFormat( user, user.getLanguageConfig(), section.getString( "hover" ), placeholders );
        }
    }
}