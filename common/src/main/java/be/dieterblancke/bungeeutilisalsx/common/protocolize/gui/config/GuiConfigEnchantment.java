package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config;

import be.dieterblancke.configuration.api.ISection;
import com.google.common.base.Strings;
import dev.simplix.protocolize.api.item.ItemStack;
import lombok.Data;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;

@Data
public class GuiConfigEnchantment
{

    private final String enchantment;
    private final int level;

    public GuiConfigEnchantment( final ISection section )
    {
        this.enchantment = section.exists( "enchantment" ) ? section.getString( "enchantment" ) : null;
        this.level = section.exists( "level" ) ? section.getInteger( "level" ) : 1;
    }

    public void addToItem( final ItemStack itemStack )
    {
        if ( Strings.isNullOrEmpty( enchantment ) || level < 0 )
        {
            return;
        }

        final CompoundTag compoundTag = itemStack.nbtData();
        final ListTag<CompoundTag> enchants = compoundTag.containsKey( "ench" )
                ? (ListTag<CompoundTag>) compoundTag.getListTag( "ench" )
                : new ListTag<>( CompoundTag.class );

        final CompoundTag enchant = new CompoundTag();
        enchant.putString( "id", this.enchantment );
        enchant.putInt( "lvl", this.level );
        enchants.add( enchant );

        compoundTag.put( "ench", enchants );
    }
}
