package dev.endoy.bungeeutilisalsx.common.placeholders;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.impl.InputPlaceHolderImpl;
import dev.endoy.bungeeutilisalsx.common.api.utils.javascript.Script;

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