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

package be.dieterblancke.bungeeutilisalsx.velocity.placeholder;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import be.dieterblancke.bungeeutilisalsx.velocity.user.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import com.velocitypowered.api.proxy.Player;

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
        return String.valueOf( BuX.getApi().getPlayerUtils().getPlayerCount( event.getUser().getServerName() ) );
    }

    private String getShortLanguage( final PlaceHolderEvent event )
    {
        final User user = event.getUser();
        if ( user instanceof ConsoleUser || user.getUuid() == null )
        {
            return "en";
        }
        else
        {
            final Player player = Bootstrap.getInstance().getProxyServer().getPlayer( user.getUuid() ).orElse( null );

            if ( player == null || player.getPlayerSettings().getLocale() == null )
            {
                return "en";
            }
            else
            {
                return player.getPlayerSettings().getLocale().getLanguage();
            }
        }
    }

    private String getLongLanguage( final PlaceHolderEvent event )
    {
        final User user = event.getUser();

        if ( user instanceof ConsoleUser || user.getUuid() == null )
        {
            return "en_US";
        }
        else
        {
            final Player player = Bootstrap.getInstance().getProxyServer().getPlayer( user.getUuid() ).orElse( null );

            if ( player == null || player.getPlayerSettings().getLocale() == null )
            {
                return "en";
            }
            else
            {
                return player.getPlayerSettings().getLocale().toString();
            }
        }
    }
}
