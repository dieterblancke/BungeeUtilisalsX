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

package com.dbsoftwares.bungeeutilisalsx.common.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SQLBansDao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SQLKickAndWarnDao;
import com.dbsoftwares.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SQLMutesDao;
import lombok.extern.java.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;


public class SQLPunishmentDao implements PunishmentDao
{

    private final BansDao bansDao;
    private final MutesDao mutesDao;
    private final KickAndWarnDao kickAndWarnDao;

    public SQLPunishmentDao()
    {
        this.bansDao = new SQLBansDao();
        this.mutesDao = new SQLMutesDao();
        this.kickAndWarnDao = new SQLKickAndWarnDao();
    }

    @Override
    public BansDao getBansDao()
    {
        return bansDao;
    }

    @Override
    public MutesDao getMutesDao()
    {
        return mutesDao;
    }

    @Override
    public KickAndWarnDao getKickAndWarnDao()
    {
        return kickAndWarnDao;
    }

    @Override
    public long getPunishmentsSince( PunishmentType type, UUID uuid, Date date )
    {
        int count = 0;

        if ( type.isActivatable() )
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= ? AND type = ? AND punishmentaction_status = ?;"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, Dao.formatDateToString( date ) );
                pstmt.setString( 3, type.toString() );
                pstmt.setBoolean( 4, false );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        count = rs.getInt( 1 );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }
        else
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= ? AND punishmentaction_status = ?;"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, Dao.formatDateToString( date ) );
                pstmt.setBoolean( 3, false );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        count = rs.getInt( 1 );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }

        return count;
    }

    @Override
    public long getIPPunishmentsSince( PunishmentType type, String ip, Date date )
    {
        int count = 0;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT COUNT(id) FROM " + type.getTable() + " WHERE ip = ? AND date >= ? AND type = ? AND punishmentaction_status = ?;"
              ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setString( 2, Dao.formatDateToString( date ) );
            pstmt.setString( 3, type.toString() );
            pstmt.setBoolean( 4, false );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    count = rs.getInt( 1 );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured", e );
        }
        return count;
    }

    @Override
    public void updateActionStatus( int limit, PunishmentType type, UUID uuid, Date date )
    {
        if ( type.isActivatable() )
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE uuid = ? AND date >= ? AND type = ? AND punishmentaction_status = ? LIMIT ?;"
                  ) )
            {
                pstmt.setBoolean( 1, true );
                pstmt.setString( 2, uuid.toString() );
                pstmt.setString( 3, Dao.formatDateToString( date ) );
                pstmt.setString( 4, type.toString() );
                pstmt.setBoolean( 5, false );
                pstmt.setInt( 6, limit );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }
        else
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE uuid = ? AND date >= ? AND punishmentaction_status = ? LIMIT ?;"
                  ) )
            {
                pstmt.setBoolean( 1, true );
                pstmt.setString( 2, uuid.toString() );
                pstmt.setString( 3, Dao.formatDateToString( date ) );
                pstmt.setBoolean( 4, false );
                pstmt.setInt( 5, limit );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }
    }

    @Override
    public void updateIPActionStatus( int limit, PunishmentType type, String ip, Date date )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE ip = ? AND date >= ? AND type = ? AND punishmentaction_status = ? LIMIT ?;"
              ) )
        {
            pstmt.setBoolean( 1, true );
            pstmt.setString( 2, ip );
            pstmt.setString( 3, Dao.formatDateToString( date ) );
            pstmt.setString( 4, type.toString() );
            pstmt.setBoolean( 5, false );
            pstmt.setInt( 6, limit );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }

    @Override
    public void savePunishmentAction( UUID uuid, String username, String ip, String uid )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( PlaceHolderAPI.formatMessage(
                      "INSERT INTO {punishmentactions-table} (uuid, user, ip, actionid) VALUES (?, ?, ?, ?);"
              ) ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, username );
            pstmt.setString( 3, ip );
            pstmt.setString( 4, uid );

            pstmt.execute();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured", e );
        }
    }
}
