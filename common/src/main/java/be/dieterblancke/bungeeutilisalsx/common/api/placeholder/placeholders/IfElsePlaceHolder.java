package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfElsePlaceHolder
{

    private static final Pattern pattern = Pattern.compile( "(\\{if \\((.+?)\\):)(.+?)(else:)(.+?)(})" );

    public static String format( String message, IfElseFormatter formatter )
    {
        Matcher matcher = pattern.matcher( message );

        while ( matcher.find() )
        {
            final String condition = matcher.group( 2 ).trim();
            final String ifResult = matcher.group( 3 ).trim();
            final String elseResult = matcher.group( 5 ).trim();

            final String result = formatter.format( condition, ifResult, elseResult );

            if ( result != null )
            {
                message = message.replace( matcher.group(), result );
            }
        }

        return message;
    }

    public interface IfElseFormatter
    {
        String format( String condition, String ifResult, String elseResult );
    }
}
