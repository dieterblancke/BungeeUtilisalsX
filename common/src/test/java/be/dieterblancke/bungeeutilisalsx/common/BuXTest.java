package be.dieterblancke.bungeeutilisalsx.common;

import java.sql.SQLException;

public class BuXTest
{

    protected AbstractBungeeUtilisalsX bungeeUtilisalsX;

    public BuXTest( final boolean shouldInitialize )
    {
        bungeeUtilisalsX = new TestBungeeUtilisalsX();

        if ( shouldInitialize )
        {
            bungeeUtilisalsX.initialize();
        }
    }

    public void shutdown() throws SQLException
    {
        bungeeUtilisalsX.getAbstractStorageManager().close();
    }
}
