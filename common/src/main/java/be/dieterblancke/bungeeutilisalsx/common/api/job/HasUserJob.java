package be.dieterblancke.bungeeutilisalsx.common.api.job;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class HasUserJob implements MultiProxyJob
{

    private final UUID uuid;
    private final String userName;

    public Optional<User> getUser()
    {
        return BuX.getApi().getUser( uuid );
    }
}
