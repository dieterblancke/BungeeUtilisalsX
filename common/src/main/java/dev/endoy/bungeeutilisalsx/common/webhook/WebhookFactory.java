package dev.endoy.bungeeutilisalsx.common.webhook;

public class WebhookFactory
{

    public static DiscordWebhookHelper discord()
    {
        return new DiscordWebhookHelper();
    }
}
