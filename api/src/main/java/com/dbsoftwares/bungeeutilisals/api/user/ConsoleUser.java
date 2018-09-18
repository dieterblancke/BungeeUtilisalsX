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
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.Packet;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.ExperimentalUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class ConsoleUser implements User {

    private UserStorage storage = new UserStorage();
    private UserCooldowns cooldowns = new UserCooldowns();

    @Override
    public void load(ProxiedPlayer parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public String getIP() {
        return "127.0.0.1";
    }

    @Override
    public Language getLanguage() {
        return BUCore.getApi().getLanguageManager().getDefaultLanguage();
    }

    @Override
    public void setLanguage(Language language) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                if (prefix) {
                    sendMessage(message);
                } else {
                    sendRawColorMessage(message);
                }
            }
        } else {
            if (prefix) {
                sendMessage(getLanguageConfig().getString(path));
            } else {
                sendRawColorMessage(getLanguageConfig().getString(path));
            }
        }
    }

    @Override
    public void sendLangMessage(boolean prefix, String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                if (prefix) {
                    sendMessage(message);
                } else {
                    sendRawColorMessage(message);
                }
            }
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            if (prefix) {
                sendMessage(message);
            } else {
                sendRawColorMessage(message);
            }
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void langKick(String path, Object... placeholders) {

    }

    @Override
    public void forceKick(String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public UUID getUUID() {
        return UUID.randomUUID();
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isSocialSpy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ProxiedPlayer getParent() {
        return null;
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getConfig(BUCore.getApi().getPlugin(), getLanguage());
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
        return false;
    }

    @Override
    public PunishmentInfo getMuteInfo() {
        return null;
    }

    @Override
    public void setMute(PunishmentInfo info) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setInStaffChat(boolean staffchat) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isInStaffChat() {
        return false;
    }

    @Override
    public Version getVersion() {
        return Version.latest();
    }

    @Override
    public void sendPacket(Packet packet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}