package be.dieterblancke.bungeeutilisalsx.common.migration;

public interface Migration
{

    boolean shouldRun() throws Exception;

    void migrate() throws Exception;

}
