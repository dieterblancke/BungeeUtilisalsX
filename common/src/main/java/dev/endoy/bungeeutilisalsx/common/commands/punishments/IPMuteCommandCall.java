package dev.endoy.bungeeutilisalsx.common.commands.punishments;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorage;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class IPMuteCommandCall extends PunishmentCommand
{

    public IPMuteCommandCall()
    {
        super( "punishments.ipmute", false );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();

        if ( dao().getPunishmentDao().getMutesDao().isIPMuted( storage.getIp(), punishmentArgs.getServerOrAll() ).join() )
        {
            user.sendLangMessage( "punishments.ipmute.already-muted" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.IPMUTE ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getMutesDao().insertIPMute(
            storage.getUuid(),
            storage.getUserName(),
            storage.getIp(),
            reason,
            punishmentArgs.getServerOrAll(),
            true,
            user.getName()
        ).thenAccept( info ->
        {
            super.attemptMute( storage, "punishments.ipmute.onmute", info );
            user.sendLangMessage( "punishments.ipmute.executed", executor.getPlaceHolders( info ) );

            if ( !parameters.contains( "-s" ) )
            {
                if ( parameters.contains( "-nbp" ) )
                {
                    BuX.getApi().langBroadcast(
                        "punishments.ipmute.broadcast",
                        executor.getPlaceHolders( info )
                    );
                }
                else
                {
                    BuX.getApi().langPermissionBroadcast(
                        "punishments.ipmute.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.ipmute.broadcast" ),
                        executor.getPlaceHolders( info )
                    );
                }
            }

            punishmentArgs.launchPunishmentFinishEvent( PunishmentType.IPMUTE );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Permanently ip mutes a given user globally (or given server if per-server punishments are enabled).";
    }

    @Override
    public String getUsage()
    {
        return "/ipmute (user / ip) <server> (reason)";
    }
}