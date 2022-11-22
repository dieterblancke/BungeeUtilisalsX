package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserSettingType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.ArrayList;
import java.util.List;

public class ToggleBossBarCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        UserSetting userSetting = new UserSetting(
                UserSettingType.BOSSBAR_DISABLED,
                user.getSettings().getUserSetting( UserSettingType.BOSSBAR_DISABLED ).map( it -> !it.getAsBoolean() ).orElse( true )
        );

        user.getSettings().upsertUserSetting( userSetting );

        if ( userSetting.getAsBoolean() )
        {
            user.sendLangMessage( "general-commands.togglebossbar.disabled" );

            new ArrayList<>( user.getActiveBossBars() )
                    .forEach( bossBar ->
                    {
                        bossBar.removeUser( user );
                    } );
        }
        else
        {
            user.sendLangMessage( "general-commands.togglebossbar.enabled" );
        }
    }

    @Override
    public String getDescription()
    {
        return "Enables or disables receiving bossbars.";
    }

    @Override
    public String getUsage()
    {
        return "/togglebossbar [on / off]";
    }
}
