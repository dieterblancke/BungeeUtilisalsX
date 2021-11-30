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
import com.dbsoftwares.configuration.api.ISection;
import lombok.Getter;

public class WebhookConfig extends Config
{

    @Getter
    private DiscordWebhookConfig discordWebhook;

    public WebhookConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        discordWebhook = null;
    }

    @Override
    public void setup()
    {
        if ( config == null )
        {
            return;
        }

        this.discordWebhook = DiscordWebhookConfig.fromSection( config.getSection( "webhooks.discord" ) );
    }

    public record DiscordWebhookConfig(boolean enabled,
                                       String url,
                                       String userName,
                                       String avatarUrl)
    {
        public static DiscordWebhookConfig fromSection( final ISection section )
        {
            return new DiscordWebhookConfig(
                    section.getBoolean( "enabled" ),
                    section.getString( "url" ),
                    section.getString( "username" ),
                    section.getString( "avatar-url" )
            );
        }
    }
}
