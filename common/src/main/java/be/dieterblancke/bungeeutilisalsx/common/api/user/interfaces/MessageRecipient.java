package be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces;

import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageConfig;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;

public interface MessageRecipient
{

    default boolean isEmpty( final BaseComponent component )
    {
        if ( !( component instanceof TextComponent ) || !( (TextComponent) component ).getText().isEmpty() )
        {
            return false;
        }
        return this.isEmpty( component.getExtra() );
    }

    default boolean isEmpty( final BaseComponent[] components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( BaseComponent component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

    default boolean isEmpty( final Collection<BaseComponent> components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( BaseComponent component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

    LanguageConfig getLanguageConfig();
}
