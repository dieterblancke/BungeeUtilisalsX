package be.dieterblancke.bungeeutilisalsx.common.api.command;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ParentCommand implements TabCall
{

    @Getter
    private final List<Command> subCommands = Lists.newArrayList();

    private final Consumer<User> helpConsumer;
    private final Supplier<Boolean> canSend;

    public ParentCommand( final String helpPath )
    {
        this( user -> user.sendLangMessage( helpPath ), () -> true );
    }

    public ParentCommand( final Consumer<User> helpConsumer, final Supplier<Boolean> canSend )
    {
        this.helpConsumer = helpConsumer;
        this.canSend = canSend;

        this.registerSubCommand( CommandBuilder.builder()
                .enabled( true )
                .name( "help" )
                .executable( new HelpSubCommandCall() )
                .build()
        );
    }

    protected void registerSubCommand( final Command cmd )
    {
        if ( cmd == null )
        {
            return;
        }
        subCommands.add( cmd );
    }

    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        // handle sub commands ...
        if ( !subCommands.isEmpty() )
        {
            for ( Command subCommand : subCommands )
            {
                if ( subCommand.check( args ) )
                {
                    subCommand.execute( user, args.subList( 1, args.size() ), parameters );
                    return;
                }
            }
        }
        if ( canSend.get() )
        {
            helpConsumer.accept( user );
        }
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        final List<String> subCommandNames = subCommands.stream().map( Command::getName ).collect( Collectors.toList() );

        if ( args.length == 0 )
        {
            return subCommandNames;
        }
        else if ( args.length == 1 )
        {
            return Utils.copyPartialMatches( args[0], subCommandNames, Lists.newArrayList() );
        }
        else
        {
            return null;
        }
    }

    private class HelpSubCommandCall implements CommandCall
    {
        @Override
        public void onExecute( User user, List<String> args, List<String> parameters )
        {
            helpConsumer.accept( user );
        }

        @Override
        public String getDescription()
        {
            return "Displays help information for the " + this.getCommandName() + "command";
        }

        @Override
        public String getUsage()
        {
            return "/" + this.getCommandName() + " help";
        }

        private String getCommandName()
        {
            return this.getClass().getSimpleName().toLowerCase().split( "command" )[0];
        }
    }
}
