package be.dieterblancke.bungeeutilisalsx.common.migration;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration
{

    boolean shouldRun( final Connection connection ) throws SQLException;

    void migrate( final Connection connection ) throws SQLException;

}
