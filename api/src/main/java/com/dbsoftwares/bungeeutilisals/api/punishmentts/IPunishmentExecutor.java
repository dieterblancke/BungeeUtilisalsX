package com.dbsoftwares.bungeeutilisals.api.punishmentts;

import java.util.LinkedHashMap;

public interface IPunishmentExecutor {

    /**
     * Adds a punishment to the User with the given type and info.
     * @param user The user you want to punish.
     * @param type The punishment type you want to execute. IPBAN, BAN or MUTE
     * @param info The punishment information you want for the punishment.
     */
    void addPunishment(String user, PunishmentType type, PunishmentInfo info);

    /**
     * Disables activity for an active punishment of an user.
     * @param user The user you want to remove the punishment from.
     * @param type The type you want to remove the punishment from.
     * @param remover The person who removed the punishment.
     */
    void removePunishment(String user, PunishmentType type, String remover);

    /**
     * @param user The user to check.
     * @param type The type of which you want to check.
     * @return True if punished, false if not.
     */
    Boolean isPunished(String user, PunishmentType type);

    /**
     * @param user The user to check.
     * @param type The type you watn to check.
     * @return True if punished before, false if not.
     */
    Boolean punishedBefore(String user, PunishmentType type);

    /**
     * @param user THe user of which you want to get the punishments.
     * @param type The punishment type you want to retrieve from.
     * @return All punishments of the given type in a LinkedHashMap, key = punishment info, value = remover of punishment, UNKNOWN if active.
     */
    LinkedHashMap<PunishmentInfo, String> getPunishments(String user, PunishmentType type);

    /**
     * @param user The user of which you want to get the punishments.
     * @return All punishments of the all types in a LinkedHashMap, key = punishment info, value = remover of punishment, UNKNOWN if active.
     */
    LinkedHashMap<PunishmentInfo, String> getPunishments(String user);

    /**
     * @param type The type you want to check on.
     * @return The active Punishment of a User of the given type, null if none.
     */
    PunishmentInfo getCurrentPunishment(String user, PunishmentType type);
}