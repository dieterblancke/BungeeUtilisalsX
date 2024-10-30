package dev.endoy.bungeeutilisalsx.common.api.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class RabbitMQConnectionFactory
{

    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    public RabbitMQConnectionFactory()
    {
        this.connectionFactory = new ConnectionFactory();
        try
        {
            this.connectionFactory.setUri( ConfigFiles.CONFIG.getConfig().getString( "multi-proxy.rabbitmq-uri" ) );
        }
        catch ( URISyntaxException | NoSuchAlgorithmException | KeyManagementException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not parse the RabbitMQ URI! Please check your URI. As RabbitMQ is required for BuX to work in this mode, the proxy will shut down ...", e );
            System.exit( -1 );
        }
    }

    public Connection getConnection() throws RabbitMQException
    {
        if ( connection == null || !connection.isOpen() )
        {
            try
            {
                connection = connectionFactory.newConnection();
            }
            catch ( IOException | TimeoutException e )
            {
                throw new RabbitMQException( "Could not instantiate new connection", e );
            }
        }
        return connection;
    }

    public Channel getChannel() throws RabbitMQException
    {
        if ( channel == null || !channel.isOpen() )
        {
            try
            {
                channel = this.getConnection().createChannel();
            }
            catch ( IOException e )
            {
                throw new RabbitMQException( "Could not instantiate new channel", e );
            }
        }
        return channel;
    }

    public void shutdown() throws RabbitMQException
    {
        try
        {
            this.channel.close();
            this.connection.close();
        }
        catch ( IOException | TimeoutException e )
        {
            throw new RabbitMQException( "Could not shutdown the RabbitMQ connections", e );
        }
    }
}
