package be.dieterblancke.bungeeutilisalsx.common.protocolize;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

public class NoOperationProtocolizeManager implements ProtocolizeManager
{

    @Override
    public void sendSound( final User user, final SoundData soundData )
    {
        // do nothing
    }
}
