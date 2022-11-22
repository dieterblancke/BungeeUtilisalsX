package be.dieterblancke.bungeeutilisalsx.common.api.job.jobs;

import be.dieterblancke.bungeeutilisalsx.common.api.job.MultiProxyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BroadcastLanguageMessageJob implements MultiProxyJob
{

    private final String languagePath;
    private final String permission;
    private final MessagePlaceholders placeholders;

    public BroadcastLanguageMessageJob( final String languagePath, final String permission, final MessagePlaceholders placeholders )
    {
        this.languagePath = languagePath;
        this.permission = permission;
        this.placeholders = placeholders;
    }

    @Override
    public boolean isAsync()
    {
        return true;
    }
}
