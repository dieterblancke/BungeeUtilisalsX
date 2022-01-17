package be.dieterblancke.bungeeutilisalsx.common.chat.protections;

import be.dieterblancke.bungeeutilisalsx.common.chat.ChatValidationResult;
import lombok.Getter;

@Getter
public class SwearValidationResult extends ChatValidationResult
{

    private final String swearWord;
    private final String resultMessage;

    public SwearValidationResult( final boolean result, final String swearWord, final String resultMessage )
    {
        super( result );
        this.swearWord = swearWord;
        this.resultMessage = resultMessage;
    }
}
