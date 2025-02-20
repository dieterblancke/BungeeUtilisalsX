package dev.endoy.bungeeutilisalsx.common.api.job.jobs;

import dev.endoy.bungeeutilisalsx.common.api.job.MultiProxyJob;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BroadcastLanguageMessageJob implements MultiProxyJob
{

    private final String languagePath;
    private final String permission;
    private final MessagePlaceholders placeholders;
    @Nullable
    private final List<String> disabledServers;

    public BroadcastLanguageMessageJob( final String languagePath, final String permission, final MessagePlaceholders placeholders )
    {
        this( languagePath, permission, placeholders, new ArrayList<>() );
    }

    public BroadcastLanguageMessageJob( final String languagePath, final String permission, final MessagePlaceholders placeholders, final List<String> disabledServers )
    {
        this.languagePath = languagePath;
        this.permission = permission;
        this.placeholders = placeholders;
        this.disabledServers = disabledServers;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
