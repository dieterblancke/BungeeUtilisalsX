package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtection;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Data;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SwearChatProtection implements ChatProtection
{

    private boolean enabled;
    private String bypassPermission;
    private String replaceWith;
    private List<Pattern> blockedWords;
    private List<SwearPattern> blockedWordPatterns;

    @Override
    public void reload()
    {
        final IConfiguration config = ConfigFiles.ANTISWEAR.getConfig();

        this.enabled = config.getBoolean( "enabled" );
        this.bypassPermission = config.getString( "bypass" );
        this.replaceWith = config.getString( "replace" );
        this.blockedWords = config.getStringList( "words" )
                .stream()
                .map( word ->
                {
                    final StringBuilder builder = new StringBuilder( "\\b(" );

                    for ( char o : word.toCharArray() )
                    {
                        builder.append( o );
                        builder.append( "+(\\W|\\d\\_)*" );
                    }
                    builder.append( ")\\b" );

                    return Pattern.compile( builder.toString() );
                } )
                .collect( Collectors.toList() );
        this.blockedWordPatterns = ConfigFiles.ANTISWEAR.getConfig().getSectionList( "patterns" )
                .stream()
                .map( section -> new SwearPattern(
                        Pattern.compile( section.getString( "expression" ) ),
                        section.getStringList( "whitelist" )
                ) )
                .collect( Collectors.toList() );
    }

    @Override
    public SwearValidationResult validateMessage( final User user, final String message )
    {
        if ( !enabled || user.hasPermission( bypassPermission ) )
        {
            return new SwearValidationResult( true, null, message );
        }

        for ( Pattern blockedWordPattern : this.blockedWords )
        {
            final SwearValidationResult validationResult = this.validateMessage( message, blockedWordPattern, null );

            if ( !validationResult.isValid() )
            {
                return validationResult;
            }
        }

        for ( SwearPattern blockedWordPattern : this.blockedWordPatterns )
        {
            final SwearValidationResult validationResult = this.validateMessage(
                    message, blockedWordPattern.getPattern(), blockedWordPattern.getWhitelist()
            );

            if ( !validationResult.isValid() )
            {
                return validationResult;
            }
        }

        return new SwearValidationResult( true, null, message );
    }

    private SwearValidationResult validateMessage( final String message, final Pattern pattern, final List<String> whitelist )
    {
        String replacedMessage = " " + message + " ";
        if ( whitelist != null )
        {
            for ( String whitelistedWord : whitelist )
            {
                replacedMessage = replacedMessage.replace( " " + whitelistedWord + " ", "" );
            }
        }
        final Matcher matcher = pattern.matcher( replacedMessage );

        if ( matcher.find() )
        {
            final String word = matcher.group();

            return new SwearValidationResult( false, word, message.replace( word, replaceWith ) );
        }
        return new SwearValidationResult( true, null, message );
    }

    @Data
    private static class SwearPattern
    {
        private final Pattern pattern;
        private final List<String> whitelist;
    }
}
