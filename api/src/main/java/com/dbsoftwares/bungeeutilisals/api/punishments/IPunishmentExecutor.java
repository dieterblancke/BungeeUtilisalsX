package com.dbsoftwares.bungeeutilisals.api.punishments;

import java.util.LinkedHashMap;

public interface IPunishmentExecutor {

    /**
     * Adds a permanent punishment to the user. (Ban, IPBan, Mute, IPMute, Kick, Warn)
     * @param type The punishment type you want to execute. PERMANENT PUNISHMENTS ONLY
     * @param info The punishment information you want for the punishment.
     */
    void addPunishment(PunishmentType type, PunishmentInfo info);

    /**
     * Adds a temporary punishment to the user. (Tempban, Tempmute, TempbanIP, TempmuteIP)
     *
     * @param type   The punishment type you want to execute. PERMANENT PUNISHMENTS ONLY
     * @param info   The punishment information you want for the punishment.
     * @param expire The future time on which the punishment should expire (in Milliseconds)
     */
    void addTemporaryPunishment(PunishmentType type, PunishmentInfo info, long expire);

    /**
     * Disables activity for an active punishment of an user.
     * @param user The user you want to remove the punishment from.
     * @param type The type you want to remove the punishment from.
     * @param removedby The person who removed the punishment, 'CONSOLE' in case of console.
     */
    void removePunishment(String user, PunishmentType type, String removedby);

    /**
     * @param uuid True if it should check on UUID, false if not.
     * @param user The user to check.
     * @param type The type of which you want to check.
     * @return True if punished, false if not.
     */
    Boolean hasPunishment(boolean uuid, String user, PunishmentType type);

    /**
     * @param uuid True if it should check on UUID, false if not.
     * @param user The user to check.
     * @param type The type you watn to check.
     * @return True if punished before, false if not.
     */
    Boolean hasPastPunishment(boolean uuid, String user, PunishmentType type);

    /**
     * @param uuid True if it should check on UUID, false if not.
     * @param user THe user of which you want to get the punishments.
     * @param type The punishment type you want to retrieve from.
     * @return All punishments of the given type in a LinkedHashMap, key = punishment info,
     * value = remover of punishment, UNKNOWN if active.
     */
    LinkedHashMap<PunishmentInfo, String> getPunishments(boolean uuid, String user, PunishmentType type);

    /**
     * @param uuid True if it should check on UUID, false if not.
     * @param user The user of which you want to get the punishments.
     * @return All punishments of the all types in a LinkedHashMap, key = punishment info,
     * value = remover of punishment, UNKNOWN if active.
     */
    LinkedHashMap<PunishmentInfo, String> getPunishments(boolean uuid, String user);

    /**
     * @param uuid True if it should check on UUID, false if not.
     * @param user The user of which you want to get the current punishment.
     * @param type The type you want to check on.
     * @return The active Punishment of a User of the given type, null if none.
     */
    PunishmentInfo getCurrentPunishment(boolean uuid, String user, PunishmentType type);
}