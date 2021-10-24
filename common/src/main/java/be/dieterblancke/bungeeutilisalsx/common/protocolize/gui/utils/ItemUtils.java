package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.utils;

import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

import java.util.UUID;

public class ItemUtils
{

    public static void setSkullTexture( final ItemStack itemStack, final String texture )
    {
        final CompoundTag compoundTag = itemStack.nbtData();
        final CompoundTag skull = new CompoundTag();
        final CompoundTag skullProperties = new CompoundTag();
        final ListTag<CompoundTag> skullTextures = new ListTag<>( CompoundTag.class );
        final CompoundTag textureTag = new CompoundTag();

        textureTag.putString( "Value", texture );
        skullTextures.add( textureTag );
        skullProperties.put( "textures", skullTextures );

        skull.putString( "Id", UUID.randomUUID().toString() );
        skull.put( "Properties", skullProperties );
        compoundTag.put( "SkullOwner", skull );
    }
}
