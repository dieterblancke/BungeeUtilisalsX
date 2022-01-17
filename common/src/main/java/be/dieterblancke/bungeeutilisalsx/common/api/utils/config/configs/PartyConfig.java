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

package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class PartyConfig extends Config
{

    private final List<PartyRole> partyRoles = new ArrayList<>();
    private final List<ServerGroup> disabledWarpServers = new ArrayList<>();

    public PartyConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.partyRoles.clear();
        this.disabledWarpServers.clear();
    }

    @Override
    public void setup()
    {
        if ( config == null )
        {
            return;
        }

        config.getSectionList( "party-roles" )
                .forEach( section -> this.partyRoles.add( new PartyRole(
                        section.getString( "name" ),
                        section.exists( "default" ) && section.getBoolean( "default" ),
                        section.exists( "priority" ) ? section.getInteger( "priority" ) : 0,
                        Optional.ofNullable( section.getStringList( "permissions" ) ).orElse( new ArrayList<>() )
                                .stream()
                                .map( PartyRolePermission::valueOf )
                                .collect( Collectors.toList() )
                ) ) );

        if ( getDefaultRole() == null )
        {
            partyRoles.add( new PartyRole( "MEMBER", true, 0, new ArrayList<>() ) );
        }

        config.getStringList( "disabled-warp-from-servers" )
                .stream()
                .map( serverName -> ConfigFiles.SERVERGROUPS.getServer( serverName ) )
                .forEach( this.disabledWarpServers::add );
    }

    public int getPartyInactivityPeriod()
    {
        return config.getInteger( "inactivity-period.party" );
    }

    public int getPartyMemberInactivityPeriod()
    {
        return config.getInteger( "inactivity-period.party-member" );
    }

    public List<PartyRole> getPartyRoles()
    {
        return partyRoles;
    }

    public Optional<PartyRole> findPartyRole( final String partyRole )
    {
        if ( Strings.isNullOrEmpty( partyRole ) )
        {
            return Optional.empty();
        }
        return partyRoles
                .stream()
                .filter( it -> it.getName().equals( partyRole ) )
                .findAny();
    }

    public PartyRole getDefaultRole()
    {
        return partyRoles.stream().filter( PartyRole::isDefaultRole ).findFirst().orElse( null );
    }

    public boolean canWarpFrom( final String currentMemberServer )
    {
        return disabledWarpServers.stream().noneMatch( group -> group.isInGroup( currentMemberServer ) );
    }

    public enum PartyRolePermission
    {
        INVITE,
        KICK,
        WARP
    }

    @Value
    public static class PartyRole
    {
        String name;
        boolean defaultRole;
        int priority;
        List<PartyRolePermission> permissions;
    }
}
