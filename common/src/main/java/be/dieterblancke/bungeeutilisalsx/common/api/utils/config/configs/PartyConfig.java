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
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PartyConfig extends Config
{

    private final List<PartyRole> partyRoles = new ArrayList<>();

    public PartyConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.partyRoles.clear();
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
                        section.getStringList( "permissions" )
                                .stream()
                                .map( PartyRolePermission::valueOf )
                                .collect( Collectors.toList() )
                ) ) );
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

    public enum PartyRolePermission
    {
        INVITE,
        KICK,
        SETTINGS
    }

    @Value
    public static class PartyRole
    {
        String name;
        List<PartyRolePermission> permissions;
    }
}
