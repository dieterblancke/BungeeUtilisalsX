package com.dbsoftwares.bungeeutilisals.commands.general.message;

import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.user.BUser;

import java.util.List;

public class MsgToggleCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User cUser, final List<String> args, final List<String> parameters )
    {
        if ( !( cUser instanceof ConsoleUser ) )
        {
            cUser.sendLangMessage( "not-for-console" );
            return;
        }
        final BUser user = (BUser) cUser;

        user.setMsgToggled( !user.isMsgToggled() );
        user.sendLangMessage( "general-commands.msgtoggle." + (user.isMsgToggled() ? "enabled" : "disabled") );
    }
}
