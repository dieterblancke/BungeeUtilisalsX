package be.dieterblancke.bungeeutilisalsx.bungee.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

public class CommandHolder extends net.md_5.bungee.api.plugin.Command implements TabExecutor
{

    private final Command command;

    public CommandHolder( final Command command )
    {
        super( command.getName(), "", command.getAliases() );

        this.command = command;
    }

    @Override
    public void execute( final CommandSender sender, final String[] args )
    {
        this.command.execute( this.getUser( sender ), args );
    }

    @Override
    public Iterable<String> onTabComplete( final CommandSender sender, final String[] args )
    {
        return this.command.onTabComplete( this.getUser( sender ), args );
    }

    private User getUser( final CommandSender sender )
    {
        return sender instanceof ProxiedPlayer
                ? BuX.getApi().getUser( sender.getName() ).orElse( null )
                : BuX.getApi().getConsoleUser();
    }
}
