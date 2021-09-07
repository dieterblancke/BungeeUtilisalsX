package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class UserMuteJob implements MultiProxyJob
{

    private final UUID uuid;
    private final String userName;
    private final boolean ip;
    private final String ipAddress;
    private final String languagePath;
    private final Object[] placeholders;
    private final PunishmentType punishmentType;
    private final String reason;

    @Override
    public boolean isAsync()
    {
        return true;
    }

    public List<User> getUsers()
    {
        return BuX.getApi().getUsers().stream()
                .filter( user -> this.isIp()
                        ? user.getIp().equalsIgnoreCase( this.ipAddress )
                        : user.getUuid().equals( this.getUuid() ) || user.getName().equalsIgnoreCase( this.getUserName() ) )
                .collect( Collectors.toList() );

    }
}
