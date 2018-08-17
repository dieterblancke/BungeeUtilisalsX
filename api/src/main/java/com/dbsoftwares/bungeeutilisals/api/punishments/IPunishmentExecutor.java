package com.dbsoftwares.bungeeutilisals.api.punishments;

import java.util.List;

public interface IPunishmentExecutor {

    String getDateFormat();

    String setPlaceHolders(String line, PunishmentInfo info);

    List<String> getPlaceHolders(PunishmentInfo info);
}