package be.dieterblancke.bungeeutilisalsx.common.webhook;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.DiscordWebhook;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.DiscordWebhook.EmbedObject;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.io.IOException;

public class DiscordWebhookHelper implements WebhookHelper<EmbedObject>
{

    @Override
    public void send( final EmbedObject embedObject )
    {
        final DiscordWebhook webhook = new DiscordWebhook( ConfigFiles.WEBHOOK_CONFIG.getDiscordWebhook() );
        webhook.addEmbed( embedObject );

        BuX.getInstance().getScheduler().runAsync( () ->
        {
            try
            {
                webhook.execute();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        } );
    }
}
