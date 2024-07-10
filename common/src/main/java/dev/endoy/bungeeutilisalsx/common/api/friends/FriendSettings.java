package dev.endoy.bungeeutilisalsx.common.api.friends;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FriendSettings
{

    private final Map<FriendSetting, Boolean> settings;

    public FriendSettings()
    {
        this( new HashMap<>() );
    }

    public FriendSettings( final Map<FriendSetting, Boolean> settings )
    {
        this.settings = settings;
    }

    public void set( final FriendSetting key, final boolean value )
    {
        settings.put( key, value );
    }

    public boolean getSetting( final FriendSetting key, final boolean def )
    {
        return settings.getOrDefault( key, def );
    }

    public boolean getSetting( final FriendSetting key )
    {
        return this.getSetting( key, key.getDefault() );
    }
}
