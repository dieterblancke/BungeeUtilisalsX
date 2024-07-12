package dev.endoy.bungeeutilisalsx.common.pluginsupport;

import com.rexcantor64.triton.api.Triton;
import com.rexcantor64.triton.api.language.Localized;
import com.rexcantor64.triton.api.players.LanguagePlayer;
import dev.endoy.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class TritonPluginSupport implements PluginSupport
{

    private Class<?> tritonClass;
    private Method getTritonInstanceMethod;

    @Override
    public boolean isEnabled()
    {
        return BuX.getInstance().serverOperations().getPlugin( "Triton" ).isPresent();
    }

    @Override
    public void registerPluginSupport()
    {
    }

    public String formatGuiMessage( User user, String message )
    {
        if ( user == null )
        {
            return message;
        }

        // TODO: revert this ugly ass fix once Triton API works again for v4
        Optional<Triton> optionalTritonInstance;

        try
        {
            optionalTritonInstance = this.getTritonInstance();
        }
        catch ( Exception e )
        {
            return message;
        }

        Triton tritonInstance = optionalTritonInstance.orElse( null );

        LanguagePlayer player = tritonInstance.getPlayerManager().get( user.getUuid() );
        Localized localized = player.getLanguage();

        return tritonInstance.getMessageParser().translateString( message, localized, tritonInstance.getConfig().getGuiSyntax() ).getResult()
                .orElse( message );
    }

    private Optional<Triton> getTritonInstance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Optional<Method> tritonInstanceMethod = this.getTritonInstanceMethod();

        if ( tritonInstanceMethod.isEmpty() )
        {
            return Optional.empty();
        }

        return Optional.ofNullable( (Triton) tritonInstanceMethod.get().invoke( null ) );
    }

    private Optional<Class<?>> getTritonClass()
    {
        if ( tritonClass == null )
        {
            tritonClass = AbstractBungeeUtilisalsX.getInstance().serverOperations().getPluginInstance( "triton" )
                    .map( Object::getClass )
                    .orElse( null );
        }
        return Optional.ofNullable( tritonClass );
    }

    private Optional<Method> getTritonInstanceMethod() throws NoSuchMethodException
    {
        if ( getTritonInstanceMethod == null )
        {
            Optional<Class<?>> tritonClass = this.getTritonClass();

            if ( tritonClass.isEmpty() )
            {
                getTritonInstanceMethod = null;
            }
            else
            {
                getTritonInstanceMethod = tritonClass.get().getMethod( "get" );
            }
        }

        return Optional.ofNullable( this.getTritonInstanceMethod );
    }
}
