package be.dieterblancke.bungeeutilisalsx.common.commands.general.spy;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class CommandSpyCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( !user.isCommandSpy() )
        {
            user.sendLangMessage( "general-commands.commandspy.enabled" );
            user.setCommandSpy( true );
        }
        else
        {
            user.sendLangMessage( "general-commands.commandspy.disabled" );
            user.setCommandSpy( false );
        }
    }

    @Override
    public String getDescription()
    {
        return "Toggles command spy for the current session. This will allow you to see all commands that are being executed.";
    }

    @Override
    public String getUsage()
    {
        return "/commandspy";
    }
}
