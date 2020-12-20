package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

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
            boolean isHidden = false;

            for ( StaffUser user : BuX.getApi().getStaffMembers() )
            {
                if ( user.getName().equalsIgnoreCase( player ) && user.isHidden() )
                {
                    isHidden = true;
                    break;
                }
            }

            if ( !isHidden )
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
            if ( user.getName().equalsIgnoreCase( name ) && user.isHidden() )
            {
                return true;
            }
        }
        return false;
    }
}
