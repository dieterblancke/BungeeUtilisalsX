package dev.endoy.bungeeutilisalsx.common.protocolize.gui;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.Data;

@Data
public abstract class GuiOpener
{

    private final String name;

    public abstract void openGui( User user, String[] args );

    public void reload()
    {
    }
}
