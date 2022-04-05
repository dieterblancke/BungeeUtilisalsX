package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class ToggleBossBarCommandCalll implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {

    }

    @Override
    public String getDescription()
    {
        return "Enables or disables receiving bossbars.";
    }

    @Override
    public String getUsage()
    {
        return "/togglebossbar [on / off]";
    }
}
