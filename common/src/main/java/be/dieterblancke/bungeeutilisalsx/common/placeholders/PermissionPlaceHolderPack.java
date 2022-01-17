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

package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

public class PermissionPlaceHolderPack implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{permission_user_prefix}", true, this::getPermissionUserPrefix );
        PlaceHolderAPI.addPlaceHolder( "{permission_user_suffix}", true, this::getPermissionUserSuffix );
        PlaceHolderAPI.addPlaceHolder( "{permission_user_primary_group}", true, this::getPermissionUserPrimaryGroup );
    }

    private String getPermissionUserPrefix( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getPrefix( event.getUser().getUuid() );
    }

    private String getPermissionUserSuffix( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getSuffix( event.getUser().getUuid() );
    }

    private String getPermissionUserPrimaryGroup( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getGroup( event.getUser().getUuid() ).join();
    }
}
