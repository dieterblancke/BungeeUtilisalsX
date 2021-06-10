package be.dieterblancke.bungeeutilisalsx.common.commands.general.message;


import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class MsgToggleCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user.isConsole() )
        {
            user.sendLangMessage( "not-for-console" );
            return;
        }

        user.setMsgToggled( !user.isMsgToggled() );
        user.sendLangMessage( "general-commands.msgtoggle." + ( user.isMsgToggled() ? "enabled" : "disabled" ) );
    }
}
