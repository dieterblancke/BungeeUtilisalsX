package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;
import java.util.stream.Collectors;

public class StaffCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() > 0 )
        {
            final IConfiguration config = ConfigFiles.GENERALCOMMANDS.getConfig();

            // Big yikes code, but works for now
            final List<String> commands = Arrays.asList( config.getString( "staff.toggle.aliases" ).split( ", " ) );

            if ( config.getBoolean( "staff.toggle.enabled" )
                    && ( config.getString( "staff.toggle.name" ).equalsIgnoreCase( args.get( 0 ) ) || commands.contains( args.get( 0 ) ) )
                    && user.hasPermission( config.getString( "staff.toggle.permission" ) ) )
            {
                StaffUser staffUser = null;

                for ( StaffUser su : BuX.getInstance().getStaffMembers() )
                {
                    if ( su.getUuid().equals( user.getUuid() ) )
                    {
                        staffUser = su;
                    }
                }

                if ( staffUser != null )
                {
                    if ( staffUser.isHidden() )
                    {
                        staffUser.setHidden( false );
                        user.sendLangMessage( "general-commands.staff.toggle.unhidden" );
                    }
                    else
                    {
                        staffUser.setHidden( true );
                        user.sendLangMessage( "general-commands.staff.toggle.hidden" );
                    }
                    return;
                }
            }
        }

        final List<StaffUser> staffUsers = BuX.getInstance().getStaffMembers()
                .stream()
                .filter( staffUser -> !staffUser.isHidden() && !staffUser.isVanished() )
                .collect( Collectors.toList() );
        if ( staffUsers.isEmpty() )
        {
            user.sendLangMessage( "general-commands.staff.no_staff" );
            return;
        }

        final Map<StaffRankData, List<StaffUser>> staffMembers = staffUsers
                .stream()
                .collect( Collectors.groupingBy( StaffUser::getRank ) );

        final LinkedList<StaffRankData> onlineStaffRanks = staffMembers
                .keySet()
                .stream()
                .sorted( Comparator.comparingInt( StaffRankData::getPriority ) )
                .collect( Collectors.toCollection( Lists::newLinkedList ) );

        user.sendLangMessage( "general-commands.staff.head", "{total}", staffUsers.size() );

        for ( StaffRankData rank : onlineStaffRanks )
        {
            final List<StaffUser> users = staffMembers.get( rank );
            final TextComponent component = MessageBuilder.buildMessage(
                    user,
                    user.getLanguageConfig().getConfig().getSection( "general-commands.staff.rank" ),
                    "{rank_displayname}", Utils.c( rank.getDisplay() ),
                    "{amount_online}", users.size(),
                    "{total}", staffUsers.size()
            );

            final List<TextComponent> components = findUsersComponents( component );

            for ( TextComponent c : components )
            {
                c.setText( "" );

                users.sort( Comparator.comparing( StaffUser::getName ) );
                final Iterator<StaffUser> userIt = users.iterator();

                while ( userIt.hasNext() )
                {
                    final StaffUser u = userIt.next();
                    final IProxyServer info = BuX.getApi().getPlayerUtils().findPlayer( u.getName() );

                    c.addExtra( MessageBuilder.buildMessage(
                            user,
                            user.getLanguageConfig().getConfig().getSection( "general-commands.staff.users.user" ),
                            "{username}", u.getName(),
                            "{server}", info == null ? "Unknown" : info.getName()
                    ) );

                    if ( userIt.hasNext() )
                    {
                        c.addExtra( Utils.c( user.getLanguageConfig().buildLangMessage( "general-commands.staff.users.separator" ) ) );
                    }
                }
            }

            user.sendMessage( component );
        }

        user.sendLangMessage( "general-commands.staff.foot", "{total}", staffUsers.size() );
    }

    @Override
    public String getDescription()
    {
        return "Lists online staff by rank.";
    }

    @Override
    public String getUsage()
    {
        return "/staff";
    }

    private List<TextComponent> findUsersComponents( final TextComponent component )
    {
        final List<TextComponent> components = component.getExtra().stream()
                .filter( c -> c instanceof TextComponent )
                .map( c -> (TextComponent) c )
                .collect( Collectors.toList() );
        final List<TextComponent> finalComponents = Lists.newArrayList();

        for ( TextComponent c : components )
        {
            if ( c.getExtra() != null && !c.getExtra().isEmpty() )
            {
                finalComponents.addAll( findUsersComponents( c ) );
            }
            if ( c.getText().contains( "{users}" ) )
            {
                finalComponents.add( c );
            }
        }
        return finalComponents;
    }
}
