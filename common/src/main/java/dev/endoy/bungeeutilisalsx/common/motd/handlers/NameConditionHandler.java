package dev.endoy.bungeeutilisalsx.common.motd.handlers;

import dev.endoy.bungeeutilisalsx.common.motd.ConditionHandler;
import dev.endoy.bungeeutilisalsx.common.motd.ConditionOperator;
import dev.endoy.bungeeutilisalsx.common.motd.MotdConnection;

public class NameConditionHandler extends ConditionHandler
{

    public NameConditionHandler( String condition )
    {
        super( condition.replaceFirst( "name ", "" ) );
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        if ( operator == ConditionOperator.EQ )
        {
            if ( connection.getName() == null )
            {
                return value.equalsIgnoreCase( "null" );
            }
            return connection.getName().equalsIgnoreCase( value );
        }
        else if ( operator == ConditionOperator.NOT_EQ )
        {
            if ( connection.getName() == null )
            {
                return !value.equalsIgnoreCase( "null" );
            }
            return !connection.getName().equalsIgnoreCase( value );
        }

        return false;
    }
}