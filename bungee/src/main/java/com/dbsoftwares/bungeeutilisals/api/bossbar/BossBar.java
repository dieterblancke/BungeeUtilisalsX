package com.dbsoftwares.bungeeutilisals.api.bossbar;

/*
 * Created by DBSoftwares on 15/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayOutBossBar;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.utils.Version;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class BossBar implements IBossBar {

    private UUID uuid;
    private BarColor color;
    private BarStyle style;
    private float progress;
    private BaseComponent[] message;
    private boolean visible;

    private UserCollection users;
    private Set<EventHandler<UserUnloadEvent>> eventHandlers; // Should only contain ONE EventHandler

    public BossBar() {
        this(UUID.randomUUID(), BarColor.PINK, BarStyle.SOLID, 1F, new ComponentBuilder("").create());
    }

    @Deprecated
    public BossBar(BarColor color, BarStyle style, float progress, String message) {
        this(UUID.randomUUID(), color, style, progress, message);
    }

    @Deprecated
    public BossBar(UUID uuid, BarColor color, BarStyle style, float progress, String message) {
        this(uuid, color, style, progress, TextComponent.fromLegacyText(message));
    }

    public BossBar(BarColor color, BarStyle style, float progress, BaseComponent[] message) {
        this(UUID.randomUUID(), color, style, progress, message);
    }

    public BossBar(UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message) {
        this.uuid = uuid;
        this.color = color;
        this.style = style;
        this.progress = progress;
        this.message = message;
        this.visible = true;
        this.users = BUCore.getApi().newUserCollection();
        this.eventHandlers = BUCore.getApi().getEventLoader().register(UserUnloadEvent.class, new BossBarListener());
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;

        PacketPlayOutBossBar packet;

        if (visible) {
            packet = new PacketPlayOutBossBar(uuid, BossBarAction.ADD, message, progress, color, style);
        } else {
            packet = new PacketPlayOutBossBar(uuid, BossBarAction.REMOVE);
        }

        users.forEach(user -> user.experimental().sendPacket(packet));
    }

    @Override
    public void setColor(BarColor color) {
        this.color = color;

        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_STYLE, color, style)));
        }
    }

    @Override
    public void setStyle(BarStyle style) {
        this.style = style;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_STYLE, color, style)));
        }
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_HEALTH, progress)));
        }
    }

    @Override
    @Deprecated
    public void setMessage(String message) {
        this.setMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void setMessage(BaseComponent[] message) {
        this.message = message;
        if (visible) {
            users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.UPDATE_TITLE, message)));
        }
    }

    @Override
    public void addUser(User user) {
        if (!users.contains(user)) {
            if (user.getVersion() == null || user.getVersion().getVersion() < Version.MINECRAFT_1_9.getVersion()) {
                BUCore.log(Level.INFO, "Could not add " + user.getName() + " to the bossbar. Reason: Unsupported Version.");
                return;
            }
            users.add(user);

            user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.ADD, message, progress, color, style));
        }
    }

    @Override
    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);

            user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.REMOVE));
        }
    }

    @Override
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    @Override
    public void clearUsers() {
        users.forEach(user -> user.experimental().sendPacket(new PacketPlayOutBossBar(uuid, BossBarAction.REMOVE)));
        users.clear();
    }

    @Override
    public void unregister() {
        eventHandlers.forEach(EventHandler::unregister);
    }

    private class BossBarListener implements EventExecutor {

        @Event
        public void onUnload(UserUnloadEvent event) {
            removeUser(event.getUser());
        }

    }
}