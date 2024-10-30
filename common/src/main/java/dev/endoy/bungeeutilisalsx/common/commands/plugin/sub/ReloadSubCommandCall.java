package dev.endoy.bungeeutilisalsx.common.commands.plugin.sub;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCompleter;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class ReloadSubCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        BuX.getInstance().reload();
        user.sendMessage( "&7All configuration files have been &areloaded&7!" );
    }

    @Override
    public String getDescription()
    {
        return "Reloads all configuration files and most systems of BungeeUtilisalsX.";
    }

    @Override
    public String getUsage()
    {
        return "/bungeeutilisals reload";
    }
}
