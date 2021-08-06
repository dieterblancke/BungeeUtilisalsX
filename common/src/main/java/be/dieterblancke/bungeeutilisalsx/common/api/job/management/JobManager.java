package be.dieterblancke.bungeeutilisalsx.common.api.job.management;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.Job;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class JobManager
{

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter( Job.class, new JobInterfaceAdapter<Job>() )
            .create();
    private static final List<JobHandlerInfo> JOB_HANDLERS = new ArrayList<>();
    private static final Map<Class<?>, Object> JOB_HANDLER_INSTANCES = Maps.newHashMap();

    static
    {
        final CodeSource src = JobManager.class.getProtectionDomain().getCodeSource();

        if ( src != null )
        {
            try
            {
                final URL jar = src.getLocation();
                final ZipInputStream zip = new ZipInputStream( jar.openStream() );

                while ( true )
                {
                    final ZipEntry e = zip.getNextEntry();
                    if ( e == null )
                    {
                        break;
                    }
                    final String name = e.getName();

                    if ( name.startsWith( "be/dieterblancke/bungeeutilisalsx/common/job/handler" ) )
                    {
                        if ( name.endsWith( ".class" ) )
                        {
                            final Class<?> clazz = Class.forName( name.replace( "/", "." ).replace( ".class", "" ) );

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
                }
            }
            catch ( IOException | ClassNotFoundException e )
            {
                e.printStackTrace();
            }
        }
    }

    public abstract void executeJob( Job job );

    @SneakyThrows
    protected void handle( final Job job )
    {
        final JobHandlerInfo jobHandlerInfo = JOB_HANDLERS.stream()
                .filter( handler -> handler.getJobClass().equals( job.getClass() ) )
                .findFirst()
                .orElse( null );

        if ( jobHandlerInfo != null )
        {
            final Class<?> clazz = jobHandlerInfo.getHandler().getDeclaringClass();

            if ( !JOB_HANDLER_INSTANCES.containsKey( clazz ) )
            {
                JOB_HANDLER_INSTANCES.put( clazz, clazz.getConstructor().newInstance() );
            }

            final Object instance = JOB_HANDLER_INSTANCES.get( clazz );

            if ( job.isAsync() )
            {
                BuX.getInstance().getScheduler().runAsync( () -> invokeJobHandler( jobHandlerInfo, instance, job ) );
            }
            else
            {
                this.invokeJobHandler( jobHandlerInfo, instance, job );
            }
        }
    }

    @SneakyThrows
    private void invokeJobHandler( final JobHandlerInfo jobHandlerInfo, final Object instance, final Job job )
    {
        jobHandlerInfo.getHandler().invoke( instance, job );
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
