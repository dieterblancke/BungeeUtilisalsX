package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui.config;

import be.dieterblancke.configuration.api.ISection;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuiAction
{

    private final GuiActionType type;
    private final String action;
    private final ISection configSection;

    public GuiAction( final GuiActionType type, final String action )
    {
        this( type, action, null );
    }

    public boolean isSet()
    {
        return !action.trim().isEmpty();
    }
}
