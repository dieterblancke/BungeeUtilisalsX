package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.impl.InputPlaceHolderImpl;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript.Script;

import java.util.Optional;

public class JavaScriptPlaceHolder extends InputPlaceHolderImpl
{

    public JavaScriptPlaceHolder()
    {
        super( false, "javascript" );
    }

    @Override
    public String getReplacement( InputPlaceHolderEvent event )
    {
        final Optional<Script> optional = BuX.getInstance().getScripts()
                .stream()
                .filter( s -> s.getFile().equalsIgnoreCase( event.getArgument() ) )
                .findFirst();

        if ( optional.isPresent() )
        {
            return optional.get().getReplacement( event.getUser() );
        }
        else
        {
            return "script not found";
        }
    }
}