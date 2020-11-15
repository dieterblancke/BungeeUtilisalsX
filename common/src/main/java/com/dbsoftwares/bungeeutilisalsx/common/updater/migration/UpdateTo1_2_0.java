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

package com.dbsoftwares.bungeeutilisalsx.common.updater.migration;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.StorageType;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

import static com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI.formatMessage;

@Slf4j
public class UpdateTo1_2_0 implements Update
{

    @Override
    public void update()
    {
        if ( BuX.getInstance().getAbstractStorageManager().getType() == StorageType.MONGODB )
        {
            return;
        }
        // TODO
    }
}
