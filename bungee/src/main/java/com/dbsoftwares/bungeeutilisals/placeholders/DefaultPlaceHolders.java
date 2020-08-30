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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.ProxyServer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultPlaceHolders implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{users-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.users" ) );
        PlaceHolderAPI.addPlaceHolder( "{ignoredusers-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.ignoredusers" ) );
        PlaceHolderAPI.addPlaceHolder( "{friends-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.friends" ) );
        PlaceHolderAPI.addPlaceHolder( "{friendrequests-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.friendrequests" ) );
        PlaceHolderAPI.addPlaceHolder( "{friendsettings-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.friendsettings" ) );
        PlaceHolderAPI.addPlaceHolder( "{bans-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.bans" ) );
        PlaceHolderAPI.addPlaceHolder( "{mutes-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.mutes" ) );
        PlaceHolderAPI.addPlaceHolder( "{kicks-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.kicks" ) );
        PlaceHolderAPI.addPlaceHolder( "{warns-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.warns" ) );
        PlaceHolderAPI.addPlaceHolder( "{punishmentactions-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.punishmentactions" ) );
        PlaceHolderAPI.addPlaceHolder( "{reports-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.reports" ) );
        PlaceHolderAPI.addPlaceHolder( "{messagequeue-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString( "storage.schemas.messagequeue" ) );

        // Proxy PlaceHolders
        PlaceHolderAPI.addPlaceHolder( "{proxy_online}", false,
                event -> String.valueOf( BUCore.getApi().getPlayerUtils().getTotalCount() ) );
        PlaceHolderAPI.addPlaceHolder( "{proxy_max}", false,
                event -> String.valueOf( ProxyServer.getInstance().getConfig().getListeners().iterator().next().getMaxPlayers() ) );

        PlaceHolderAPI.addPlaceHolder( "{redis_online}", false,
                event -> String.valueOf( BUCore.getApi().getBridgeManager().useBridging() ? BUCore.getApi().getPlayerUtils().getTotalCount() : 0 ) );

        PlaceHolderAPI.addPlaceHolder( "{date}", false, this::getCurrentDate );
        PlaceHolderAPI.addPlaceHolder( "{time}", false, this::getCurrentTime );
        PlaceHolderAPI.addPlaceHolder( "{datetime}", false, this::getCurrentDateTime );
    }

    private String getCurrentDate( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "date" );
    }

    private String getCurrentTime( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "time" );
    }

    private String getCurrentDateTime( final PlaceHolderEvent event )
    {
        return this.getCurrentTime( event.getUser(), "datetime" );
    }

    private String getCurrentTime( final User user, final String type )
    {
        final IConfiguration configuration = getLanguageConfiguration( user );

        if ( configuration == null )
        {
            return "";
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat( configuration.getString( "placeholders.format." + type ) );

        return dateFormat.format( new Date() );
    }

    private IConfiguration getLanguageConfiguration( User user )
    {
        if ( user == null )
        {
            return BUCore.getApi().getLanguageManager().getConfig(
                    BUCore.getApi().getPlugin().getDescription().getName(),
                    BUCore.getApi().getLanguageManager().getDefaultLanguage()
            );
        }
        return user.getLanguageConfig();
    }
}