package be.dieterblancke.bungeeutilisalsx.common.protocolize;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.ISection;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.player.ProtocolizePlayer;
import dev.simplix.protocolize.data.Sound;

public class SimpleProtocolizeManager implements ProtocolizeManager
{

    @Override
    public void sendSound( final User user, final SoundData soundData )
    {
        if ( user == null || soundData == null )
        {
            return;
        }

        final ProtocolizePlayer protocolizePlayer = this.getProtocolizePlayer( user );
        final Sound sound = Sound.valueOf( soundData.sound() );
        final SoundCategory category = SoundCategory.valueOf( soundData.category() );

        protocolizePlayer.playSound( sound, category, soundData.volume(), soundData.pitch() );
    }

    private ProtocolizePlayer getProtocolizePlayer( final User user )
    {
        return Protocolize.playerProvider().player( user.getUuid() );
    }
}
