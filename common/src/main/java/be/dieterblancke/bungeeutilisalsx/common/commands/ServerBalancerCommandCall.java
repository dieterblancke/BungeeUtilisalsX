package be.dieterblancke.bungeeutilisalsx.common.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ServerBalancerCommandCall implements CommandCall
{

    private final ServerBalancerGroup serverBalancerGroup;

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        BuX.getApi().getServerBalancer().getOptimalServer( serverBalancerGroup )
                .ifPresentOrElse( server ->
                {
                    user.sendToServer( server );

                    user.sendLangMessage(
                            "server-balancer.command.sending",
                            MessagePlaceholders.create()
                                    .append( "groupName", serverBalancerGroup.getServerGroup().getName() )
                                    .append( "serverName", server.getName() )
                    );
                }, () -> user.sendLangMessage(
                        "server-balancer.command.no-servers-available",
                        MessagePlaceholders.create().append( "groupName", serverBalancerGroup.getServerGroup().getName() )
                ) );
    }

    @Override
    public String getDescription()
    {
        return "Sends the player to a server balancer group.";
    }

    @Override
    public String getUsage()
    {
        return "/(groupname/command) [player]";
    }
}
