package dev.endoy.bungeeutilisalsx.common.commands;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.OpenGuiJob;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Strings;

import java.util.List;

public class OpenGuiCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.opengui.usage" );
            return;
        }
        final String gui = args.get( 0 );
        final String[] guiArgs = args.stream().skip( 1 ).toArray( String[]::new );

        if ( gui.equalsIgnoreCase( "custom" ) && guiArgs.length == 0 )
        {
            user.sendLangMessage( "general-commands.opengui.usage-custom" );
            return;
        }

        if ( parameters.stream().anyMatch( it -> it.startsWith( "-u" ) ) )
        {
            final String targetUser = parameters.stream()
                    .filter( it -> it.startsWith( "-u" ) )
                    .findFirst()
                    .map( it -> it.replaceFirst( "-u=", "" ) )
                    .orElse( "" );

            if ( !Strings.isNullOrEmpty( targetUser ) )
            {
                if ( !BuX.getApi().getPlayerUtils().isOnline( targetUser ) )
                {
                    user.sendLangMessage( "offline" );
                    return;
                }

                BuX.getInstance().getJobManager().executeJob( new OpenGuiJob( targetUser, gui, guiArgs ) );
                return;
            }
        }

        BuX.getInstance().getProtocolizeManager().getGuiManager().openGui(
                user,
                gui,
                guiArgs
        );
    }

    @Override
    public String getDescription()
    {
        return """
                Opens a given gui for yourself or another user, for another user, you need this permission: COMMANDPERMISSION.parameters.-u!
                For example: /opengui custom test will open a custom GUI named from the gui/custom/test.yml file.
                """;
    }

    @Override
    public String getUsage()
    {
        return "/opengui (gui) [args] [-u=USER]";
    }
}
