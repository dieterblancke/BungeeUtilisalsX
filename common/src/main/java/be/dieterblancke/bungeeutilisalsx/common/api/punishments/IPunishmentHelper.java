package be.dieterblancke.bungeeutilisalsx.common.api.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import be.dieterblancke.configuration.api.IConfiguration;

import java.util.List;
import java.util.UUID;

public interface IPunishmentHelper
{

    boolean isTemplateReason( final String reason );

    List<String> searchTemplate( final IConfiguration config, final PunishmentType type, String template );

    String getDateFormat();

    String getTimeLeftFormat();

    String setPlaceHolders( String line, PunishmentInfo info );

    MessagePlaceholders getPlaceHolders( PunishmentInfo info );

    default boolean isHigherPunishment( UUID executor, UUID target )
    {
        return BuX.getInstance().getActivePermissionIntegration().hasLowerOrEqualGroup( executor, target )
                && !ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "allow-higher-punishments" );
    }
}