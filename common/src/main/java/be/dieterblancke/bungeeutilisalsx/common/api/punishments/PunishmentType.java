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

package be.dieterblancke.bungeeutilisalsx.common.api.punishments;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;

public enum PunishmentType
{

    BAN( true, false ), TEMPBAN( true, true ), IPBAN( true, false ), IPTEMPBAN( true, true ),
    MUTE( true, false ), TEMPMUTE( true, true ), IPMUTE( true, false ), IPTEMPMUTE( true, true ),
    KICK( false, false ), WARN( false, false );

    private final boolean activatable;
    private final boolean temporary;
    private final String table;

    PunishmentType( boolean activatable, boolean temporary )
    {
        this.activatable = activatable;
        this.temporary = temporary;

        final String toString = toString();
        final String type;

        if ( toString.contains( "BAN" ) )
        {
            type = "ban";
        }
        else if ( toString.contains( "MUTE" ) )
        {
            type = "mute";
        }
        else
        {
            type = toString.toLowerCase();
        }
        this.table = "bu_" + type + "s";
    }

    public boolean isActivatable()
    {
        return activatable;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    public String getTable()
    {
        return table;
    }

    public boolean isIP()
    {
        return toString().startsWith( "IP" );
    }

    public boolean isBan()
    {
        return toString().contains( "BAN" );
    }

    public boolean isMute()
    {
        return toString().contains( "MUTE" );
    }

    public boolean isEnabled()
    {
        final IConfiguration config = ConfigFiles.PUNISHMENT_CONFIG.getConfig();
        final String type = this.toString().toLowerCase();
        final String tempType = this.isIP()
                ? "iptemp" + this.toString().toLowerCase().replace( "ip", "" )
                : "temp" + type;

        return ( config.exists( "commands." + type + ".enabled" ) && config.getBoolean( "commands." + type + ".enabled" ) )
                || ( config.exists( "commands." + tempType + ".enabled" ) && config.getBoolean( "commands." + tempType + ".enabled" ) );
    }
}