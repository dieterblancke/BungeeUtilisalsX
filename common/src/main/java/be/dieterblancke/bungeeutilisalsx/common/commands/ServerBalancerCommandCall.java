package be.dieterblancke.bungeeutilisalsx.common.commands;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ServerBalancerCommandCall implements CommandCall
{

    private final ServerBalancerGroup serverBalancerGroup;

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        // TODO
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
