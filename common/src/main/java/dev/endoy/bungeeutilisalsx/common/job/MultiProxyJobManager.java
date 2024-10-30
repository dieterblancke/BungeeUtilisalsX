package dev.endoy.bungeeutilisalsx.common.job;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.job.Job;
import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobManager;
import dev.endoy.bungeeutilisalsx.common.api.rabbitmq.RabbitMQConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<Void> executeJob( final Job job )
    {
        return CompletableFuture.runAsync( () ->
        {
            if ( job instanceof MultiProxyJob )
            {
                this.basicPublish( ALL_PROXIES_CHANNEL, this.encodeJob( job ) );
            }
            else
            {
                this.basicPublish( SINGLE_PROXIES_CHANNEL, this.encodeJob( job ) );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @SneakyThrows
    private void basicPublish( final String channel, final byte[] message )
    {
        connectionFactory.getChannel().basicPublish( channel, "", null, message );
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
