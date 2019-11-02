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

package com.dbsoftwares.bungeeutilisals.commands.addons.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.AddonData;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddonInfoSubCommand extends SubCommand
{

    public AddonInfoSubCommand()
    {
        super( "info", 1 );
    }

    @Override
    public String getUsage()
    {
        return "/addons info (name)";
    }

    @Override
    public String getPermission()
    {
        return "bungeeutilisals.admin.addons.info";
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        final List<AddonData> addons = BUCore.getApi().getAddonManager().getAllAddons();

        final Optional<AddonData> dataOptional = addons.stream().filter( addon -> addon.getName().equalsIgnoreCase( args[0] ) ).findFirst();
        if ( dataOptional.isPresent() )
        {
            final AddonData data = dataOptional.get();
            final boolean installed = BUCore.getApi().getAddonManager().isRegistered( data.getName() );

            user.sendLangMessage( "general-commands.addon.info",
                    "{installed}", installed ? "Yes" : "No",
                    "{name}", data.getName(),
                    "{version}", data.getVersion() + ( installed ? " (local version: " + BUCore.getApi().getAddonManager().getAddon( data.getName() ).getDescription().getVersion() + ")" : "" ),
                    "{author}", data.getAuthor(),
                    "{reqDepends}", data.getRequiredDependencies() == null ? "None" : Utils.formatList( data.getRequiredDependencies(), ", " ),
                    "{optDepends}", data.getOptionalDependencies() == null ? "None" : Utils.formatList( data.getOptionalDependencies(), ", " ),
                    "{description}", data.getDescription()
            );
        } else
        {
            user.sendLangMessage( "general-commands.addon.notfound", "{name}", args[0] );
        }
    }

    @Override
    public List<String> getCompletions( User user, String[] args )
    {
        return BUCore.getApi().getAddonManager().getAllAddons().stream().map( AddonData::getName ).collect( Collectors.toList() );
    }

}
