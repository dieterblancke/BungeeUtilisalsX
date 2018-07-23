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

/*
 * Created by DBSoftwares on 14 juli 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
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
                    Utils.format(user, hover)
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