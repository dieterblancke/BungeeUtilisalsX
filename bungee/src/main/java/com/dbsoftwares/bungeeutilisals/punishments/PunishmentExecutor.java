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

package com.dbsoftwares.bungeeutilisals.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public boolean isTemplateReason(final String reason) {
        if (!FileLocation.PUNISHMENTS.getConfiguration().getBoolean("templates.enabled")) {
            return false;
        }
        return reason.startsWith(FileLocation.PUNISHMENTS.getConfiguration().getString("templates.detect"));
    }

    @Override
    public List<String> searchTemplate(final IConfiguration config, final PunishmentType type, String template) {
        template = template.replaceFirst(
                FileLocation.PUNISHMENTS.getConfiguration().getString("templates.detect"),
                ""
        );
        final List<ISection> sections = config.getSectionList("punishments.templates");

        for (ISection section : sections) {
            if (!section.getString("name").equals(template)) {
                continue;
            }
            final List<PunishmentType> types = formatPunishmentTypes(section.getString("use_for"));

            if (!types.contains(type)) {
                continue;
            }
            return section.getStringList("lines");
        }
        return null;
    }

    private List<PunishmentType> formatPunishmentTypes(final String str) {
        final List<PunishmentType> types = Lists.newArrayList();

        // check for separator ", "
        for (String s : str.split(", ")) {
            final PunishmentType type = Utils.valueOfOr(PunishmentType.class, s, null);

            if (type != null) {
                types.add(type);
            }
        }

        if (types.isEmpty()) {
            // check for separator ","
            for (String s : str.split(",")) {
                final PunishmentType type = Utils.valueOfOr(PunishmentType.class, s, null);

                if (type != null) {
                    types.add(type);
                }
            }
        }

        if (types.isEmpty()) {
            types.add(Utils.valueOfOr(PunishmentType.class, str, PunishmentType.BAN));
        }
        return types;
    }

    @Override
    public String getDateFormat() {
        return FileLocation.PUNISHMENTS.getConfiguration().getString("date-format");
    }

    @Override
    public String setPlaceHolders(String line, PunishmentInfo info) {
        line = line.replace("{reason}", info.getReason());
        line = line.replace("{date}", Utils.formatDate(getDateFormat(), info.getDate()));
        line = line.replace("{by}", info.getExecutedBy());
        line = line.replace("{server}", info.getServer());

        // Just adding in case someone wants them ...
        line = line.replace("{uuid}", info.getUuid().toString());
        line = line.replace("{ip}", info.getIp());
        line = line.replace("{user}", info.getUser());
        line = line.replace("{id}", info.getId());
        line = line.replace("{type}", info.getType().toString().toLowerCase());

        if (info.getExpireTime() == null) {
            line = line.replace("{expire}", "Never");
        } else {
            line = line.replace("{expire}", Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        }
        if (info.getRemovedBy() == null) {
            line = line.replace("{expire}", "Unknown");
        } else {
            line = line.replace("{removedBy}", info.getRemovedBy());
        }
        return line;
    }

    @Override
    public List<String> getPlaceHolders(PunishmentInfo info) {
        final List<String> placeholders = Lists.newArrayList();

        if (info.getReason() != null) {
            placeholders.add("{reason}");
            placeholders.add(info.getReason());
        }

        if (info.getDate() != null) {
            placeholders.add("{date}");
            placeholders.add(Utils.formatDate(getDateFormat(), info.getDate()));
        }

        if (info.getExecutedBy() != null) {
            placeholders.add("{by}");
            placeholders.add(info.getExecutedBy());
        }

        if (info.getServer() != null) {
            placeholders.add("{server}");
            placeholders.add(info.getServer());
        }

        // Just adding in case someone wants them ...
        if (info.getUuid() != null) {
            placeholders.add("{uuid}");
            placeholders.add(info.getUuid().toString());
        }

        if (info.getIp() != null) {
            placeholders.add("{ip}");
            placeholders.add(info.getIp());
        }

        if (info.getUser() != null) {
            placeholders.add("{user}");
            placeholders.add(info.getUser());
        }

        placeholders.add("{id}");
        placeholders.add(String.valueOf(info.getId()));

        if (info.getType() != null) {
            placeholders.add("{type}");
            placeholders.add(info.getType().toString().toLowerCase());
        }

        placeholders.add("{expire}");
        if (info.getExpireTime() != null) {
            placeholders.add(Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        } else {
            placeholders.add("Never");
        }

        placeholders.add("{removedBy}");
        if (info.getRemovedBy() != null) {
            placeholders.add(info.getRemovedBy());
        } else {
            placeholders.add("Unknown");
        }

        return placeholders;
    }
}