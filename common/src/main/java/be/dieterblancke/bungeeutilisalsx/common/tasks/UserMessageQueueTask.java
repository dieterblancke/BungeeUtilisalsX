package be.dieterblancke.bungeeutilisalsx.common.tasks;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

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
