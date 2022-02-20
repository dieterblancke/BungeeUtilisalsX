package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtection;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatValidationResult;
import be.dieterblancke.configuration.api.IConfiguration;

public class SpamChatProtection implements ChatProtection
{

    private boolean enabled;
    private String bypassPermission;
    private TimeUnit delayUnit;
    private int delayTime;

    @Override
    public void reload()
    {
        final IConfiguration config = ConfigFiles.ANTISPAM.getConfig();

        this.enabled = config.getBoolean( "enabled" );
        this.bypassPermission = config.getString( "bypass" );
        this.delayUnit = TimeUnit.valueOf( config.getString( "delay.unit" ).toUpperCase() );
        this.delayTime = config.getInteger( "delay.time" );
    }

    @Override
    public ChatValidationResult validateMessage( final User user, final String message )
    {
        if ( !enabled || user.hasPermission( bypassPermission ) )
        {
            return ChatValidationResult.VALID;
        }
        if ( !user.getCooldowns().canUse( "CHATSPAM" ) )
        {
            return ChatValidationResult.INVALID;
        }
        user.getCooldowns().updateTime( "CHATSPAM", delayUnit, delayTime );
        return ChatValidationResult.VALID;
    }
}
