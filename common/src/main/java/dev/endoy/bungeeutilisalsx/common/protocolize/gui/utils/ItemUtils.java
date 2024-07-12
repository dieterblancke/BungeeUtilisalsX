package dev.endoy.bungeeutilisalsx.common.protocolize.gui.utils;

import dev.simplix.protocolize.api.item.ItemStack;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

public class ItemUtils
{

    public static void setSkullTexture( final ItemStack itemStack, final String textureUrl )
    {
        final CompoundTag skullOwner = new CompoundTag();

        skullOwner.putString( "Name", textureUrl );
        final CompoundTag properties = new CompoundTag();

        final ListTag<CompoundTag> textures = new ListTag<>( CompoundTag.class );
        final CompoundTag texture = new CompoundTag();

        texture.putString( "Value", textureUrl );
        textures.add( texture );

        properties.put( "textures", textures );
        skullOwner.put( "Properties", properties );
        itemStack.nbtData().put( "SkullOwner", skullOwner );
    }
}
