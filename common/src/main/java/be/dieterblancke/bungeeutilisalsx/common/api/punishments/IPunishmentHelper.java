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

package be.dieterblancke.bungeeutilisalsx.common.api.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.util.List;
import java.util.UUID;

public interface IPunishmentHelper
{

    boolean isTemplateReason( final String reason );

    List<String> searchTemplate( final IConfiguration config, final PunishmentType type, String template );

    String getDateFormat();

    String getTimeLeftFormat();

    String setPlaceHolders( String line, PunishmentInfo info );

    List<String> getPlaceHolders( PunishmentInfo info );

    default boolean isHigherPunishment( UUID executor, UUID target )
    {
        return BuX.getInstance().getActivePermissionIntegration().hasLowerOrEqualGroup( executor, target )
                && !ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "allow-higher-punishments" );
    }
}