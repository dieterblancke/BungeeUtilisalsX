package be.dieterblancke.bungeeutilisalsx.common.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.configuration.api.ISection;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CustomCommandCall implements CommandCall
{

    private final ISection section;
    private final String server;
    private final List<String> commands;

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( section.exists( "alias-for" ) )
        {
            user.executeCommand( section.getString( "alias-for" ) );
            return;
        }
        if ( !server.equals( "all" ) && !server.equalsIgnoreCase( "global" ) )
        {
            final Optional<ServerGroup> optionalGroup = ConfigFiles.SERVERGROUPS.getServer( server );

            if ( optionalGroup.isPresent() && !optionalGroup.get().isInGroup( user.getServerName() ) )
            {
                return;
            }
        }
        final String messagesKey = "messages";

        if ( section.exists( messagesKey ) )
        {
            final Component component;

            if ( section.isList( messagesKey ) )
            {
                component = MessageBuilder.buildMessage( user, section.getSectionList( messagesKey ) );
            }
            else
            {
                component = MessageBuilder.buildMessage( user, section.getSection( messagesKey ) );
            }

            user.sendMessage( component );
        }
        commands.forEach( command -> BuX.getApi().getConsoleUser().executeCommand(
                PlaceHolderAPI.formatMessage( user, command )
        ) );
    }

    @Override
    public String getDescription()
    {
        return "";
    }

    @Override
    public String getUsage()
    {
        return "";
    }
}
