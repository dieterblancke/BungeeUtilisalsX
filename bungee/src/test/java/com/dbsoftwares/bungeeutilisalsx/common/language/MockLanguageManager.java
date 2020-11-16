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

package com.dbsoftwares.bungeeutilisalsx.common.language;

import com.dbsoftwares.bungeeutilisalsx.common.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.Language;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.LanguageIntegration;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MockLanguageManager implements ILanguageManager
{
    @Override
    public Language getLangOrDefault( String language )
    {
        return new Language( "en_US", true );
    }

    @Override
    public LanguageIntegration getLanguageIntegration()
    {
        return null;
    }

    @Override
    public void setLanguageIntegration( LanguageIntegration integration )
    {

    }

    @Override
    public Language getDefaultLanguage()
    {
        return new Language( "en_US", true );
    }

    @Override
    public Optional<Language> getLanguage( String language )
    {
        return Optional.of( new Language( "en_US", true ) );
    }

    @Override
    public void addPlugin( String plugin, File folder, FileStorageType type )
    {

    }

    @Override
    public void loadLanguages( String plugin )
    {

    }

    @Override
    public IConfiguration getLanguageConfiguration( String plugin, User user )
    {
        return null;
    }

    @Override
    public IConfiguration getLanguageConfiguration( String plugin, ProxiedPlayer player )
    {
        return null;
    }

    @Override
    public IConfiguration getLanguageConfiguration( String plugin, CommandSender sender )
    {
        return null;
    }

    @Override
    public File getFile( String plugin, Language language )
    {
        return null;
    }

    @Override
    public IConfiguration getConfig( String plugin, Language language )
    {
        return null;
    }

    @Override
    public Boolean isRegistered( String plugin, Language language )
    {
        return true;
    }

    @Override
    public Boolean saveLanguage( String plugin, Language language )
    {
        return true;
    }

    @Override
    public Boolean reloadConfig( String plugin, Language language )
    {
        return true;
    }

    @Override
    public List<Language> getLanguages()
    {
        return Collections.singletonList( new Language( "en_US", true ) );
    }
}
