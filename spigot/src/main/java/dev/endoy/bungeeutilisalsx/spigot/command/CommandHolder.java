package dev.endoy.bungeeutilisalsx.spigot.command;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.Command;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandHolder extends org.bukkit.command.Command implements TabExecutor
{

    private final Command command;

    public CommandHolder( final Command command )
    {
        super(
            command.getName(),
            command.getCommand().getDescription(),
            command.getCommand().getUsage(),
            Arrays.stream( command.getAliases() ).collect( Collectors.toList() )
        );

        this.command = command;
    }

    private User getUser( final CommandSender sender )
    {
        return sender instanceof Player
            ? BuX.getApi().getUser( sender.getName() ).orElse( null )
            : BuX.getApi().getConsoleUser();
    }

    @Override
    public boolean execute( @NotNull CommandSender sender,
                            @NotNull String s,
                            @NotNull String[] args )
    {
        this.command.execute( this.getUser( sender ), args );
        return true;
    }

    @Override
    public boolean onCommand( @NotNull CommandSender sender,
                              @NotNull org.bukkit.command.Command command,
                              @NotNull String s,
                              @NotNull String[] args )
    {
        this.command.execute( this.getUser( sender ), args );
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete( @NotNull CommandSender sender,
                                       @NotNull org.bukkit.command.Command command,
                                       @NotNull String s,
                                       @NotNull String[] args )
    {
        return this.command.onTabComplete( this.getUser( sender ), args );
    }
}
