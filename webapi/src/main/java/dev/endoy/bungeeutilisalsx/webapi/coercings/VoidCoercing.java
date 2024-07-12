package dev.endoy.bungeeutilisalsx.webapi.coercings;

import graphql.schema.Coercing;
import org.springframework.stereotype.Component;

@Component
public class VoidCoercing implements Coercing<Void, String>
{

    public String serialize( Object input )
    {
        return null;
    }

    public Void parseValue( Object input )
    {
        return null;
    }

    public Void parseLiteral( Object input )
    {
        return null;
    }
}
