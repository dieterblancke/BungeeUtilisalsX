package dev.endoy.bungeeutilisalsx.common.commands.general.message;


import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

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

    @Override
    public String getDescription()
    {
        return "Allows you to toggle private messages for the current session (will re-enable on rejoin).";
    }

    @Override
    public String getUsage()
    {
        return "/msgtoggle";
    }
}
