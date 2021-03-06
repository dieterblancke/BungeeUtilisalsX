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

package com.dbsoftwares.bungeeutilisals.placeholders;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.data.StaffRankData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;

import java.util.Comparator;

public class UserPlaceHolderPack implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{user}", true, this::getUserName );
        PlaceHolderAPI.addPlaceHolder( "{user_prefix}", true, this::getUserPrefix );
        PlaceHolderAPI.addPlaceHolder( "{ping}", true, this::getUserPing );
        PlaceHolderAPI.addPlaceHolder( "{server}", true, this::getServerName );
        PlaceHolderAPI.addPlaceHolder( "{server_online}", true, this::getServerCount );
        PlaceHolderAPI.addPlaceHolder( "{language_short}", true, this::getShortLanguage );
        PlaceHolderAPI.addPlaceHolder( "{language_long}", true, this::getLongLanguage );
    }

    private String getUserName( final PlaceHolderEvent event )
    {
        return event.getUser().getName();
    }

    private String getUserPing( final PlaceHolderEvent event )
    {
        return String.valueOf( event.getUser().getPing() );
    }

    private String getUserPrefix( final PlaceHolderEvent event )
    {
        return ConfigFiles.RANKS.getRanks().stream()
                .filter( rank -> event.getUser().hasPermission( rank.getPermission() ) )
                .max( Comparator.comparingInt( StaffRankData::getPriority ) )
                .map( rank -> Utils.c( rank.getDisplay() ) )
                .orElse( "" );
    }

    private String getServerName( final PlaceHolderEvent event )
    {
        return event.getUser().getServerName();
    }

    private String getServerCount( final PlaceHolderEvent event )
    {
        return String.valueOf( BUCore.getApi().getPlayerUtils().getPlayerCount( event.getUser().getServerName() ) );
    }

    private String getShortLanguage( final PlaceHolderEvent event )
    {
        final User user = event.getUser();

        if ( user instanceof ConsoleUser || user.getParent() == null || user.getParent().getLocale() == null )
        {
            return "en";
        }
        else
        {
            return user.getParent().getLocale().getLanguage();
        }
    }

    private String getLongLanguage( final PlaceHolderEvent event )
    {
        final User user = event.getUser();

        if ( user instanceof ConsoleUser || user.getParent() == null || user.getParent().getLocale() == null )
        {
            return "en_US";
        }
        else
        {
            return user.getParent().getLocale().toString();
        }
    }
}
