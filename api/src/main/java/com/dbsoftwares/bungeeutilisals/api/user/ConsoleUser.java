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

package com.dbsoftwares.bungeeutilisals.api.user;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettings;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;
import java.util.UUID;

public class ConsoleUser implements User {

    private static final String NOT_SUPPORTED = "Not supported yet.";

    private UserStorage storage = new UserStorage();
    private UserCooldowns cooldowns = new UserCooldowns();

    @Getter
    private List<FriendData> friends = Lists.newArrayList();

    @Override
    public void load(ProxiedPlayer parent) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void unload() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public UserStorage getStorage() {
        return storage;
    }

    @Override
    public UserCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }

    @Override
    public Language getLanguage() {
        return BUCore.getApi().getLanguageManager().getDefaultLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public CommandSender sender() {
        return ProxyServer.getInstance().getConsole();
    }

    @Override
    public void sendRawMessage(String message) {
        sendMessage(new TextComponent(message));
    }

    @Override
    public void sendRawColorMessage(String message) {
        sendMessage(Utils.format(message));
    }

    @Override
    public void sendMessage(String message) {
        sendMessage(getLanguageConfig().getString("prefix"), message);
    }

    @Override
    public void sendLangMessage(String path) {
        sendLangMessage(true, path);
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        sendLangMessage(true, path, placeholders);
    }

    @Override
    public void sendLangMessage(boolean prefix, String path) {
        final String message = buildLangMessage(path);

        if (message.isEmpty()) {
            return;
        }

        if (prefix) {
            sendMessage(message);
        } else {
            sendRawColorMessage(message);
        }
    }

    @Override
    public void sendLangMessage(boolean prefix, String path, Object... placeholders) {
        final String message = buildLangMessage(path, placeholders);

        if (message.isEmpty()) {
            return;
        }

        if (prefix) {
            sendMessage(message);
        } else {
            sendRawColorMessage(message);
        }
    }

    @Override
    public void sendMessage(String prefix, String message) {
        sendMessage(Utils.format(prefix + message));
    }

    @Override
    public void sendMessage(BaseComponent component) {
        sender().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        sender().sendMessage(components);
    }

    @Override
    public void kick(String reason) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void langKick(String path, Object... placeholders) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void forceKick(String reason) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public UUID getUuid() {
        return UUID.randomUUID();
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Boolean isSocialSpy() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public ProxiedPlayer getParent() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getConfig(BUCore.getApi().getPlugin().getDescription().getName(), getLanguage());
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public String getServerName() {
        return "BUNGEE";
    }

    @Override
    public boolean isMuted() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public PunishmentInfo getMuteInfo() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void setMute(PunishmentInfo info) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public boolean isInStaffChat() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void setInStaffChat(boolean staffchat) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public Version getVersion() {
        return Version.latest();
    }

    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public void setLocation(Location location) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    @Override
    public String buildLangMessage(final String path, final Object... placeholders) {
        if (!getLanguageConfig().exists(path)) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        if (getLanguageConfig().isList(path)) {
            final List<String> messages = getLanguageConfig().getStringList(path);

            if (messages.isEmpty()) {
                return "";
            }

            for (int i = 0; i < messages.size(); i++) {
                final String message = replacePlaceHolders(messages.get(i), placeholders);
                builder.append(message);

                if (i < messages.size() - 1) {
                    builder.append("\n");
                }
            }
        } else {
            final String message = replacePlaceHolders(getLanguageConfig().getString(path), placeholders);

            if (message.isEmpty()) {
                return "";
            }

            builder.append(message);
        }
        return builder.toString();
    }

    @Override
    public FriendSettings getFriendSettings() {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }

    private String replacePlaceHolders(String message, Object... placeholders) {
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
        }
        message = PlaceHolderAPI.formatMessage(this, message);
        return message;
    }

    @Override
    public void sendPacket(DefinedPacket packet) {
        throw new UnsupportedOperationException(NOT_SUPPORTED);
    }
}