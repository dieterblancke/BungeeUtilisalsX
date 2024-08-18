package dev.endoy.bungeeutilisalsx.common.commands.friends;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandBuilder;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.ParentCommand;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.commands.friends.sub.*;

import java.util.List;

public class FriendsCommandCall extends ParentCommand implements CommandCall
{

    public FriendsCommandCall()
    {
        super(
            user -> user.sendLangMessage( "friends.help.message" ),
            () -> ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "command.send-message" )
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "add" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.add" )
                .executable( new FriendAddSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "accept" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.accept" )
                .executable( new FriendAcceptSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "deny" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.deny" )
                .executable( new FriendDenySubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "removerequest" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.removerequest" )
                .executable( new FriendRemoveRequestSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "remove" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.remove" )
                .executable( new FriendRemoveSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "list" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.list" )
                .executable( new FriendListSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "requests" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.requests" )
                .executable( new FriendRequestsSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "msg" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.msg" )
                .executable( new FriendMsgSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "reply" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.reply" )
                .executable( new FriendReplySubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "settings" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.settings" )
                .executable( new FriendSettingsSubCommandCall() )
                .build()
        );

        super.registerSubCommand(
            CommandBuilder.builder()
                .name( "broadcast" )
                .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.broadcast" )
                .executable( new FriendBroadcastSubCommandCall() )
                .build()
        );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            if ( ConfigFiles.FRIENDS_CONFIG.getConfig().getBoolean( "command.open-gui" )
                && BuX.getInstance().isProtocolizeEnabled() )
            {
                BuX.getInstance().getProtocolizeManager().getGuiManager().openGui( user, "friend", new String[0] );
            }
        }

        super.onExecute( user, args, parameters );
    }

    @Override
    public String getDescription()
    {
        return "This command either sends a list of available friends commands, or it opens a GUI.";
    }

    @Override
    public String getUsage()
    {
        return "/friends";
    }
}
