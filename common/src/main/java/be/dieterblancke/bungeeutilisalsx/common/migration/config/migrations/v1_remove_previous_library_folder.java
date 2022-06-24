package be.dieterblancke.bungeeutilisalsx.common.migration.config.migrations;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.migration.ConfigMigration;
import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;

import java.io.File;

public class v1_remove_previous_library_folder implements ConfigMigration
{

    private final File librariesFolder;

    public v1_remove_previous_library_folder()
    {
        this.librariesFolder = new File( BuX.getInstance().getDataFolder(), "libraries" );
    }

    @Override
    public boolean shouldRun() throws Exception
    {
        return librariesFolder.exists();
    }

    @Override
    public void migrate() throws Exception
    {
        MoreFiles.deleteRecursively( librariesFolder.toPath(), RecursiveDeleteOption.ALLOW_INSECURE );
    }
}
