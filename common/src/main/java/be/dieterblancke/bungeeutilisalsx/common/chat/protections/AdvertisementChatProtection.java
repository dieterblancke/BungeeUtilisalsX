package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatValidationResult;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AdvertisementChatProtection implements ChatProtection
{

    private boolean enabled;
    private List<String> allowedAddresses;
    private String bypassPermission;
    private List<Pattern> patterns;

    @Override
    public void reload()
    {
        this.enabled = ConfigFiles.ANTIAD.isEnabled();
        this.allowedAddresses = new ArrayList<>( ConfigFiles.ANTIAD.getConfig().getStringList( "allowed" ) );
        this.bypassPermission = ConfigFiles.ANTIAD.getConfig().getString( "bypass" );
        this.patterns = ConfigFiles.ANTIAD.getConfig().getStringList( "patterns" )
                .stream()
                .map( Pattern::compile )
                .collect( Collectors.toList() );
    }

    @Override
    public ChatValidationResult validateMessage( final User user, String message )
    {
        if ( !enabled || user.hasPermission( bypassPermission ) )
        {
            return ChatValidationResult.VALID;
        }

        message = Normalizer.normalize( message, Normalizer.Form.NFKD );
        message = message.replaceAll( "[^\\x00-\\x7F]", "" );
        message = message.replace( ",", "." );

        for ( String word : message.split( " " ) )
        {
            if ( this.allowedAddresses.contains( word.toLowerCase() ) )
            {
                continue;
            }
            for ( Pattern pattern : this.patterns )
            {
                if ( pattern.matcher( word ).find() )
                {
                    return ChatValidationResult.INVALID;
                }
            }
        }
        return ChatValidationResult.VALID;
    }
}
