package dev.endoy.bungeeutilisalsx.common.migration;

public interface MigrationManager
{

    void initialize();

    void migrate() throws Exception;

}
