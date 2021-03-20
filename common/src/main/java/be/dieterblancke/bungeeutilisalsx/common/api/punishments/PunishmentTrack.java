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

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PunishmentTrack
{

    private final String identifier;
    private final CountNormalPunishments countNormalPunishments;
    private final int maxRuns;
    private final TrackAction limitReachedAction;
    private final List<PunishmentTrackRecord> records;

    @Data
    @AllArgsConstructor
    public static final class CountNormalPunishments {

        private final boolean enabled;
        private final List<PunishmentType> types;

    }

    @Data
    @AllArgsConstructor
    public static final class PunishmentTrackRecord
    {

        private final int count;
        private final TrackAction action;

    }

    @Data
    @AllArgsConstructor
    public static final class TrackAction {

        private final PunishmentType typeAction;
        private final String duration;

    }
}
