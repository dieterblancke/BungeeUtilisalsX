package com.dbsoftwares.bungeeutilisals.bungee.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.mysql.MySQL;
import com.dbsoftwares.bungeeutilisals.api.user.IExperimentalUser;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCooldowns;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.tables.UserTable;
import lombok.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

@Data
public class BUser implements User {

    private String player;
    private Boolean socialspy = false;
    private ExperimentalUser experimental;
    private UserTable userData;
    private UserCooldowns cooldowns;

    @Override
    public void load(UserPreLoadEvent event) {
        ProxiedPlayer p = event.getPlayer();

        this.player = p.getName();
        this.experimental = new ExperimentalUser(this);
        this.cooldowns = new UserCooldowns();

        BUCore.getApi().getDebugger().debug("Searching user data for %s", player);
        UserTable data;
        if (BungeeUtilisals.getInstance().useUUID()) {
            data = MySQL.search(UserTable.class).select("*").where("uuid = %s", p.getUniqueId().toString()).search().get();
        } else {
            data = MySQL.search(UserTable.class).select("*").where("username = %s", player).get();
        }
        if (data == null) {
            BUCore.getApi().getDebugger().debug("%s not found, creating ...", player);
            data = MySQL.insert(UserTable.getDefault(p));
        }
        this.userData = data;

        if (!userData.getUsername().equalsIgnoreCase(player)) { // Stored name != user current name | Name changed?
            userData.setUsername(player);
            save();
        }

        UserLoadEvent userLoadEvent = new UserLoadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userLoadEvent);
    }

    @Override
    public void unload() {
        save();
        cooldowns.remove();

        UserUnloadEvent event = new UserUnloadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEventAsync(event);
    }

    @Override
    public void save() {
        BUCore.getApi().getDebugger().debug("Saving data for %s!", player);

        if (BungeeUtilisals.getInstance().useUUID()) {
            MySQL.update(userData, "uuid = %s", getParent().getUniqueId().toString());
        } else {
            MySQL.update(userData, "username = %s", player);
        }
    }

    @Override
    public String getIdentifier() {
        return BungeeUtilisals.getInstance().useUUID() ? getParent().getUniqueId().toString() : player;
    }

    @Override
    public UserCooldowns getCooldowns() {
        return cooldowns;
    }

    @Override
    public String getIP() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return BUCore.getApi().getLanguageManager().getLanguage(userData.getLanguage())
                .orElse(BUCore.getApi().getLanguageManager().getDefaultLanguage().orElse(null));
    }

    @Override
    public void setLanguage(Language language) {
        userData.setLanguage(language.getName());
    }

    @Override
    public CommandSender sender() {
        return getParent();
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
        sendMessage(getLanguageConfig().getString(path));
    }

    @Override
    public void sendLangMessage(String path, Object... placeholders) {
        String message = getLanguageConfig().getString(path);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace(placeholders[i].toString(), placeholders[i + 1].toString());
        }

        sendMessage(message);
    }

    @Override
    public void sendMessage(String prefix, String message) {
        TextComponent component = new TextComponent(Utils.format(prefix));

        component.setExtra(Arrays.asList(Utils.format(message)));

        sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent component) {
        getParent().sendMessage(component);
    }

    @Override
    public void sendMessage(BaseComponent[] components) {
        getParent().sendMessage(components);
    }

    @Override
    public void kick(String reason) {
        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> getParent().disconnect(buildComponent(reason)));
    }

    @Override
    public void forceKick(String reason) {
        getParent().disconnect(buildComponent(reason));
    }

    @Override
    public String getName() {
        return player;
    }

    @Override
    public void sendNoPermMessage() {
        sendLangMessage("no-permission");
    }

    @Override
    public void setSocialspy(Boolean socialspy) {
        this.socialspy = socialspy;
    }

    @Override
    public Boolean isSocialSpy() {
        return socialspy;
    }

    @Override
    public ProxiedPlayer getParent() {
        return ProxyServer.getInstance().getPlayer(player);
    }

    @Override
    public IConfiguration getLanguageConfig() {
        return BUCore.getApi().getLanguageManager().getLanguageConfiguration(BUCore.getApi().getPlugin(), this);
    }

    @Override
    public IExperimentalUser experimental() {
        return experimental;
    }

    private BaseComponent[] buildComponent(String... text) {
        ComponentBuilder builder = new ComponentBuilder("");
        if (text == null || text.length == 0) {
            return builder.create();
        }
        for (String aText : text) {
            builder.append(Utils.format(aText));
        }
        return builder.create();
    }
}