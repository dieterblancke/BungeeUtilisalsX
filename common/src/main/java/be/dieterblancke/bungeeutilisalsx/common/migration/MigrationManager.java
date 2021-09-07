package be.dieterblancke.bungeeutilisalsx.common.migration;

public interface MigrationManager
{

    void initialize();

    void migrate() throws Exception;

}
