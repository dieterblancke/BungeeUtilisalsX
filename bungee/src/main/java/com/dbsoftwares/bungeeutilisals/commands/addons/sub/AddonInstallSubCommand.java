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
import com.dbsoftwares.bungeeutilisals.api.addon.Addon;
import com.dbsoftwares.bungeeutilisals.api.addon.AddonData;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.exceptions.AddonException;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AddonInstallSubCommand extends SubCommand
{

    public AddonInstallSubCommand()
    {
        super( "install", 1 );
    }

    @Override
    public String getUsage()
    {
        return "/addons install (name)";
    }

    @Override
    public String getPermission()
    {
        return "bungeeutilisals.admin.addons.install";
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        final String addonName = args[0];
        final Optional<AddonData> optional = BUCore.getApi().getAddonManager().getAllAddons().stream().filter( data -> data.getName().equalsIgnoreCase( addonName ) ).findFirst();

        if ( optional.isPresent() )
        {
            final AddonData addonData = optional.get();

            if ( addonData.getRequiredDependencies() != null )
            {
                for ( String depend : addonData.getRequiredDependencies() )
                {
                    if ( !BUCore.getApi().getAddonManager().isRegistered( depend ) )
                    {
                        user.sendLangMessage( "general-commands.addon.install.error.nodepend", "{dependencies}", Utils.formatList( addonData.getRequiredDependencies(), ", " ) );
                        return;
                    }
                }
            }

            try
            {
                File result = downloadAddon( addonData );

                BUCore.getApi().getAddonManager().loadSingleAddon( result );

                enableAddon( user, addonData.getName() );
            }
            catch ( Exception e )
            {
                user.sendLangMessage( "general-commands.addon.install.error.download", "{name}", addonName );
                BUCore.getLogger().error( "An error occured: ", e );
            }
        }
        else
        {
            user.sendLangMessage( "general-commands.addon.notfound", "{name}", addonName );
        }
    }

    @Override
    public List<String> getCompletions( User user, String[] args )
    {
        return BUCore.getApi().getAddonManager().getAllAddons().stream().map( AddonData::getName ).collect( Collectors.toList() );
    }

    private void enableAddon( User user, String addonName )
    {
        Addon addon = BUCore.getApi().getAddonManager().getAddon( addonName );
        try
        {
            addon.onEnable();
            BUCore.getLogger().info(
                    "Enabled addon " + addon.getDescription().getName() + " version "
                            + addon.getDescription().getVersion() + " by " + addon.getDescription().getAuthor()
            );
            user.sendLangMessage( "general-commands.addon.install.installed", "{name}", addonName );
        }
        catch ( final Throwable t )
        {
            user.sendLangMessage( "general-commands.addon.install.error.enable", "{name}", addonName );
            throw new AddonException( "Exception encountered when loading addon: " + addonName, t );
        }
    }

    private File downloadAddon( final AddonData data ) throws Exception
    {
        final File target = new File( BUCore.getApi().getAddonManager().getAddonsFolder(), data.getName() + ".jar" );

        final HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();
        final Gson gson = new Gson();
        final GenericUrl url = new GenericUrl( data.getDownloadURL() );
        final HttpRequest request = factory.buildGetRequest( url );
        final Future<HttpResponse> futureResponse = request.executeAsync();

        final HttpResponse response = futureResponse.get();
        if ( response.isSuccessStatusCode() )
        {
            try ( final InputStream input = response.getContent() )
            {
                Files.copy( input, Paths.get( target.toURI() ) );
            }
        }
        return target;
    }
}
