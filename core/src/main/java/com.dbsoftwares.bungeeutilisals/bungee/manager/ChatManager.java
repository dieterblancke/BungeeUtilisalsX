package com.dbsoftwares.bungeeutilisals.bungee.manager;

import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.permissions.Permissions;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingManager;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.unicode.UnicodeTranslator;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.SwearSettings;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.UTFSettings;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatManager implements IChatManager {

    static Pattern ippattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");
    static Pattern webpattern = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
    List<Pattern> swearPatterns = Lists.newArrayList();

    public ChatManager(BungeeUtilisals plugin) {
        SettingManager.getSettings(SettingType.ANTISWEAR, SwearSettings.class).getSwearWords().forEach(word -> {
            StringBuilder builder = new StringBuilder("\\b(");

            for (char o : word.toCharArray()) {
                builder.append(o);
                builder.append("+(\\W|\\d\\_)*");
            }
            builder.append(")\\b");

            swearPatterns.add(Pattern.compile(builder.toString()));
        });
    }

    @Override
    public Boolean checkForAdvertisement(User user, String message) {
        if (user.getParent().hasPermission(Permissions.AD_BYPASS)) {
            return false;
        }
        message.replaceAll("[^A-Za-z0-9:/]", "");
        String[] words = message.split(" ");
        if (words.length == 1) {
            if (words[0].toLowerCase().startsWith("http") || words[0].toLowerCase().startsWith("https")
                    || (ippattern.matcher(words[0]).find() || webpattern.matcher(words[0]).find())) {
                return true;
            }
        }
        for (String word : words) {
            if (word.toLowerCase().startsWith("http") || word.toLowerCase().startsWith("https")
                    || ippattern.matcher(word).find() || webpattern.matcher(word).find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean checkForCaps(User user, String message) {
        if (user.getParent().hasPermission(Permissions.CAPS_BYPASS) || message.length() < 4) {
            return false;
        }

        Double upperCase = 0.0D;
        for (int i = 0; i < message.length(); i++) {
            if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(message.substring(i, i + 1))) {
                upperCase += 1.0D;
            }
        }

        return (upperCase / message.length()) > 0.-40;
    }

    @Override
    public Boolean checkForSpam(User user, String message) {
        if (user.getParent().hasPermission(Permissions.SPAM_BYPASS)) {
            return false;
        }
        if (!user.getStorage().canUse("CHATSPAM")) {
            return true;
        }
        user.getStorage().updateTime("CHATSPAM", TimeUnit.SECONDS, 3);
        return false;
    }

    @Override
    public Boolean checkForSwear(User user, String message) {
        if (user.getParent().hasPermission(Permissions.SWEAR_BYPASS)) {
            return false;
        }

        for (Pattern pattern : swearPatterns) {
            if (pattern.matcher(message).find()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String replaceSymbols(String message) {
        for (Map.Entry<String, String> entry : SettingManager.getSettings(SettingType.UTFSYMBOLS, UTFSettings.class).getSymbols().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value.contains(", ")) {
                for (String val : value.split(", ")) {
                    message = message.replace(val, UnicodeTranslator.translate(key));
                }
            } else {
                message = message.replace(value, UnicodeTranslator.translate(key));
            }
        }
        return message;
    }

    @Override
    public String fancyFont(String message) {
        // TODO: make this thing work ...
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char replaceableChar : message.toCharArray()) {
            int i = 0;
            Boolean charFound = false;
            while (!charFound && i < chars.length) {
                if (chars[i] == replaceableChar) {
                    charFound = true;
                }
            }
            if (charFound) {
                builder.append((char) (65248 + replaceableChar));
            } else {
                builder.append(replaceableChar);
            }
        }

        return builder.toString();
    }
}