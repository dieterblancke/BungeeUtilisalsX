package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;

import java.util.List;

public class FindCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.find.usage" );
            return;
        }

        if ( StaffUtils.isHidden( args.get( 0 ) ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        final IProxyServer server = BuX.getApi().getPlayerUtils().findPlayer( args.get( 0 ) );

        if ( server == null )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        user.sendLangMessage( "general-commands.find.message", "{user}", args.get( 0 ), "{server}", server.getName() );
    }

    @Override
    public String getDescription()
    {
        return "Finds the server the given user currently is in.";
    }

    @Override
    public String getUsage()
    {
        return "/find (user)";
    }
}
