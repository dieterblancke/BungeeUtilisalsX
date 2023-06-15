package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.configuration.api.FileStorageType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class VersionsConfig extends Config
{

    @Getter
    private final List<Version> versions = new ArrayList<>();

    public VersionsConfig()
    {
        super( FileStorageType.JSON, "configurations/_internal/versions.json" );
    }

    @Override
    public void purge()
    {
        versions.clear();
    }

    @Override
    public void setup()
    {
        this.versions.addAll(
                config.getSectionList( "versions" )
                        .stream()
                        .map( section -> new Version(
                                section.getString( "versionName" ),
                                section.getInteger( "protocolVersion" )
                        ) )
                        .toList()
        );
    }

    public Version getVersion( int protocolVersion )
    {
        return versions.stream()
                .filter( version -> version.protocolVersion() == protocolVersion )
                .findFirst()
                .orElse( this.getUnknownVersion() );
    }

    public Version getUnknownVersion()
    {
        return versions.stream()
                .filter( version -> version.protocolVersion() == -1 )
                .findFirst()
                .orElse( new Version( "Unknown", -1 ) );
    }

    public Version getLatestVersion()
    {
        return versions.stream()
                .max( Comparator.comparing( Version::protocolVersion ) )
                .orElse( this.getUnknownVersion() );
    }

    public Optional<Version> getVersionByName( String value )
    {
        return versions.stream()
                .filter( version -> version.versionName().equals( value ) )
                .findFirst();
    }

    public record Version(String versionName, int protocolVersion)
    {
    }
}
