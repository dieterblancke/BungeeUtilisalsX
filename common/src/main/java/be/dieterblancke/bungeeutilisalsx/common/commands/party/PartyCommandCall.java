package be.dieterblancke.bungeeutilisalsx.common.commands.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.party.sub.*;

import java.util.List;

public class PartyCommandCall extends ParentCommand implements CommandCall
{

    public PartyCommandCall()
    {
        super(
                user -> user.sendLangMessage( "party.help.message" ),
                () -> ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "command.send-message" )
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "create" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.create" )
                        .executable( new PartyCreateSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "invite" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.invite" )
                        .executable( new PartyInviteSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.accept" )
                        .executable( new PartyAcceptSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "leave" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.leave" )
                        .executable( new PartyLeaveSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "chat" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.chat" )
                        .executable( new PartyChatSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "setowner" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.setowner" )
                        .executable( new PartySetOwnerSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "kick" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.kick" )
                        .executable( new PartyKickSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "warp" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.warp" )
                        .executable( new PartyWarpSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "list" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.list" )
                        .executable( new PartyListSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "setrole" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.setrole" )
                        .executable( new PartySetRoleSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "disband" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.disband" )
                        .executable( new PartyDisbandSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "info" )
                        .fromSection( ConfigFiles.PARTY_CONFIG.getConfig(), "subcommands.info" )
                        .executable( new PartyInfoSubCommandCall() )
                        .build()
        );

    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.isEmpty() )
        {
            if ( ConfigFiles.PARTY_CONFIG.getConfig().getBoolean( "command.open-gui" )
                    && BuX.getInstance().isProtocolizeEnabled() )
            {
                BuX.getInstance().getProtocolizeManager().getGuiManager().openGui( user, "party", new String[0] );
            }
        }

        super.onExecute( user, args, parameters );
    }

    @Override
    public String getDescription()
    {
        return "This command either sends a list of available party commands, or it opens a GUI.";
    }

    @Override
    public String getUsage()
    {
        return "/party";
    }
}
