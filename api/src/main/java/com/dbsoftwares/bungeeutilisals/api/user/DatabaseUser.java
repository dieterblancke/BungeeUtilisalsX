package com.dbsoftwares.bungeeutilisals.api.user;

import java.util.List;

public interface DatabaseUser {

    /**
     * @param user The User you want to check existance.
     * @return True if found, false if not.
     */
    Boolean exists(String user);

    /**
     * @param IP The IP you want to check.
     * @return A list of all User names playing on the given IP.
     */
    List<String> getPlayersOnIP(String IP);

    /**
     * @param user User you want to retrieve the data from.
     * @return The requested IP. UNKNOWN if not found.
     */
    String getIP(String user);

    /**
     * Allows you to update the User's IP into the database.
     * @param user The user you want to update.
     * @param IP The new IP you want to set.
     */
    void setIP(String user, String IP);

    /**
     * Ignores a player for an User in the database.
     * @param user The user you want to let ignore a player.
     * @param ignoredplayer The player you want to ignore.
     */
    void addIgnoredPlayer(String user, String ignoredplayer);

    /**
     * Unignores a player for an User in the database.
     * @param user The user you want to let unignore a player.
     * @param ignoredplayer The player you want to unignore.
     */
    void removeIgnoredPlayer(String user, String ignoredplayer);
}