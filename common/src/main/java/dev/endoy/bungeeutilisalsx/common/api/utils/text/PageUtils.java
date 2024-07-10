package dev.endoy.bungeeutilisalsx.common.api.utils.text;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.Getter;

import java.util.List;

public class PageUtils
{

    public static <T> List<T> getPageFromList( final int page, final List<T> list, final int pageSize ) throws PageNotFoundException
    {
        final int maxPages = (int) Math.ceil( list.size() / (double) pageSize );

        if ( page > maxPages )
        {
            throw new PageNotFoundException( page, maxPages );
        }

        final int begin = ( ( page - 1 ) * 10 );
        int end = begin + 10;

        if ( end > list.size() )
        {
            end = list.size();
        }

        return list.subList( begin, end );
    }

    public static <T> void sendPagedList( final User user,
                                          final List<T> list,
                                          final String pageStr,
                                          final int pageSize,
                                          final PageResponseHandler<T> responseHandler )
    {
        if ( list.isEmpty() )
        {
            responseHandler.getEmptyListMessage().sendMessage( user, MessagePlaceholders.empty() );
            return;
        }

        final int pages = (int) Math.ceil( (double) list.size() / pageSize );
        final int page;
        if ( MathUtils.isInteger( pageStr ) )
        {
            page = Math.min( Integer.parseInt( pageStr ), pages );
        }
        else
        {
            page = 1;
        }

        try
        {
            List<T> listPage = PageUtils.getPageFromList( page, list, pageSize );
            int previous = page > 1 ? page - 1 : 1;
            int next = Math.min( page + 1, pages );

            MessagePlaceholders pagePlaceholders = MessagePlaceholders.create()
                    .append( "page", page )
                    .append( "maxPages", pages )
                    .append( "previousPage", previous )
                    .append( "nextPage", next );

            responseHandler.getHeaderMessage().sendMessage( user, pagePlaceholders );

            for ( T item : listPage )
            {
                responseHandler.getItemMessage( item ).sendMessage( user, pagePlaceholders );
            }

            responseHandler.getFooterMessage().sendMessage( user, pagePlaceholders );
        }
        catch ( PageUtils.PageNotFoundException e )
        {
            responseHandler.getInvalidPageMessage().sendMessage(
                    user,
                    MessagePlaceholders.create()
                            .append( "page", e.getPage() )
                            .append( "maxpages", e.getMaxPages() )
                            .append( "maxPages", e.getMaxPages() )
            );
        }
    }

    public interface PageResponseHandler<T>
    {

        PageMessageInfo getEmptyListMessage();

        PageMessageInfo getHeaderMessage();

        PageMessageInfo getItemMessage( T type );

        PageMessageInfo getFooterMessage();

        PageMessageInfo getInvalidPageMessage();

    }

    public static class PageMessageInfo
    {

        private final String path;
        private final MessagePlaceholders parameters;

        public PageMessageInfo( String path )
        {
            this.path = path;
            this.parameters = MessagePlaceholders.empty();
        }

        public PageMessageInfo( String path, MessagePlaceholders parameters )
        {
            this.path = path;
            this.parameters = parameters;
        }

        public void sendMessage( User user, MessagePlaceholders parameters )
        {
            user.sendLangMessage(
                    path,
                    MessagePlaceholders.create()
                            .append( this.parameters )
                            .append( parameters )
            );
        }
    }

    public static class PageNotFoundException extends Exception
    {

        @Getter
        private final int page;
        @Getter
        private final int maxPages;

        public PageNotFoundException( int page, int maxPages )
        {
            this.page = page;
            this.maxPages = maxPages;
        }
    }
}
