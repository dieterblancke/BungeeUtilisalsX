package be.dieterblancke.bungeeutilisalsx.common.api.punishments;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PunishmentTrack
{

    private final String identifier;
    private final boolean canRunAgain;
    private final List<PunishmentTrackRecord> records;

    @Data
    @AllArgsConstructor
    public static final class PunishmentTrackRecord
    {

        private final int count;
        private final String action;

    }
}
