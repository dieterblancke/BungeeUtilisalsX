package dev.endoy.bungeeutilisalsx.common.api.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class TabCompleter
{

    private static final List<String> EMPTY = ImmutableList.of();

    public static List<String> buildTabCompletion( final Collection<String> coll, final String[] args )
    {
        if ( args.length == 0 )
        {
            return Lists.newArrayList( coll );
        }
        else
        {
            final String lastWord = args[args.length - 1];
            final List<String> list = Lists.newArrayList();

            for ( String s : coll )
            {
                if ( s.toLowerCase().startsWith( lastWord.toLowerCase() ) )
                {
                    list.add( s );
                }
            }

            return list;
        }
    }

    public static List<String> empty()
    {
        return EMPTY;
    }
}
