package be.dieterblancke.bungeeutilisalsx.common.api.user;

import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStorage implements HasMessagePlaceholders
{

    private UUID uuid;
    private String userName;
    private String ip;
    private Language language;
    private Date firstLogin;
    private Date lastLogout;
    private List<String> ignoredUsers = Lists.newArrayList();
    private String joinedHost;

    private Map<String, Object> data = Maps.newHashMap();

    public boolean isLoaded()
    {
        return uuid != null && userName != null && ip != null && language != null && firstLogin != null && lastLogout != null;
    }

    public <T> T getData( final String key )
    {
        return !data.containsKey( key ) ? null : (T) data.get( key );
    }

    public <T> T getDataOrPut( final String key, Supplier<T> supplier )
    {
        if ( !this.hasData( key ) )
        {
            this.setData( key, supplier.get() );
        }
        return this.getData( key );
    }

    public void setData( final String key, final Object value )
    {
        data.put( key, value );
    }

    public boolean hasData( final String key )
    {
        return data.containsKey( key );
    }

    public void removeData( final String key )
    {
        this.data.remove( key );
    }

    public <T> T getData( final UserStorageKey key )
    {
        return this.getData( key.toString() );
    }

    public <T> T getDataOrPut( final UserStorageKey key, Supplier<T> supplier )
    {
        return this.getDataOrPut( key.toString(), supplier );
    }

    public void setData( final UserStorageKey key, final Object value )
    {
        this.setData( key.toString(), value );
    }

    public boolean hasData( final UserStorageKey key )
    {
        return this.hasData( key.toString() );
    }

    public void removeData( final UserStorageKey key )
    {
        this.removeData( key.toString() );
    }

    @Override
    public MessagePlaceholders getMessagePlaceholders()
    {
        return MessagePlaceholders.create()
                .append( "uuid", uuid )
                .append( "user", userName )
                .append( "ip", ip )
                .append( "language", Optional.ofNullable( language ).map( Language::getName ).orElse( "" ) );
    }
}