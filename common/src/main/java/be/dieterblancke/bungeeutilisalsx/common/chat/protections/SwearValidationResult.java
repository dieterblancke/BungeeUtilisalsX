package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.chat.ChatValidationResult;
import lombok.Data;

@Data
public class SwearValidationResult extends ChatValidationResult
{

    private final String resultMessage;

    public SwearValidationResult( final boolean result, final String resultMessage )
    {
        super( result );
        this.resultMessage = resultMessage;
    }
}
