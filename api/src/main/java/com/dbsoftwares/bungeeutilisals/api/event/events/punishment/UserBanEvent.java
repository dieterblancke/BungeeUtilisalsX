package com.dbsoftwares.bungeeutilisals.api.event.events.punishment;

import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * This event will be executed upon User Ban.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserBanEvent extends AbstractEvent {

    String name;
    String uuid;
    String bannerName;
    PunishmentInfo info;

    public Optional<User> getBanner() {
        return getApi().getUser(bannerName);
    }
}
