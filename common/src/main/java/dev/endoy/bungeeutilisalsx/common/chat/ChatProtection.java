package dev.endoy.bungeeutilisalsx.common.chat;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;

public interface ChatProtection
{

    void reload();

    <T extends ChatValidationResult> T validateMessage( User user, String message );

}
