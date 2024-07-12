package dev.endoy.bungeeutilisalsx.common.motd;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

@Data
@AllArgsConstructor
public abstract class ConditionHandler
{

    protected String condition;
    protected ConditionOperator operator;
    protected String value;

    public ConditionHandler( final String condition )
    {
        this.condition = condition.trim();

        final String[] arguments = condition.split( " " );
        this.operator = ConditionOperator.detectOperator( arguments[0] );
        this.value = String.join( " ", Arrays.copyOfRange( arguments, 1, arguments.length ) );
    }

    public abstract boolean checkCondition( MotdConnection connection );

}
