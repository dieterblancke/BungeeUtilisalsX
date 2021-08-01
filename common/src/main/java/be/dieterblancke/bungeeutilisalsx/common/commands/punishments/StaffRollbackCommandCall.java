package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

import java.util.Date;
import java.util.List;

public class StaffRollbackCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "punishments.staffrollback.usage" );
            return;
        }

        final String playerName = args.get( 0 );
        final Date time = new Date( Utils.parseDateDiffInPast( args.get( 1 ) ) );
        final boolean hardRollback = parameters.contains( "-f" );
        final int amount;

        if ( hardRollback )
        {
            amount = BuX.getApi().getStorageManager().getDao().getPunishmentDao().hardDeleteSince( playerName, time );
        }
        else
        {
            amount = BuX.getApi().getStorageManager().getDao().getPunishmentDao().softDeleteSince( playerName, user.getName(), time );
        }

        user.sendLangMessage(
                "punishments.staffrollback." + ( hardRollback ? "hard" : "soft" ) + "-rollback",
                "{amount}", amount,
                "{user}", playerName
        );
    }
}
