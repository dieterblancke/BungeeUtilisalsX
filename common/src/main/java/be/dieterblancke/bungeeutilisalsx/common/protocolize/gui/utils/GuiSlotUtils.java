package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.utils;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config.GuiConfig;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GuiSlotUtils
{

    private GuiSlotUtils()
    {
        // do nothing
    }

    public static Collection<Integer> formatSlots( final GuiConfig guiConfig, final String slots )
    {
        final Set<Integer> slotList = new HashSet<>();

        if ( slots.equalsIgnoreCase( "borders" ) )
        {
            slotList.addAll( getBorders( guiConfig ) );
        }
        else if ( slots.contains( ".." ) )
        {
            slotList.addAll( getSlotRanges( guiConfig, slots ) );
        }
        else
        {
            slotList.addAll( getSlots( guiConfig, slots ) );
        }

        return slotList;
    }

    private static Collection<Integer> getBorders( final GuiConfig config )
    {
        final Set<Integer> slots = new HashSet<>();

        for ( int row = 0; row < config.getRows(); row++ )
        {
            final int firstRowSlot = row * 9;

            for ( int i = firstRowSlot; i < firstRowSlot + 9; i++ )
            {
                if ( row == 0 || row == config.getRows() - 1 || i == firstRowSlot || i == firstRowSlot + 8 )
                {
                    slots.add( i );
                }
            }
        }
        return slots;
    }

    private static Collection<Integer> getSlotRanges( final GuiConfig config, final String slotRanges )
    {
        final Set<Integer> slots = new HashSet<>();

        for ( String range : slotRanges.split( "," ) )
        {
            if ( range.contains( ".." ) )
            {
                final int begin = Integer.parseInt( range.split( "\\.\\." )[0] );
                final int end = Integer.parseInt( range.split( "\\.\\." )[1] );

                for ( int i = begin; i <= end; i++ )
                {
                    slots.add( i );
                }
            }
        }

        return slots;
    }

    private static Collection<Integer> getSlots( final GuiConfig config, final String slotsStr )
    {
        final Set<Integer> slots = new HashSet<>();

        for ( String slot : slotsStr.split( "," ) )
        {
            try
            {
                slots.add( Integer.parseInt( slot.trim() ) );
            }
            catch ( NumberFormatException e )
            {
                e.printStackTrace();
            }
        }

        return slots;
    }
}
