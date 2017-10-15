package com.dbsoftwares.bungeeutilisals.bungee.user;

/*
 * Created by DBSoftwares on 04 september 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.universal.DBUser;
import com.dbsoftwares.bungeeutilisals.universal.utilities.Utilities;
import com.dbsoftwares.bungeeutilisals.universal.enums.UserType;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;

@Data
public class BUser implements DBUser {

    UserType type;
    String name;
    ProxiedPlayer player;

    public BUser(UserType type, String name) {
        this.type = type;
        this.name = name;

        player = ProxyServer.getInstance().getPlayer(name);
    }

    public void sendMessage(String message) {
        TextComponent component = new TextComponent(Utilities.c(BungeeUtilisals.getInstance().getConfig().prefix));

        component.setExtra(Arrays.asList(TextComponent.fromLegacyText(Utilities.c(message))));

        componentedMessage(component);
    }

    public void componentedMessage(BaseComponent component) {
        (isConsole() ? ProxyServer.getInstance().getConsole() : player).sendMessage(component);
    }

    @Override
    public Boolean isUser() {
        return type.equals(UserType.USER);
    }

    @Override
    public Boolean isConsole() {
        return type.equals(UserType.CONSOLE);
    }

    @Override
    public Boolean hasPermission(String permission) {
        return type == UserType.CONSOLE || player.hasPermission(permission);
    }
}
