package be.dieterblancke.bungeeutilisalsx.common.protocolize.gui;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
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
