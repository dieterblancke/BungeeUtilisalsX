package com.dbsoftwares.bungeeutilisals.api.utils;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.google.common.collect.Lists;

import java.util.List;

public class StaffUtils
{

    private StaffUtils()
    {
    }

    public static List<String> filterPlayerList( final List<String> players )
    {
        final List<String> temp = Lists.newArrayList();

        for ( String player : players )
        {
            boolean isHidden = false;

            for ( StaffUser user : BUCore.getApi().getStaffMembers() )
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
}
