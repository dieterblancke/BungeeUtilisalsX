package be.dieterblancke.bungeeutilisalsx.common.protocolize;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;

public class ProtocolizeManager
{

    public ProtocolizePlayer getProtocolizePlayer( final User user )
    {
        return Protocolize.playerProvider().player( user.getUuid() );
    }

    public void sendSound( final User user )
    {
        final ProtocolizePlayer protocolizePlayer = this.getProtocolizePlayer( user );

        // TODO: play sound
    }
}
