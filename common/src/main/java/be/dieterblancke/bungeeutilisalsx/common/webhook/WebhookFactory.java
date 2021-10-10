package be.dieterblancke.bungeeutilisalsx.common.webhook;

public class WebhookFactory
{

    public static DiscordWebhookHelper discord()
    {
        return new DiscordWebhookHelper();
    }
}
