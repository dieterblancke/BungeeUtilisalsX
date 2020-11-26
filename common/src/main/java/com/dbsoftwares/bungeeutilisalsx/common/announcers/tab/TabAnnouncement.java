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

package com.dbsoftwares.bungeeutilisalsx.common.announcers.tab;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode( callSuper = false )
public class TabAnnouncement extends Announcement
{

    private boolean language;
    private List<String> header;
    private List<String> footer;

    public TabAnnouncement( boolean language, List<String> header, List<String> footer, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.language = language;
        this.header = header;
        this.footer = footer;
    }

    public void send()
    {
        if ( serverGroup.isGlobal() )
        {
            send( filter( BuX.getApi().getUsers().stream() ) );
        }
        else
        {
            serverGroup.getServers().forEach( server -> send( filter( server.getUsers().stream() ) ) );
        }
    }

    private void send( final Stream<User> stream )
    {
        stream.forEach( user ->
        {
            final IConfiguration config = user.getLanguageConfig();

            final List<String> headers = language ? config.getStringList( this.header.get( 0 ) ) : this.header;
            final List<String> footers = language ? config.getStringList( this.footer.get( 0 ) ) : this.footer;

            user.setTabHeader(
                    Utils.format( user, headers ),
                    Utils.format( user, footers )
            );
        } );
    }
}