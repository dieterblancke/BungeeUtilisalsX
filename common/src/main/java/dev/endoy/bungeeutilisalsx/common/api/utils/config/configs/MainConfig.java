package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import lombok.Getter;

public class MainConfig extends Config
{
    @Getter
    private boolean debug;

    public MainConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.debug = false;
    }

    @Override
    protected void setup()
    {
        if ( config == null )
        {
            return;
        }

        this.debug = config.getBoolean( "debug", false );
    }
}
