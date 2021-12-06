package be.dieterblancke.bungeeutilisalsx.common.motd;

import lombok.Getter;

public enum ConditionOperator
{

    EQ( "==" ),
    NOT_EQ( "!=" ),
    LT( "<" ),
    LTE( "<=" ),
    GT( ">" ),
    GTE( ">=" );

    @Getter
    private final String operator;

    ConditionOperator( final String operator )
    {
        this.operator = operator;
    }

    public static ConditionOperator detectOperator( final String operator )
    {
        for ( ConditionOperator conditionOperator : values() )
        {
            if ( conditionOperator.getOperator().equals( operator ) )
            {
                return conditionOperator;
            }
        }
        return EQ;
    }
}
