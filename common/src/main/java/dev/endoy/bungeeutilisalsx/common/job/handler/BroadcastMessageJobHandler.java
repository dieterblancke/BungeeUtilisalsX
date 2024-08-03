package dev.endoy.bungeeutilisalsx.common.job.handler;

import com.google.common.base.Strings;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.BroadcastMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.ExternalPluginBroadcastLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.AbstractJobHandler;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobHandler;
import dev.endoy.bungeeutilisalsx.common.api.language.LanguageConfig;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

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

    @JobHandler
    void handleExternalPluginLanguageMessageJob( final ExternalPluginBroadcastLanguageMessageJob job )
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

    private void sendMessage( final User user, final ExternalPluginBroadcastLanguageMessageJob job )
    {
        LanguageConfig languageConfig = BuX.getApi().getLanguageManager().getLanguageConfiguration( job.getPluginName(), user );

        languageConfig.sendLangMessage( user, job.getLanguagePath(), job.getPlaceholders() );
    }
}
