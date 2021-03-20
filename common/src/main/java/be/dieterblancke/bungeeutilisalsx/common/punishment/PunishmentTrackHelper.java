package be.dieterblancke.bungeeutilisalsx.common.punishment;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentTrackHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrack;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.UUID;

public class PunishmentTrackHelper implements IPunishmentTrackHelper
{

    @Override
    public void addTrackPunishment( final UUID uuid, final PunishmentInfo punishmentInfo )
    {
        for ( PunishmentTrack punishmentTrack : ConfigFiles.PUNISHMENT_TRACKS.getPunishmentTracks() )
        {
            if ( punishmentTrack.getCountNormalPunishments().isEnabled()
                    && punishmentTrack.getCountNormalPunishments().getTypes().contains( punishmentInfo.getType() ) )
            {
                // TODO
            }
        }
    }
}
