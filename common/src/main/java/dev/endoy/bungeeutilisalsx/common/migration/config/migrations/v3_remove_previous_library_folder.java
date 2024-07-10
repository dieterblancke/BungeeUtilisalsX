package dev.endoy.bungeeutilisalsx.common.migration.config.migrations;

import com.google.common.io.MoreFiles;
import com.google.common.io.RecursiveDeleteOption;
import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.migration.ConfigMigration;

import java.io.File;

public class v3_remove_previous_library_folder implements ConfigMigration
{

    private final File librariesFolder;

    public v3_remove_previous_library_folder()
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
