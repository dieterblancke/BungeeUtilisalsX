package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

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
        final Long time = Utils.parseDateDiffInPast( args.get( 1 ) );

        if ( parameters.contains( "-f" ) )
        {
//            BuX.getApi().getStorageManager().getDao().getPunishmentDao().softDeleteSince( user, time );
        }
        else
        {
//            BuX.getApi().getStorageManager().getDao().getPunishmentDao().hardDeleteSince( user, time );
        }
    }
}
