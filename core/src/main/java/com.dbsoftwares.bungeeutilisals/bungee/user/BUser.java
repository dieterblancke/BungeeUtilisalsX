package com.dbsoftwares.bungeeutilisals.bungee.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.punishmentts.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;

public class BUser implements User {

    String player;
    Boolean socialspy = false;

    @Override
    public void load(UserPreLoadEvent event) {
        this.player = event.getPlayer().getName();

        UserLoadEvent userLoadEvent = new UserLoadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEvent(userLoadEvent);
    }

    @Override
    public void unload() {
        UserUnloadEvent event = new UserUnloadEvent(this);
        BungeeUtilisals.getApi().getEventLoader().launchEventAsync(event);


    }

    @Override
    public void save() {

    }

    @Override
    public UserStorage getStorage() {
        return null;
    }

    @Override
    public Integer getPing() {
        return null;
    }

    @Override
    public String getIP() {
        return null;
    }

    @Override
    public Language getLanguage() {
        return null;
    }

    @Override
    public void setLanguage(Language language) {

    }

    @Override
    public Boolean isMuted() {
        return null;
    }

    @Override
    public void setCurrentMute(PunishmentInfo info) {

    }

    @Override
    public PunishmentInfo getCurrentMute() {
        return null;
    }

    @Override
    public CommandSender sender() {
        return null;
    }

    @Override
    public void sendRawMessage(String message) {

    }

    @Override
    public void sendRawColorMessage(String message) {

    }

    @Override
    public void sendMessage(String message) {
        TextComponent component = new TextComponent(/*BungeeUtilisals.getApi().getPrefix() TODO: get language & language prefix */);

        component.setExtra(Arrays.asList(Utils.format(message)));

        sendMessage(component);
    }

    @Override
    public void sendMessage(String prefix, String message) {

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
        getParent().disconnect(buildComponent(reason));
    }

    @Override
    public void forceKick(String reason) {

    }

    @Override
    public String getName() {
        return player;
    }

    @Override
    public void sendNoPermMessage() {

    }

    @Override
    public Boolean isStaff() {
        return null;
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
    public Configuration getLanguageConfig() {
        return null;
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