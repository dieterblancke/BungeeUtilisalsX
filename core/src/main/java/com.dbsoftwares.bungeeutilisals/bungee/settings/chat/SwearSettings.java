package com.dbsoftwares.bungeeutilisals.bungee.settings.chat;

import com.dbsoftwares.bungeeutilisals.api.settings.SettingStorage;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingType;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
public class SwearSettings extends SettingStorage {

    Boolean enabled, cancel;
    String replacement;
    List<String> words;
    List<Pattern> patterns;

    public SwearSettings() {
        super(BungeeUtilisals.getInstance(), SettingType.ANTISWEAR);
    }

    @Override
    public void reload() {
        super.reloadConfig();

        load();
    }

    @Override
    public void load() {
        patterns = Lists.newArrayList();
        Configuration section = getConfig().getSection("symbols");

        enabled = getConfig().getBoolean("enabled");
        cancel = getConfig().getBoolean("cancel");
        replacement = getConfig().getString("replace");
        words = getConfig().getStringList("words");

        for (String word : words) {
            StringBuilder builder = new StringBuilder("\\b(");

            for (char o : word.toCharArray()) {
                builder.append(o);
                builder.append("+(\\W|\\d\\_)*");
            }
            builder.append(")\\b");

            patterns.add(Pattern.compile(builder.toString()));
        }
    }
}