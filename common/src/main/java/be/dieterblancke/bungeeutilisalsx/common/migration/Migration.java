package be.dieterblancke.bungeeutilisalsx.common.migration;

import java.sql.Connection;
import java.sql.SQLException;

public interface Migration
{

    boolean shouldRun() throws Exception;

    void migrate() throws Exception;

}
