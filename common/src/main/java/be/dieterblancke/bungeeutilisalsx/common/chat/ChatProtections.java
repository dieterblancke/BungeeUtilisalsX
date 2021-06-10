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

package be.dieterblancke.bungeeutilisalsx.common.chat;

import be.dieterblancke.bungeeutilisalsx.common.chat.protections.AdvertisementChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.protections.CapsChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.protections.SpamChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.protections.SwearChatProtection;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ChatProtections
{

    public static ChatProtection ADVERTISEMENT_PROTECTION = new AdvertisementChatProtection();
    public static ChatProtection CAPS_PROTECTION = new CapsChatProtection();
    public static ChatProtection SPAM_PROTECTION = new SpamChatProtection();
    public static SwearChatProtection SWEAR_PROTECTION = new SwearChatProtection();

    public static void reloadAllProtections()
    {
        final List<ChatProtection> protections = getAllProtections();

        for ( ChatProtection protection : protections )
        {
            protection.reload();
        }
    }

    public static List<ChatProtection> getAllProtections()
    {
        final List<ChatProtection> protections = Lists.newArrayList();

        for ( Field field : ChatProtections.class.getFields() )
        {
            if ( !Modifier.isStatic( field.getModifiers() ) )
            {
                continue;
            }
            try
            {
                final Object value = field.get( null );

                if ( value instanceof ChatProtection )
                {
                    protections.add( (ChatProtection) value );
                }
            }
            catch ( IllegalAccessException e )
            {
                // ignore
            }
        }
        return protections;
    }
}