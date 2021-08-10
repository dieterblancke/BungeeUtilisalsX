package be.dieterblancke.bungeeutilisalsx.common.job;

import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;
import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobManager;
import be.dieterblancke.bungeeutilisalsx.common.api.rabbitmq.RabbitMQConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MultiProxyJobManager extends JobManager
{

    private static final String ALL_PROXIES_CHANNEL = "bux:jobs:all";
    private static final String SINGLE_PROXIES_CHANNEL = "bux:jobs:single";
    private final RabbitMQConnectionFactory connectionFactory;

    @SneakyThrows
    public MultiProxyJobManager()
    {
        connectionFactory = new RabbitMQConnectionFactory();

        final Channel channel = connectionFactory.getChannel();
        channel.exchangeDeclare( ALL_PROXIES_CHANNEL, "fanout", true );
        final String queueName = channel.queueDeclare().getQueue();
        channel.queueBind( queueName, ALL_PROXIES_CHANNEL, "" );
        channel.basicConsume( queueName, true, new MultiProxyDeliverCallback(), consumerTag ->
        {
        } );

        channel.queueDeclare( SINGLE_PROXIES_CHANNEL, true, false, false, null );
        channel.basicConsume( SINGLE_PROXIES_CHANNEL, false, new SingleProxyDeliverCallback( channel ), consumerTag ->
        {
        } );
    }

    @Override
    @SneakyThrows
    public void executeJob( final Job job )
    {
        if ( job instanceof MultiProxyJob )
        {
            connectionFactory.getChannel().basicPublish( ALL_PROXIES_CHANNEL, "", null, this.encodeJob( job ) );
        }
        else
        {
            connectionFactory.getChannel().basicPublish( SINGLE_PROXIES_CHANNEL, "", null, this.encodeJob( job ) );
        }
    }

    private void handleMessage( final String message )
    {
        this.handle( this.decodeJob( message ) );
    }

    class MultiProxyDeliverCallback implements DeliverCallback
    {

        @Override
        public void handle( final String consumerTag, final Delivery delivery ) throws IOException
        {
            handleMessage( new String( delivery.getBody(), StandardCharsets.UTF_8 ) );
        }
    }

    @RequiredArgsConstructor
    class SingleProxyDeliverCallback implements DeliverCallback
    {

        private final Channel channel;

        @Override
        public void handle( final String consumerTag, final Delivery delivery ) throws IOException
        {
            try
            {
                handleMessage( new String( delivery.getBody(), StandardCharsets.UTF_8 ) );
            }
            finally
            {
                channel.basicAck( delivery.getEnvelope().getDeliveryTag(), false );
            }
        }
    }
}
