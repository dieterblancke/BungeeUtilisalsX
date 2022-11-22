package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class KickCommandCall extends PunishmentCommand
{

    public KickCommandCall()
    {
        super( "punishments.kick", false );
    }

    @Override
    public void onPunishmentExecute( final User user,
                                     final List<String> args,
                                     final List<String> parameters,
                                     final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();

        if ( !BuX.getApi().getPlayerUtils().isOnline( punishmentArgs.getPlayer() ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.KICK ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getKickAndWarnDao().insertKick(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                user.getName()
        ).thenAccept( info ->
        {

            // Attempting to kick if player is online. If briding is enabled and player is not online, it will attempt to kick on other bungee's.
            super.attemptKick( storage, "punishments.kick.onkick", info );
            user.sendLangMessage( "punishments.kick.executed", executor.getPlaceHolders( info ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                            "punishments.kick.broadcast",
                            executor.getPlaceHolders( info )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                            "punishments.kick.broadcast",
                            ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.kick.broadcast" ),
                            executor.getPlaceHolders( info )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.KICK );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Kicks a user for a given reason.";
    }

    @Override
    public String getUsage()
    {
        return "/kick (user) <server> (reason)";
    }
}