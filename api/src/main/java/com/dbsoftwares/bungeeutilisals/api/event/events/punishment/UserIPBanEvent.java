package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * This event will be executed upon User IPBan.
 */
public class UserIPBanEvent extends AbstractEvent {

    @Getter @Setter String userName;
    @Getter @Setter String bannerName;
    @Getter @Setter PunishmentInfo info;

    public UserIPBanEvent(String user, String banner, PunishmentInfo info) {
        this.userName = user;
        this.bannerName = banner;
        this.info = info;
    }

    public Optional<User> getUser() {
        return getApi().getUser(userName);
    }

    public Optional<User> getBanner() {
        return getApi().getUser(bannerName);
    }
}
