package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config;

import be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.utils.GuiSlotUtils;
import be.dieterblancke.configuration.api.ISection;
import lombok.Data;

import java.util.Collection;

@Data
public class GuiConfigItem
{

    private final Collection<Integer> slots;
    private final GuiAction action;
    private final GuiAction rightAction;
    private final GuiConfigItemStack item;
    private final String showIf;

    public GuiConfigItem( final GuiConfig guiConfig, final ISection section )
    {
        this.slots = GuiSlotUtils.formatSlots(
                guiConfig,
                section.isInteger( "slots" )
                        ? String.valueOf( section.getInteger( "slots" ) )
                        : section.getString( "slots" ).trim()
        );
        this.action = this.asAction( section, "action" );
        this.rightAction = this.asAction( section, "right-action" );
        this.item = section.exists( "item" ) ? new GuiConfigItemStack( section.getSection( "item" ) ) : null;
        this.showIf = section.exists( "show-if" ) ? section.getString( "show-if" ) : "";
    }

    private GuiAction asAction( final ISection section, final String key )
    {
        if ( section.exists( key ) )
        {
            if ( section.isSection( key ) )
            {
                final ISection actionSection = section.getSection( key );

                return new GuiAction(
                        GuiActionType.valueOf( actionSection.getString( "type" ).toUpperCase() ),
                        actionSection.getString( "action" ),
                        actionSection
                );
            }
            else
            {
                return new GuiAction( GuiActionType.COMMAND, section.getString( key ) );
            }
        }
        else
        {
            return new GuiAction( GuiActionType.COMMAND, "" );
        }
    }
}
