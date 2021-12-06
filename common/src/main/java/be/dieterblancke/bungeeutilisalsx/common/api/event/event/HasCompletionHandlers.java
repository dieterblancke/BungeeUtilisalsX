package be.dieterblancke.bungeeutilisalsx.common.api.event.event;

import java.util.List;
import java.util.function.Consumer;

public interface HasCompletionHandlers<T>
{

    default void handleCompletion()
    {
        for ( Consumer<T> completionHandler : this.getCompletionHandlers() )
        {
            completionHandler.accept( (T) this );
        }
    }

    List<Consumer<T>> getCompletionHandlers();

}
