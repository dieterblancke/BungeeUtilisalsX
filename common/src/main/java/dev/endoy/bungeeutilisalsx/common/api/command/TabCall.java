package dev.endoy.bungeeutilisalsx.common.api.command;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public interface TabCall
{

    List<String> onTabComplete( final User user, final String[] args );
}
