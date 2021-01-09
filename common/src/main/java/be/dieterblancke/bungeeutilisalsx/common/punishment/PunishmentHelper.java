package be.dieterblancke.bungeeutilisalsx.common.punishment;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class PunishmentHelper implements IPunishmentHelper
{

    @Override
    public boolean isTemplateReason( final String reason )
    {
        if ( !ConfigFiles.PUNISHMENTS.getConfig().getBoolean( "templates.enabled" ) )
        {
            return false;
        }
        return reason.startsWith( ConfigFiles.PUNISHMENTS.getConfig().getString( "templates.detect" ) );
    }

    @Override
    public List<String> searchTemplate( final IConfiguration config, final PunishmentType type, String template )
    {
        template = template.replaceFirst(
                ConfigFiles.PUNISHMENTS.getConfig().getString( "templates.detect" ),
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
        return ConfigFiles.PUNISHMENTS.getConfig().getString( "date-format" );
    }

    @Override
    public String setPlaceHolders( String line, PunishmentInfo info )
    {
        if ( line == null || info == null )
        {
            return null;
        }
        line = line.replace( "{reason}", info.getReason() );
        line = line.replace( "{date}", Utils.formatDate( getDateFormat(), info.getDate() ) );
        line = line.replace( "{by}", info.getExecutedBy() );
        line = line.replace( "{server}", info.getServer() );

        // Just adding in case someone wants them ...
        line = line.replace( "{uuid}", info.getUuid().toString() );
        line = line.replace( "{ip}", info.getIp() );
        line = line.replace( "{user}", info.getUser() );
        line = line.replace( "{id}", info.getId() );
        line = line.replace( "{type}", info.getType().toString().toLowerCase() );
        line = line.replace( "{punishment_uid}", info.getPunishmentUid() == null ? "" : info.getPunishmentUid() );

        if ( info.getExpireTime() == null )
        {
            line = line.replace( "{expire}", "Never" );
        }
        else
        {
            line = line.replace( "{expire}", Utils.formatDate( getDateFormat(), new Date( info.getExpireTime() ) ) );
        }
        if ( info.getRemovedBy() == null )
        {
            line = line.replace( "{expire}", "Unknown" );
        }
        else
        {
            line = line.replace( "{removedBy}", info.getRemovedBy() );
        }
        return line;
    }

    @Override
    public List<String> getPlaceHolders( PunishmentInfo info )
    {
        final List<String> placeholders = Lists.newArrayList();

        if ( info.getReason() != null )
        {
            placeholders.add( "{reason}" );
            placeholders.add( info.getReason() );
        }

        if ( info.getDate() != null )
        {
            placeholders.add( "{date}" );
            placeholders.add( Utils.formatDate( getDateFormat(), info.getDate() ) );
        }

        if ( info.getExecutedBy() != null )
        {
            placeholders.add( "{by}" );
            placeholders.add( info.getExecutedBy() );
        }

        if ( info.getServer() != null )
        {
            placeholders.add( "{server}" );
            placeholders.add( info.getServer() );
        }

        // Just adding in case someone wants them ...
        if ( info.getUuid() != null )
        {
            placeholders.add( "{uuid}" );
            placeholders.add( info.getUuid().toString() );
        }

        if ( info.getIp() != null )
        {
            placeholders.add( "{ip}" );
            placeholders.add( info.getIp() );
        }

        if ( info.getUser() != null )
        {
            placeholders.add( "{user}" );
            placeholders.add( info.getUser() );
        }

        placeholders.add( "{id}" );
        placeholders.add( String.valueOf( info.getId() ) );

        if ( info.getType() != null )
        {
            placeholders.add( "{type}" );
            placeholders.add( info.getType().toString().toLowerCase() );
        }

        placeholders.add( "{expire}" );
        if ( info.getExpireTime() != null )
        {
            placeholders.add( Utils.formatDate( getDateFormat(), new Date( info.getExpireTime() ) ) );
        }
        else
        {
            placeholders.add( "Never" );
        }

        placeholders.add( "{removedBy}" );
        if ( info.getRemovedBy() != null )
        {
            placeholders.add( info.getRemovedBy() );
        }
        else
        {
            placeholders.add( "Unknown" );
        }

        placeholders.add( "{punishment_uid}" );
        placeholders.add( info.getPunishmentUid() == null ? "" : info.getPunishmentUid() );

        return placeholders;
    }
}
