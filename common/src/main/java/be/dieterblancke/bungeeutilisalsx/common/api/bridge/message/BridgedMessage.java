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

package be.dieterblancke.bungeeutilisalsx.common.api.bridge.message;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BridgedMessage
{

    private BridgeType type;
    private UUID identifier;
    private String from;
    private List<String> targets;
    private List<String> ignoredTargets;
    private String action;
    private String message;

    public BridgedMessage(
            final BridgeType type,
            final UUID identifier,
            final String from,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object message
    )
    {
        this.type = type;
        this.identifier = identifier;
        this.from = from;
        this.targets = targets;
        this.ignoredTargets = ignoredTargets;
        this.action = action;
        this.message = BuX.getGson().toJson( message );
    }
}
