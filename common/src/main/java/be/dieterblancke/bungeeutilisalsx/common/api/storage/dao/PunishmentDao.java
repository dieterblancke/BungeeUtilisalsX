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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.KickAndWarnDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Validate;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.UUID;

public interface PunishmentDao
{

    static String getPunishmentIdCharacters() {
        return ConfigFiles.PUNISHMENTS.getConfig().getString( "puid-characters" );
    }

    static PunishmentInfo buildPunishmentInfo( final String id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        final PunishmentInfo info = new PunishmentInfo();

        info.setId( id );
        info.setUuid( uuid );
        info.setUser( user );
        info.setIp( ip );
        info.setReason( reason );
        info.setServer( server );
        info.setExecutedBy( executedby );
        info.setDate( date );
        info.setType( type );

        info.setExpireTime( time );
        info.setActive( active );
        Validate.ifNotNull( removedby, info::setRemovedBy );
        info.setPunishmentUid(punishmentUid);

        return info;
    }

    static PunishmentInfo buildPunishmentInfo( final int id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        return buildPunishmentInfo( String.valueOf( id ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }

    static PunishmentInfo buildPunishmentInfo( final String id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, null );
    }

    static PunishmentInfo buildPunishmentInfo( final int id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( String.valueOf( id ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
    }

    static PunishmentInfo buildPunishmentInfo( final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( -1, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
    }

    static PunishmentInfo buildPunishmentInfo( final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        return buildPunishmentInfo( String.valueOf( -1 ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }

    BansDao getBansDao();

    MutesDao getMutesDao();

    KickAndWarnDao getKickAndWarnDao();

    long getPunishmentsSince( PunishmentType type, UUID uuid, Date date );

    long getIPPunishmentsSince( PunishmentType type, String ip, Date date );

    void updateActionStatus( int limit, PunishmentType type, UUID uuid, Date date );

    void updateIPActionStatus( int limit, PunishmentType type, String ip, Date date );

    void savePunishmentAction( final UUID uuid, final String username, final String ip, final String uid );
}
