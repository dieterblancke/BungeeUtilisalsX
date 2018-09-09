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

package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class MessageBuilder {

    public static TextComponent buildMessage(User user, ISection section, Object... placeholders) {
        if (section.isList("text")) {
            TextComponent component = new TextComponent();

            section.getSectionList("text").forEach(text -> component.addExtra(buildMessage(user, text)));
            return component;
        }
        String text = searchAndFormat(user.getLanguageConfig(), section.getString("text"), placeholders);
        TextComponent component = new TextComponent(Utils.format(user, text));

        if (section.exists("hover")) {
            String hover = section.isList("hover")
                    ? Utils.formatList(section.getStringList("hover"), "\n")
                    : searchAndFormat(user.getLanguageConfig(), section.getString("hover"), placeholders);

            component.setHoverEvent(new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    Utils.format(user, format(hover, placeholders))
            ));
        }
        if (section.exists("click")) {
            component.setClickEvent(new ClickEvent(
                    ClickEvent.Action.valueOf(section.getString("click.type")),
                    PlaceHolderAPI.formatMessage(user, Utils.c(format(section.getString("click.action"), placeholders)))
            ));
        }

        return component;
    }

    public static List<TextComponent> buildMessage(User user, List<ISection> sections, Object... placeholders) {
        List<TextComponent> components = Lists.newArrayList();

        sections.forEach(section -> components.add(buildMessage(user, section, placeholders)));
        return components;
    }

    private static String searchAndFormat(IConfiguration config, String str, Object... placeholders) {
        String text = str;

        if (config.exists(str) && config.isString(str)) {
            if (config.isString(str)) {
                text = config.getString(str);
            } else if (config.isList(str)) {
                text = Utils.formatList(config.getStringList(str), "\n");
            }
        }
        return format(text, placeholders);
    }

    private static String format(String str, Object... placeholders) {
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            str = str.replace(placeholders[i].toString(), placeholders[i + 1].toString());
        }
        return str;
    }
}