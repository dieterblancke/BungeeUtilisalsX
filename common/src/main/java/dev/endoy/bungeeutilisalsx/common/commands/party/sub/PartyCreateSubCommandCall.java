package dev.endoy.bungeeutilisalsx.common.commands.party.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class PartyCreateSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user.isConsole() )
        {
            user.sendLangMessage( "not-for-console" );
            return;
        }
        try
        {
            BuX.getInstance().getPartyManager().createParty( user );

            user.sendLangMessage( "party.create.created" );
        }
        catch ( AlreadyInPartyException e )
        {
            user.sendLangMessage( "party.create.already-in-party" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Creates a new party.";
    }

    @Override
    public String getUsage()
    {
        return "/party create";
    }
}
