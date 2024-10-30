package dev.endoy.bungeeutilisalsx.common.commands.general;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.TabCall;
import dev.endoy.bungeeutilisalsx.common.api.language.Language;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return BuX.getApi().getLanguageManager().getLanguages().stream().map( Language::getName ).collect( Collectors.toList() );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final String languages = BuX.getApi().getLanguageManager().getLanguages().stream()
            .map( Language::getName )
            .collect( Collectors.joining( ", " ) );

        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.language.usage", MessagePlaceholders.create().append( "languages", languages ) );
            return;
        }
        final String langName = args.get( 0 );

        if ( user.getLanguage().getName().equalsIgnoreCase( langName ) )
        {
            user.sendLangMessage( "general-commands.language.already", MessagePlaceholders.create().append( "language", langName ) );
            return;
        }

        final Optional<Language> optional = BuX.getApi().getLanguageManager().getLanguage( langName );

        if ( optional.isPresent() )
        {
            final Language language = optional.get();

            user.setLanguage( language );
            BuX.getApi().getStorageManager().getDao().getUserDao().setLanguage( user.getUuid(), language );

            user.sendLangMessage( "general-commands.language.changed", MessagePlaceholders.create().append( "language", language.getName() ) );
        }
        else
        {
            user.sendLangMessage(
                "general-commands.language.notfound",
                MessagePlaceholders.create()
                    .append( "language", langName )
                    .append( "languages", languages )
            );
        }
    }

    @Override
    public String getDescription()
    {
        return "Changes your current language.";
    }

    @Override
    public String getUsage()
    {
        return "/language (language)";
    }
}
