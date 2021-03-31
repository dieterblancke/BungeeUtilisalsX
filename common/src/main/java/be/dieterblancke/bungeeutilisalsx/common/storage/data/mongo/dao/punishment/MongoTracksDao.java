package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao.punishment;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao;

import java.util.List;
import java.util.UUID;

public class MongoTracksDao implements TracksDao
{

    @Override
    public List<PunishmentTrackInfo> getTrackInfos( final UUID uuid, final String trackId, final String server )
    {
        return null;
    }

    @Override
    public void addToTrack( final PunishmentTrackInfo trackInfo )
    {
        // TODO: add to track
    }

    @Override
    public void resetTrack( final UUID uuid, final String trackId, final String server )
    {
        // TODO: set the active status of all track records for this user with this trackId and server to false
    }
}
