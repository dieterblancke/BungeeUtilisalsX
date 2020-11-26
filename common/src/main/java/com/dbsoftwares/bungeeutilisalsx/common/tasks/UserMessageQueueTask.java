package com.dbsoftwares.bungeeutilisalsx.common.tasks;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

public class UserMessageQueueTask implements Runnable
{
    @Override
    public void run()
    {
        for ( User user : BuX.getApi().getUsers() )
        {
            if ( user.getMessageQueue() != null )
            {
                user.getMessageQueue().refetch();
                user.executeMessageQueue();
            }
        }
    }
}
