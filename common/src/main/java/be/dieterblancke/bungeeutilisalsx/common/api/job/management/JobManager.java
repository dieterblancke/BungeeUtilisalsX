package be.dieterblancke.bungeeutilisalsx.common.api.job.management;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class JobManager
{

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter( Job.class, new JobInterfaceAdapter() )
            .create();
    private static final List<JobHandlerInfo> JOB_HANDLERS = new ArrayList<>();
    private static final Map<Class<?>, Object> JOB_HANDLER_INSTANCES = Maps.newHashMap();

    static
    {
        final List<Class<?>> classes = Utils.getClassesInPackage( "be.dieterblancke.bungeeutilisalsx.common.job.handler" );

        BuX.debug( classes.toString() );

        for ( Class<?> clazz : classes )
        {
            for ( Method method : clazz.getDeclaredMethods() )
            {
                if ( method.isAnnotationPresent( JobHandler.class ) )
                {
                    method.setAccessible( true );

                    if ( method.getParameterTypes().length == 1 )
                    {
                        final Class<?> clazzType = method.getParameterTypes()[0];

                        if ( Job.class.isAssignableFrom( clazzType ) )
                        {
                            JOB_HANDLERS.add( new JobHandlerInfo( clazzType, method ) );
                        }
                    }
                }
            }
        }
    }

    public abstract CompletableFuture<Void> executeJob( Job job );

    @SneakyThrows
    protected void handle( final Job job )
    {
        JOB_HANDLERS.stream()
                .filter( handler -> handler.getJobClass().equals( job.getClass() ) )
                .forEach( jobHandlerInfo ->
                {
                    final Class<?> clazz = jobHandlerInfo.getHandler().getDeclaringClass();
                    final Object instance = getJobHandlerInstance( clazz );

                    if ( job.isAsync() )
                    {
                        BuX.getInstance().getScheduler().runAsync( () -> invokeJobHandler( jobHandlerInfo, instance, job ) );
                    }
                    else
                    {
                        this.invokeJobHandler( jobHandlerInfo, instance, job );
                    }
                } );
    }

    @SneakyThrows
    private void invokeJobHandler( final JobHandlerInfo jobHandlerInfo, final Object instance, final Job job )
    {
        jobHandlerInfo.getHandler().invoke( instance, job );
    }

    @SneakyThrows
    private Object getJobHandlerInstance( final Class<?> clazz )
    {
        if ( !JOB_HANDLER_INSTANCES.containsKey( clazz ) )
        {
            JOB_HANDLER_INSTANCES.put( clazz, clazz.getConstructor().newInstance() );
        }

        return JOB_HANDLER_INSTANCES.get( clazz );
    }

    protected byte[] encodeJob( final Job job )
    {
        return GSON.toJson( job ).getBytes( StandardCharsets.UTF_8 );
    }

    protected Job decodeJob( final String message )
    {
        return GSON.fromJson( message, Job.class );
    }
}
