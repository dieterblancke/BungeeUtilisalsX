package be.dieterblancke.bungeeutilisalsx.common.api.command;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public interface CommandCall
{

    void onExecute( final User user, final List<String> args, final List<String> parameters );

    String getDescription();

    String getUsage();

}
