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

package be.dieterblancke.bungeeutilisalsx.common.api.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MojangUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.SneakyThrows;

import java.util.List;
import java.util.UUID;

public interface IPlayerUtils
{

    int getPlayerCount( String server );

    List<String> getPlayers( String server );

    int getTotalCount();

    List<String> getPlayers();

    IProxyServer findPlayer( String name );

    boolean isOnline( String name );

    UUID getUuidNoFallback( String targetName );

    @SneakyThrows
    default UUID getUuid( final String targetName )
    {
        UUID uuid = this.getUuidNoFallback( targetName );

        if ( uuid != null )
        {
            return uuid;
        }
        uuid = BuX.getApi().getStorageManager().getDao().getUserDao().getUuidFromName( targetName ).get();

        if ( uuid != null )
        {
            return uuid;
        }

        try
        {
            return Utils.readUUIDFromString( MojangUtils.getUuid( targetName ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}