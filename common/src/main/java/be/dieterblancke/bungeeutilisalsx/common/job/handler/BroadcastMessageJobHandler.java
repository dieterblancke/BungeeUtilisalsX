package be.dieterblancke.bungeeutilisalsx.common.job.handler;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Strings;

public class BroadcastMessageJobHandler extends AbstractJobHandler
{

    @JobHandler
    void handleBroadcastMessageJob( final BroadcastMessageJob job )
    {
        for ( User user : BuX.getApi().getUsers() )
        {
            if ( Strings.isNullOrEmpty( job.getPermission() ) || user.hasPermission( job.getPermission() ) )
            {
                this.sendMessage( user, job );
            }
        }
        this.sendMessage( BuX.getApi().getConsoleUser(), job );
    }

    @JobHandler
    void handleBroadcastLanguageMessageJob( final BroadcastLanguageMessageJob job )
    {
        for ( User user : BuX.getApi().getUsers() )
        {
            if ( Strings.isNullOrEmpty( job.getPermission() ) || user.hasPermission( job.getPermission() ) )
            {
                this.sendMessage( user, job );
            }
        }
        this.sendMessage( BuX.getApi().getConsoleUser(), job );
    }

    private void sendMessage( final User user, final BroadcastMessageJob job )
    {
        if ( job.getPrefix() != null )
        {
            user.sendMessage( job.getPrefix(), job.getMessage() );
        }
        else
        {
            user.sendMessage( job.getMessage() );
        }
    }

    private void sendMessage( final User user, final BroadcastLanguageMessageJob job )
    {
        user.sendLangMessage( job.getLanguagePath(), job.getPlaceholders() );
    }
}
