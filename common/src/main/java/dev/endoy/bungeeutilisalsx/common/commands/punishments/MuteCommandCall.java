package dev.endoy.bungeeutilisalsx.common.commands.punishments;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class MuteCommandCall extends PunishmentCommand
{

    public MuteCommandCall()
    {
        super( "punishments.mute", false );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        if ( dao().getPunishmentDao().getMutesDao().isMuted( storage.getUuid(), punishmentArgs.getServerOrAll() ).join() )
        {
            user.sendLangMessage( "punishments.mute.already-muted" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.MUTE ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getMutesDao().insertMute(
            storage.getUuid(),
            storage.getUserName(),
            storage.getIp(),
            reason,
            punishmentArgs.getServerOrAll(),
            true,
            user.getName()
        ).thenAccept( info ->
        {
            super.attemptMute( storage, "punishments.mute.onmute", info );
            user.sendLangMessage( "punishments.mute.executed", executor.getPlaceHolders( info ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                        "punishments.mute.broadcast",
                        executor.getPlaceHolders( info )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                        "punishments.mute.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.mute.broadcast" ),
                        executor.getPlaceHolders( info )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.MUTE );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Permanently bans a given user globally (or given server if per-server punishments are enabled).";
    }

    @Override
    public String getUsage()
    {
        return "/mute (user) <server> (reason)";
    }
}