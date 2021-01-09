package be.dieterblancke.bungeeutilisalsx.common.chat;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

public interface ChatProtection
{

    void reload();

    <T extends ChatValidationResult> T validateMessage( User user, String message );

}
