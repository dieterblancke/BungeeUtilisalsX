package be.dieterblancke.bungeeutilisalsx.common.commands.plugin.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class VersionSubCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        user.sendMessage( "&7You are running BungeeUtilisalsX &av" + BuX.getInstance().getVersion() + "&7!" );
    }

    @Override
    public String getDescription()
    {
        return "Prints the current BungeeUtilisalsX version in your chat.";
    }

    @Override
    public String getUsage()
    {
        return "/bungeeutilisals version";
    }
}
