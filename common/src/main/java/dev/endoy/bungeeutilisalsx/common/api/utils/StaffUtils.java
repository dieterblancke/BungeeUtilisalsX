package dev.endoy.bungeeutilisalsx.common.api.utils;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StaffUtils
{

    private StaffUtils()
    {
    }

    public static List<String> filterPlayerList( final Collection<String> players )
    {
        final List<String> temp = Lists.newArrayList();

        for ( String player : players )
        {
            if ( !isHidden( player ) )
            {
                temp.add( player );
            }
        }
        return temp;
    }

    public static boolean isHidden( final String name )
    {
        for ( StaffUser user : BuX.getApi().getStaffMembers() )
        {
            if ( user.getName().equalsIgnoreCase( name ) && ( user.isHidden() || user.isVanished() ) )
            {
                return true;
            }
        }
        return BuX.getApi().getUser( name ).map( User::isVanished ).orElse( false );
    }

    public static Optional<StaffRankData> getStaffRankForUser( User user )
    {
        String group = user.getGroup();

        if ( !Strings.isNullOrEmpty( group ) )
        {
            BuX.debug( "User " + user.getName() + " detected with group: " + group + ", trying to match rank on group first ..." );

            return ConfigFiles.RANKS.getRanks()
                    .stream()
                    .sorted( Comparator.comparingInt( StaffRankData::getPriority ) )
                    .filter( rank -> rank.getName().equalsIgnoreCase( group ) )
                    .findFirst()
                    .or( () -> getStaffRankForUserByPermissions( user ) );
        }

        return getStaffRankForUserByPermissions( user );
    }

    private static Optional<StaffRankData> getStaffRankForUserByPermissions( User user )
    {
        return ConfigFiles.RANKS.getRanks()
                .stream()
                .sorted( Comparator.comparingInt( StaffRankData::getPriority ) )
                .filter( rank -> user.hasPermission( rank.getPermission(), true ) )
                .findFirst();
    }
}
