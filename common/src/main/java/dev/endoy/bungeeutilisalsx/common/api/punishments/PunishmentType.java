package dev.endoy.bungeeutilisalsx.common.api.punishments;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.IConfiguration;

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