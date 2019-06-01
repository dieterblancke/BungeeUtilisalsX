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

package com.dbsoftwares.bungeeutilisals.api.chat.message;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.LinkedList;

@Data
@ToString
public class ChatMessage {

    private final LinkedList<ChatMessagePartim> partims = Lists.newLinkedList();

    public ChatMessage() {
    }

    public ChatMessage(String message) {
        this.addPartim(message);
    }

    public void addPartim(final String message) {
        this.addPartim(message, null, null);
    }

    public void addPartim(final String message, final HoverPartim hoverPartim) {
        this.addPartim(message, hoverPartim, null);
    }

    public void addPartim(final String message, final ClickPartim clickPartim) {
        this.addPartim(message, null, clickPartim);
    }

    public void addPartim(final String message, final HoverPartim hoverPartim, final ClickPartim clickPartim) {
        this.partims.add(new ChatMessagePartim(message, hoverPartim, clickPartim));
    }

    public void newLine() {
        this.addPartim("\n");
    }

    public void sendTo(User user) {
        final TextComponent component = new TextComponent();

        for (ChatMessagePartim partim : partims) {
            final String message = Utils.c(PlaceHolderAPI.formatMessage(user, partim.getMessage()));
            final HoverPartim hoverPartim = partim.getHoverPartim();
            final ClickPartim clickPartim = partim.getClickPartim();

            final TextComponent extra = new TextComponent(message);
            if (hoverPartim != null) {
                extra.setHoverEvent(new HoverEvent(hoverPartim.getAction(), Utils.format(user, hoverPartim.getText())));
            }
            if (clickPartim != null) {
                extra.setClickEvent(new ClickEvent(clickPartim.getAction(), PlaceHolderAPI.formatMessage(user, clickPartim.getValue())));
            }
            component.addExtra(extra);
        }
        user.sendMessage(component);
    }

    public void sendTo(User[] users) {
        for (User user : users) {
            sendTo(user);
        }
    }

    public void sendTo(Iterable<User> users) {
        users.forEach(this::sendTo);
    }

}
