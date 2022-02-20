package be.dieterblancke.bungeeutilisalsx.common.motd.handlers;

import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionHandler;
import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionOperator;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;

public class DomainConditionHandler extends ConditionHandler
{

    public DomainConditionHandler( String condition )
    {
        super( condition.replaceFirst( "domain ", "" ) );
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        if ( connection.getVirtualHost() == null || connection.getVirtualHost().getHostName() == null )
        {
            return false;
        }
        final String joinedHost = connection.getVirtualHost().getHostName();

        return operator == ConditionOperator.EQ && joinedHost.equalsIgnoreCase( value );
    }
}
