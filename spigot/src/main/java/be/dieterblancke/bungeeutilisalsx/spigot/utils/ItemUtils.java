package be.dieterblancke.bungeeutilisalsx.spigot.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.UUID;

public class ItemUtils
{

    public static void setSkullTexture( final ItemMeta itemMeta, String url )
    {
        if ( !( itemMeta instanceof SkullMeta ) )
        {
            return;
        }

        final GameProfile profile = new GameProfile( UUID.randomUUID(), null );
        final PropertyMap propertyMap = profile.getProperties();
        if ( propertyMap == null )
        {
            throw new IllegalStateException( "Profile doesn't contain a property map" );
        }

        if ( !url.startsWith( "https://textures.minecraft.net/" ) )
        {
            url = "https://textures.minecraft.net/texture/" + url;
        }

        final String encodedData = Base64.getEncoder().encodeToString( String.format( "{textures:{SKIN:{url:\"%s\"}}}", url ).getBytes() );
        propertyMap.put( "textures", new Property( "textures", encodedData ) );

        try
        {
            final Method method = itemMeta.getClass().getDeclaredMethod( "setProfile", GameProfile.class );

            method.setAccessible( true );
            method.invoke( itemMeta, profile );
        }
        catch ( NoSuchMethodException e )
        {
            try
            {
                final Field field = itemMeta.getClass().getDeclaredField( "profile" );
                field.setAccessible( true );
                field.set( itemMeta, profile );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }
        }
        catch ( IllegalAccessException | InvocationTargetException e )
        {
            e.printStackTrace();
        }
    }
}
