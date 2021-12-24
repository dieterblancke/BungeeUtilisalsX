package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
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

    public static Optional<StaffRankData> getStaffRankForUser( final User user )
    {
        final String group = user.getGroup();

        return ConfigFiles.RANKS.getRanks()
                .stream()
                .filter( rank -> Strings.isNullOrEmpty( group ) ? user.hasPermission( rank.getPermission(), true ) : rank.getName().equalsIgnoreCase( group ) )
                .max( Comparator.comparingInt( StaffRankData::getPriority ) );
    }
}
