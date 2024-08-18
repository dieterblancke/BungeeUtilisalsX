package dev.endoy.bungeeutilisalsx.common.commands.punishments;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class IPTempMuteCommandCall extends PunishmentCommand
{

    public IPTempMuteCommandCall()
    {
        super( "punishments.iptempmute", true );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        final long time = punishmentArgs.getTime();

        if ( time == 0L )
        {
            user.sendLangMessage( "punishments.iptempmute.non-valid" );
            return;
        }
        if ( dao().getPunishmentDao().getMutesDao().isIPMuted( storage.getIp(), punishmentArgs.getServerOrAll() ).join() )
        {
            user.sendLangMessage( "punishments.iptempmute.already-muted" );
            return;
        }
        if ( punishmentArgs.launchEvent( PunishmentType.IPTEMPMUTE ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getMutesDao().insertTempIPMute(
            storage.getUuid(),
            storage.getUserName(),
            storage.getIp(),
            reason,
            punishmentArgs.getServerOrAll(),
            true,
            user.getName(),
            time
        ).thenAccept( info ->
        {
            super.attemptMute( storage, "punishments.iptempmute.onmute", info );
            user.sendLangMessage( "punishments.iptempmute.executed", executor.getPlaceHolders( info ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                        "punishments.iptempmute.broadcast",
                        executor.getPlaceHolders( info )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                        "punishments.iptempmute.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.iptempmute.broadcast" ),
                        executor.getPlaceHolders( info )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.IPTEMPMUTE );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Temporarily ipmutes a given user globally (or given server if per-server punishments are enabled).";
    }

    @Override
    public String getUsage()
    {
        return "/iptempmute (user / ip) (time) <server> (reason)";
    }
}