package com.dbsoftwares.bungeeutilisals.api.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.IExperimentalUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import lombok.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Data
public class ConsoleUser implements User {

    private UserStorage storage = new UserStorage();
    private UserCooldowns cooldowns = new UserCooldowns();

    @Override
    public void load(UserPreLoadEvent event) {
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
    public String getIdentifier() {
        return getName();
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
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                sendMessage(message);
            }
        } else {
            sendMessage(getLanguageConfig().getString(path));
        }
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        if (getLanguageConfig().isList(path)) {
            for (String message : getLanguageConfig().getStringList(path)) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
                }

                sendMessage(message);
            }
        } else {
            String message = getLanguageConfig().getString(path);
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
            }

            sendMessage(message);
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
    public void forceKick(String reason) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return "CONSOLE";
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
    public IExperimentalUser experimental() {
        throw new UnsupportedOperationException("Not supported yet.");
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
}