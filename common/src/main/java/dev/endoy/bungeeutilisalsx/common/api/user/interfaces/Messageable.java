package dev.endoy.bungeeutilisalsx.common.api.user.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Collection;

public interface Messageable
{

    /**
     * Checks if the content of a component is empty
     *
     * @param component the component to check
     * @return true if empty, false otherwise
     */
    default boolean isEmpty( final Component component )
    {
        if ( !( component instanceof TextComponent ) || !( (TextComponent) component ).content().isEmpty() )
        {
            return false;
        }
        return this.isEmpty( component.children() );
    }

    /**
     * Checks if the content of an array of components is empty
     *
     * @param components the components to check
     * @return true if empty, false otherwise
     */
    default boolean isEmpty( final Component[] components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( Component component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the content of a collection of components is empty
     *
     * @param components the components to check
     * @return true if empty, false otherwise
     */
    default boolean isEmpty( final Collection<Component> components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( Component component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends a Component message to the user, colors will be formatted.
     *
     * @param component The component to be sent.
     */
    void sendMessage( Component component );

}
