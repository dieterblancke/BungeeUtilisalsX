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

package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

public class UserPlaceHolderPack implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{user}", true, this::getUserName );
        PlaceHolderAPI.addPlaceHolder( "{me}", true, this::getUserName );
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
        return StaffUtils.getStaffRankForUser( event.getUser() )
                .map( rank -> Utils.c( rank.getDisplay() ) )
                .orElse( "" );
    }

    private String getServerName( final PlaceHolderEvent event )
    {
        return event.getUser().getServerName();
    }

    private String getServerCount( final PlaceHolderEvent event )
    {
        if ( event.getUser().getServerName().trim().isEmpty() )
        {
            return "0";
        }
        return String.valueOf( BuX.getApi().getPlayerUtils().getPlayerCount( event.getUser().getServerName() ) );
    }

    private String getShortLanguage( final PlaceHolderEvent event )
    {
        return event.getUser().getLanguageTagShort();
    }

    private String getLongLanguage( final PlaceHolderEvent event )
    {
        return event.getUser().getLanguageTagLong();
    }
}
