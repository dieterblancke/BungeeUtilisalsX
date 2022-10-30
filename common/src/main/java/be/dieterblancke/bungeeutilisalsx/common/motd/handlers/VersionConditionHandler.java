package be.dieterblancke.bungeeutilisalsx.common.motd.handlers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.VersionsConfig.Version;
import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionHandler;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;

import java.util.stream.Collectors;

public class VersionConditionHandler extends ConditionHandler
{

    public VersionConditionHandler( String condition )
    {
        super( condition.replaceFirst( "version ", "" ) );
    }

    @Override
    public boolean checkCondition( MotdConnection connection )
    {
        Version version = ConfigFiles.VERSIONS_CONFIG.getVersionByName( value )
                .orElseGet( () ->
                {
                    BuX.getLogger().warning( "Found an invalid version in condition 'version " + condition + "', using 'unknown' version instead!" );
                    BuX.getLogger().warning( "Available versions:" );
                    BuX.getLogger().warning( listVersions() );

                    return ConfigFiles.VERSIONS_CONFIG.getUnknownVersion();
                } );

        return switch ( operator )
                {
                    case LT -> connection.getVersion() < version.protocolVersion();
                    case LTE -> connection.getVersion() <= version.protocolVersion();
                    case EQ -> connection.getVersion() == version.protocolVersion();
                    case NOT_EQ -> connection.getVersion() != version.protocolVersion();
                    case GTE -> connection.getVersion() >= version.protocolVersion();
                    case GT -> connection.getVersion() > version.protocolVersion();
                };
    }

    private String listVersions()
    {
        return ConfigFiles.VERSIONS_CONFIG.getVersions()
                .stream()
                .map( Version::versionName )
                .collect( Collectors.joining( ", " ) );
    }
}