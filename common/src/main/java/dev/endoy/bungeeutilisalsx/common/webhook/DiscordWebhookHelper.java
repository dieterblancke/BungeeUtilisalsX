package dev.endoy.bungeeutilisalsx.common.webhook;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.DiscordWebhook;
import dev.endoy.bungeeutilisalsx.common.api.utils.DiscordWebhook.EmbedObject;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

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
