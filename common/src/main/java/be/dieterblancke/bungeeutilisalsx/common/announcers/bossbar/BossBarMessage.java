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

package be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar;

import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import com.dbsoftwares.configuration.api.ISection;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class BossBarMessage
{
    BarColor color;
    BarStyle style;
    float progress;
    boolean language;
    String text;

    public BossBarMessage( final ISection section )
    {
        this(
                BarColor.valueOf( section.getString( "color" ) ),
                BarStyle.valueOf( section.getString( "style" ) ),
                section.getFloat( "progress" ),
                section.exists( "language" ) ? section.getBoolean( "language" ) : false,
                section.getString( "text" )
        );
    }
}
