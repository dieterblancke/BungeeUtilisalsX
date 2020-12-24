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

package be.dieterblancke.bungeeutilisalsx.spigot.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultPlaceHolders implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        final ISection section = ConfigFiles.CONFIG.getConfig().getSection( "storage.schemas" );

        for ( String key : section.getKeys() )
        {
            PlaceHolderAPI.addPlaceHolder(
                    "{" + key + "-table}",
                    false,
                    event -> section.getString( key )
            );
        }

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
        final String format = configuration.getString( "placeholders.format." + type );
        if ( format == null )
        {
            return "";
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat( format );

        return dateFormat.format( new Date() );
    }

    private IConfiguration getLanguageConfiguration( User user )
    {
        if ( user == null )
        {
            return BuX.getApi().getLanguageManager().getConfig(
                    BuX.getInstance().getName(),
                    BuX.getApi().getLanguageManager().getDefaultLanguage()
            );
        }
        return user.getLanguageConfig();
    }
}