package be.dieterblancke.bungeeutilisalsx.common.permission;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PermissionIntegration
{

    boolean isActive();

    CompletableFuture<String> getGroup( UUID user );

    String getPrefix( UUID uuid );

    String getSuffix( UUID uuid );

    default boolean hasLowerOrEqualGroup( final UUID userUuid, final UUID otherUuid )
    {
        final String userGroup = this.getGroup( userUuid ).join();
        final String otherGroup = this.getGroup( otherUuid ).join();

        if ( userGroup.isEmpty() || otherGroup.isEmpty() )
        {
            return false;
        }

        final StaffRankData userRankData = ConfigFiles.RANKS.getRankData( userGroup );
        final StaffRankData otherRankData = ConfigFiles.RANKS.getRankData( otherGroup );

        if ( userRankData == null || otherRankData == null )
        {
            return false;
        }
        return userRankData.getPriority() <= otherRankData.getPriority();
    }
}
