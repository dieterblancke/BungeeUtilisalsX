package com.dbsoftwares.bungeeutilisals.commands;

import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

@AllArgsConstructor
public class CustomCommandCall implements CommandCall
{

    private final ISection section;
    private final String server;
    private final List<String> commands;

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        if ( !server.equals( "all" ) && !server.equalsIgnoreCase( "global" ) )
        {
            final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( server );

            if ( !group.isInGroup( user.getServerName() ) )
            {
                return;
            }
        }
        final String messagesKey = "messages";
        final List<TextComponent> components;

        if ( section.isList( messagesKey ) )
        {
            components = MessageBuilder.buildMessage( user, section.getSectionList( messagesKey ) );
        }
        else
        {
            components = Lists.newArrayList( MessageBuilder.buildMessage( user, section.getSection( messagesKey ) ) );
        }

        components.forEach( user::sendMessage );
        commands.forEach( command -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                ProxyServer.getInstance().getConsole(),
                PlaceHolderAPI.formatMessage( user, command )
        ) );
    }
}
