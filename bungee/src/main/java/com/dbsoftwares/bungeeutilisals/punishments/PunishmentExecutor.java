package com.dbsoftwares.bungeeutilisals.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PunishmentExecutor implements IPunishmentExecutor {

    @Override
    public String getDateFormat() {
        return FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("date-format");
    }

    @Override
    public String setPlaceHolders(String line, PunishmentInfo info) {
        line = line.replace("{reason}", info.getReason());
        line = line.replace("{date}", Utils.formatDate(getDateFormat(), info.getDate()));
        line = line.replace("{by}", info.getExecutedBy());
        line = line.replace("{server}", info.getServer());

        // Just adding in case someone wants them ...
        line = line.replace("{uuid}", info.getUuid().toString());
        line = line.replace("{ip}", info.getIP());
        line = line.replace("{user}", info.getUser());
        line = line.replace("{id}", String.valueOf(info.getId()));

        // Checking if value is present, if so: replacing
        if (info.getExpireTime() != null) {
            line = line.replace("{expire}", Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        }
        return line;
    }

    @Override
    public List<String> getPlaceHolders(PunishmentInfo info) {
        List<String> placeholders = Lists.newArrayList();

        placeholders.add("{reason}");
        placeholders.add(info.getReason());
        placeholders.add("{date}");
        placeholders.add(Utils.formatDate(getDateFormat(), info.getDate()));
        placeholders.add("{by}");
        placeholders.add(info.getExecutedBy());
        placeholders.add("{server}");
        placeholders.add(info.getServer());

        // Just adding in case someone wants them ...
        placeholders.add("{uuid}");
        placeholders.add(info.getUuid().toString());
        placeholders.add("{ip}");
        placeholders.add(info.getIP());
        placeholders.add("{user}");
        placeholders.add(info.getUser());
        placeholders.add("{id}");
        placeholders.add(String.valueOf(info.getId()));

        // Checking if value is present);
        // placeholders.add(if so: replacing
        if (info.getExpireTime() != null) {
            placeholders.add("{expire}");
            placeholders.add(Utils.formatDate(getDateFormat(), new Date(info.getExpireTime())));
        }

        return placeholders;
    }
}