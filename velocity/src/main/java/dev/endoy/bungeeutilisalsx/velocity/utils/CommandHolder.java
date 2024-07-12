package dev.endoy.bungeeutilisalsx.velocity.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.Command;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.List;

public class CommandHolder implements SimpleCommand
{

    private final Command command;

    public CommandHolder( final Command command )
    {
        this.command = command;
    }

    private User getUser( final CommandSource sender )
    {
        return sender instanceof Player
                ? BuX.getApi().getUser( ( (Player) sender ).getUsername() ).orElse( null )
                : BuX.getApi().getConsoleUser();
    }

    @Override
    public void execute( Invocation invocation )
    {
        this.command.execute( this.getUser( invocation.source() ), invocation.arguments() );
    }

    @Override
    public List<String> suggest( Invocation invocation )
    {
        return this.command.onTabComplete( this.getUser( invocation.source() ), invocation.arguments() );
    }
}
