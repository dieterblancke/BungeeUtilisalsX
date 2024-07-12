package dev.endoy.bungeeutilisalsx.spigot;

import dev.endoy.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.IBuXApi;
import dev.endoy.bungeeutilisalsx.common.IPluginDescription;
import dev.endoy.bungeeutilisalsx.common.ServerOperationsApi;
import dev.endoy.bungeeutilisalsx.common.api.utils.Platform;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import dev.endoy.bungeeutilisalsx.common.commands.CommandManager;
import dev.endoy.bungeeutilisalsx.spigot.command.SpigotCommandManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final ServerOperationsApi serverOperationsApi = new SpigotOperationsApi();
    private final CommandManager commandManager = new SpigotCommandManager();
    private final IPluginDescription pluginDescription = new SpigotPluginDescription();
    private final List<StaffUser> staffMembers = new ArrayList<>();

    @Override
    protected IBuXApi createBuXApi()
    {
        // TODO
        return null;
    }

    @Override
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    @Override
    protected void registerListeners()
    {
        // TODO
    }

    @Override
    public ServerOperationsApi serverOperations()
    {
        return serverOperationsApi;
    }

    @Override
    public File getDataFolder()
    {
        return Bootstrap.getInstance().getDataFolder();
    }

    @Override
    public String getVersion()
    {
        return Bootstrap.getInstance().getDescription().getVersion();
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return staffMembers;
    }

    @Override
    public IPluginDescription getDescription()
    {
        return pluginDescription;
    }

    @Override
    public Logger getLogger()
    {
        return Bootstrap.getInstance().getLogger();
    }

    @Override
    public Platform getPlatform()
    {
        return Platform.SPIGOT;
    }

    @Override
    protected void loadConfigs()
    {
        // only load needed configs

    }

    @Override
    protected void registerMetrics()
    {
        // do nothing
    }
}
