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

package com.dbsoftwares.bungeeutilisals.api.chat;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

public interface IChatManager {

    /**
     * Checks the given message for advertisements.
     * @param user The user who sent the message.
     * @param message The message you want to check for Advertisements.
     * @return True if advertisement was found, false if not.
     */
    Boolean checkForAdvertisement(User user, String message);

    /**
     * Checks the given message for caps.
     * @param user The user who sent the message.
     * @param message The message you want to check for caps.
     * @return True if too much caps was detected, false if not.
     */
    Boolean checkForCaps(User user, String message);

    /**
     * Checks the given message for spam.
     * @param user The user who sent the message.
     * @return True if the delay was too short, false if not.
     */
    Boolean checkForSpam(User user);

    /**
     * Checks the given message for swear words.
     * @param user The user who sent the message.
     * @param message The message you want to check for swear words.
     * @return True if swear words were detected, false if not.
     */
    Boolean checkForSwear(User user, String message);

    /**
     * Replaces all swear words found with a given replacement.
     * @param user The user who sent the message.
     * @param message The message you want to replace the swear words in.
     * @param replacement The replacement for swear messages.
     * @return The given message with (optional) swear words replaced by the replacement.
     */
    String replaceSwearWords(User user, String message, String replacement);

    /**
     * Replaces combinations with their corresponding characters.
     * @param message The message that you want to replace in.
     * @return The message with replacements.
     */
    String replaceSymbols(String message);

    /**
     * Replaces A-Z characters in a message with a fancier Unicode font.
     * @param message The message in which you want to replace.
     * @return The message with replacements.
     */
    String fancyFont(String message);
}