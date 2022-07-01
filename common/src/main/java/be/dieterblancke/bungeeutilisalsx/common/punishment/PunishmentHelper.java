package be.dieterblancke.bungeeutilisalsx.common.punishment;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class PunishmentHelper implements IPunishmentHelper
{

    @Override
    public boolean isTemplateReason( final String reason )
    {
        if ( !ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "templates.enabled" ) )
        {
            return false;
        }
        return reason.startsWith( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "templates.detect" ) );
    }

    @Override
    public List<String> searchTemplate( final IConfiguration config, final PunishmentType type, String template )
    {
        template = template.replaceFirst(
                ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "templates.detect" ),
                ""
        );
        final List<ISection> sections = config.getSectionList( "punishments.templates" );

        for ( ISection section : sections )
        {
            if ( !section.getString( "name" ).equals( template ) )
            {
                continue;
            }
            final List<PunishmentType> types = formatPunishmentTypes( section.getString( "use_for" ) );

            if ( !types.contains( type ) )
            {
                continue;
            }
            return section.getStringList( "lines" );
        }
        return null;
    }

    private List<PunishmentType> formatPunishmentTypes( final String str )
    {
        final List<PunishmentType> types = Lists.newArrayList();

        // check for separator ", "
        for ( String s : str.split( ", " ) )
        {
            final PunishmentType type = Utils.valueOfOr( PunishmentType.class, s, null );

            if ( type != null )
            {
                types.add( type );
            }
        }

        if ( types.isEmpty() )
        {
            // check for separator ","
            for ( String s : str.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( PunishmentType.class, s, null );

                if ( type != null )
                {
                    types.add( type );
                }
            }
        }

        if ( types.isEmpty() )
        {
            types.add( Utils.valueOfOr( PunishmentType.class, str, PunishmentType.BAN ) );
        }
        return types;
    }

    @Override
    public String getDateFormat()
    {
        return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "date-format" );
    }

    @Override
    public String getTimeLeftFormat()
    {
        return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "time-left-format" );
    }

    @Override
    public String setPlaceHolders( String line, PunishmentInfo info )
    {
        if ( line == null || info == null )
        {
            return null;
        }

        return getPlaceHolders( info ).format( line );
    }

    @Override
    public MessagePlaceholders getPlaceHolders( PunishmentInfo info )
    {
        MessagePlaceholders placeholders = MessagePlaceholders.create();

        if ( info.getReason() != null )
        {
            placeholders.append( "reason", info.getReason() );
        }

        if ( info.getDate() != null )
        {
            placeholders.append( "date", Utils.formatDate( getDateFormat(), info.getDate() ) );
        }

        if ( info.getExecutedBy() != null )
        {
            placeholders.append( "by", info.getExecutedBy() );
        }

        if ( info.getServer() != null )
        {
            placeholders.append( "server", info.getServer() );
        }

        // Just appending in case someone wants them ...
        if ( info.getUuid() != null )
        {
            placeholders.append( "uuid", info.getUuid().toString() );
        }

        if ( info.getIp() != null )
        {
            placeholders.append( "ip", info.getIp() );
        }

        if ( info.getUser() != null )
        {
            placeholders.append( "user", info.getUser() );
        }

        placeholders.append( "id", String.valueOf( info.getId() ) );

        if ( info.getType() != null )
        {
            placeholders.append( "type", info.getType().toString().toLowerCase() );
        }

        placeholders.append( "expire", info.getExpireTime() != null ? Utils.formatDate( getDateFormat(), new Date( info.getExpireTime() ) ) : "Never" );
        placeholders.append( "timeLeft", info.getExpireTime() != null ? Utils.getTimeLeft( getTimeLeftFormat(), info.getExpireTime() - System.currentTimeMillis() ) : "Never" );
        placeholders.append( "removedBy", info.getRemovedBy() != null ? info.getRemovedBy() : "Unknown" );
        placeholders.append( "punishment_uid", info.getPunishmentUid() == null ? "" : info.getPunishmentUid() );

        return placeholders;
    }
}
