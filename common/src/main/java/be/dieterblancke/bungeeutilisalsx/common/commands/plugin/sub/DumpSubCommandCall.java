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

package be.dieterblancke.bungeeutilisalsx.common.commands.plugin.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.dump.Dump;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class DumpSubCommandCall implements CommandCall, TabCall
{

    @Override
    @SuppressWarnings( "all" )
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Dump dump = getDump();
        BuX.getInstance().getScheduler().runAsync( () ->
        {
            final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            try
            {
                final HttpURLConnection con = (HttpURLConnection) new URL( "https://paste.dieterblancke.xyz/documents/" ).openConnection();

                con.addRequestProperty(
                        "User-Agent", "BungeeUtilisalsX v" + BuX.getInstance().getVersion()
                );
                con.setRequestMethod( "POST" );
                con.setRequestProperty( "Content-Type", "application/json" );
                con.setRequestProperty( "charset", "utf-8" );
                con.setDoOutput( true );

                final OutputStream out = con.getOutputStream();
                out.write( gson.toJson( dump ).getBytes( StandardCharsets.UTF_8 ) );
                out.close();

                if ( con.getResponseCode() == 429 )
                {
                    user.sendMessage( "&eYou have exceeded the allowed amount of dumps per minute." );
                    return;
                }

                final String response = CharStreams.toString( new InputStreamReader( con.getInputStream() ) );
                con.getInputStream().close();

                final JsonObject jsonResponse = gson.fromJson( response, JsonObject.class );

                if ( !jsonResponse.has( "key" ) )
                {
                    throw new IllegalStateException( "Could not create dump correctly, did something go wrong?" );
                }

                user.sendMessage( "&eSuccessfully created a dump at: "
                        + "&bhttps://paste.dieterblancke.xyz/" + jsonResponse.get( "key" ).getAsString() + ".dump" );
            }
            catch ( IOException e )
            {
                user.sendMessage( "Could not create dump. Please check the console for errors." );
                BuX.getLogger().log( Level.SEVERE, "Could not create dump request:", e );
            }
        } );
    }

    private Dump getDump()
    {
        final OperatingSystemMXBean operatingSystemMXBean = ( (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean() );
        final long totalMemory = bytesToMegaBytes( operatingSystemMXBean.getTotalPhysicalMemorySize() );
        final long freeMemory = bytesToMegaBytes( operatingSystemMXBean.getFreePhysicalMemorySize() );
        final long usedMemory = totalMemory - freeMemory;
        final File root = new File( "/" );
        final File bungeeRoot = Paths.get( "." ).toFile();

        final Map<String, Object> systemInfo = Maps.newLinkedHashMap();
        systemInfo.put( "javaVersion", System.getProperty( "java.version" ) );
        systemInfo.put( "operatingSystem", System.getProperty( "os.name" ) );
        systemInfo.put( "arch", ManagementFactory.getOperatingSystemMXBean().getArch() );
        systemInfo.put( "systemMaxMemory", totalMemory + " MB" );
        systemInfo.put( "systemUsedMemory", usedMemory + " MB" );
        systemInfo.put( "systemFreeMemory", freeMemory + " MB" );
        systemInfo.put( "amountOfSystemCores", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors() );
        systemInfo.put( "averageLoad", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage() );
        systemInfo.put( "systemStorage", bytesToGigaBytes( root.getFreeSpace() ) + " GB / " + bytesToGigaBytes( root.getTotalSpace() ) + " GB" );
        systemInfo.put( "____________________", "____________________" );
        systemInfo.put( "proxyVersion", BuX.getInstance().proxyOperations().getProxyIdentifier() );
        systemInfo.put( "startup", new SimpleDateFormat( "kk:mm dd-MM-yyyy" ).format( new Date( ManagementFactory.getRuntimeMXBean().getStartTime() ) ) );
        systemInfo.put( "maxMemory", bytesToMegaBytes( Runtime.getRuntime().totalMemory() ) + " MB" );
        systemInfo.put( "usedMemory", bytesToMegaBytes( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) + " MB" );
        systemInfo.put( "freeMemory", bytesToMegaBytes( Runtime.getRuntime().freeMemory() ) + " MB" );
        systemInfo.put( "amountOfAvailableCores", Runtime.getRuntime().availableProcessors() );
        systemInfo.put( "onlinePlayers", BuX.getApi().getPlayerUtils().getTotalCount() );
        systemInfo.put( "proxyStorage", bytesToGigaBytes( bungeeRoot.getFreeSpace() ) + " GB / " + bytesToGigaBytes( bungeeRoot.getTotalSpace() ) + " GB" );

        final Map<String, Map<String, Object>> configurations = Maps.newHashMap();
        for ( Config config : ConfigFiles.getAllConfigs() )
        {
            final Map<String, Object> values = readValues( config.getConfig().getValues() );
            configurations.put( config.toString().toLowerCase(), values );
        }

        return new Dump(
                "BungeeUtilisalsX",
                systemInfo,
                BuX.getInstance().proxyOperations().getPlugins(),
                configurations
        );
    }

    private Map<String, Object> readValues( Map<String, Object> map )
    {
        final Map<String, Object> values = Maps.newHashMap();

        map.forEach( ( key, value ) ->
        {
            if ( value instanceof List )
            {
                final List<Map<String, Object>> sectionList = Lists.newArrayList();

                for ( Object item : (List) value )
                {
                    if ( item instanceof ISection )
                    {
                        sectionList.add( readValues( ( (ISection) item ).getValues() ) );
                    }
                }
                if ( !sectionList.isEmpty() )
                {
                    values.put( key, sectionList );
                }
                else
                {
                    values.put( key, value );
                }
            }
            else if ( value instanceof ISection )
            {
                values.put( key, readValues( ( (ISection) value ).getValues() ) );
            }
            else
            {
                if ( key.endsWith( "password" ) )
                {
                    values.put( key, "***********" );
                }
                else
                {
                    if ( value instanceof String )
                    {
                        value = ( (String) value ).replace( "\r\n", " " ).replace( "\n", " " );
                    }
                    values.put( key, value );
                }
            }
        } );

        return values;
    }

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }

    private long bytesToMegaBytes( long bytes )
    {
        return bytes / 1024 / 1024;
    }

    private long bytesToGigaBytes( long bytes )
    {
        return bytesToMegaBytes( bytes ) / 1024;
    }
}
