package be.dieterblancke.bungeeutilisalsx.common.webhook;

public interface WebhookHelper<T>
{

    void send( T data );

}
