package be.dieterblancke.bungeeutilisalsx.common.commands.domains;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.UserUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DomainsListSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final Map<String, Integer> domains = new HashMap<>();
        final Map<Pattern, String> mappings = ConfigFiles.GENERALCOMMANDS.getConfig().getSectionList( "domains.mappings" )
                .stream()
                .collect( Collectors.toMap(
                        ( section ) -> Pattern.compile( section.getString( "regex" ) ),
                        ( section ) -> section.getString( "domain" )
                ) );

        BuX.getApi().getStorageManager().getDao().getUserDao().getJoinedHostList()
                .thenAccept( ( tempDomains ) ->
                {
                    final Map<String, Set<String>> domainLists = new HashMap<>();

                    tempDomains.forEach( ( domain, amount ) ->
                    {
                        for ( Map.Entry<Pattern, String> entry : mappings.entrySet() )
                        {
                            final Matcher matcher = entry.getKey().matcher( domain );

                            if ( matcher.find() )
                            {
                                this.addDomain( domains, domainLists, entry.getValue(), amount );
                                return;
                            }
                        }
                        this.addDomain( domains, domainLists, domain, amount );
                    } );

                    user.sendLangMessage( "general-commands.domains.list.header", "{total}", domains.size() );

                    domains.entrySet().stream()
                            .sorted( ( o1, o2 ) -> Integer.compare( o2.getValue(), o1.getValue() ) )
                            .forEach( entry ->
                                    user.sendLangMessage(
                                            "general-commands.domains.list.format",
                                            "{domain}", entry.getKey(),
                                            "{online}", UserUtils.getOnlinePlayersOnDomain( entry.getKey() ),
                                            "{total}", entry.getValue()
                                    )
                            );

                    user.sendLangMessage( "general-commands.domains.list.footer", "{total}", domains.size() );
                } );
    }

    @Override
    public String getDescription()
    {
        return "Lists all domains that have been used to join your network. This can also show how many people joined on the domain in total and how many are online right now.";
    }

    @Override
    public String getUsage()
    {
        return "/domains list";
    }

    private void addDomain( final Map<String, Integer> domains,
                            final Map<String, Set<String>> domainLists,
                            final String domain,
                            final int amount )
    {
        domains.compute( domain, ( key, value ) -> ( value == null ? 0 : value ) + amount );
        domainLists.compute( domain, ( key, value ) ->
        {
            if ( value == null )
            {
                value = new HashSet<>();
            }
            value.add( domain );

            return value;
        } );
    }
}
