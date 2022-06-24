package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatValidationResult;
import be.dieterblancke.configuration.api.IConfiguration;

public class CapsChatProtection implements ChatProtection
{

    private boolean enabled;
    private String bypassPermission;
    private int minMessageLength;
    private String characters;
    private double percentage;

    @Override
    public void reload()
    {
        final IConfiguration config = ConfigFiles.ANTICAPS.getConfig();

        this.enabled = config.getBoolean( "enabled" );
        this.bypassPermission = config.getString( "bypass" );
        this.minMessageLength = config.getInteger( "min-length" );
        this.characters = config.getString( "characters" );
        this.percentage = config.getDouble( "percentage" );
    }

    @Override
    public ChatValidationResult validateMessage( final User user, final String message )
    {
        if ( !enabled || user.hasPermission( bypassPermission ) || message.length() < minMessageLength )
        {
            return ChatValidationResult.VALID;
        }

        double upperCase = 0.0D;
        for ( int i = 0; i < message.length(); i++ )
        {
            if ( characters.contains( message.substring( i, i + 1 ) ) )
            {
                upperCase += 1.0D;
            }
        }

        return ( upperCase / message.length() ) > ( percentage / 100 ) ? ChatValidationResult.INVALID : ChatValidationResult.VALID;
    }
}
