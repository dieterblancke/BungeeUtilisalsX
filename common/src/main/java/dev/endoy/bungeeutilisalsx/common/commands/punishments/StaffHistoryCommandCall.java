package dev.endoy.bungeeutilisalsx.common.commands.punishments;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StaffHistoryCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.staffhistory.usage" );
            return;
        }

        final String username = args.get( 0 );
        final String action = args.size() > 1 ? args.get( 1 ) : "all";

        this.listPunishments( username, action ).thenAccept( allPunishments ->
        {
            if ( allPunishments.isEmpty() )
            {
                user.sendLangMessage(
                        "punishments.staffhistory.no-punishments",
                        MessagePlaceholders.create()
                                .append( "user", username )
                                .append( "type", action )
                );
                return;
            }
            int page = args.size() > 2 ? ( MathUtils.isInteger( args.get( 2 ) ) ? Integer.parseInt( args.get( 2 ) ) : 1 ) : 1;
            final int pages = (int) Math.ceil( (double) allPunishments.size() / 10 );

            if ( page > pages )
            {
                page = pages;
            }

            final int previous = page > 1 ? page - 1 : 1;
            final int next = Math.min( page + 1, pages );

            int maxNumber = page * 10;
            int minNumber = maxNumber - 10;

            if ( maxNumber > allPunishments.size() )
            {
                maxNumber = allPunishments.size();
            }

            MessagePlaceholders messagePlaceholders = MessagePlaceholders.create()
                    .append( "previousPage", previous )
                    .append( "currentPage", page )
                    .append( "nextPage", next )
                    .append( "maxPages", pages )
                    .append( "type", action )
                    .append( "user", username )
                    .append( "punishmentAmount", allPunishments.size() );

            List<PunishmentInfo> punishments = allPunishments.subList( minNumber, maxNumber );
            user.sendLangMessage( "punishments.staffhistory.head", messagePlaceholders );

            punishments.forEach( punishment ->
                    user.sendLangMessage(
                            "punishments.staffhistory.format",
                            BuX.getApi().getPunishmentExecutor().getPlaceHolders( punishment )
                    )
            );
            user.sendLangMessage( "punishments.staffhistory.foot", messagePlaceholders );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Shows you a list of punishments executed by this specific user.";
    }

    @Override
    public String getUsage()
    {
        return "/staffhistory (user) [type / all] [page]";
    }

    private CompletableFuture<List<PunishmentInfo>> listPunishments( final String name, final String action )
    {
        if ( action.equalsIgnoreCase( "all" ) )
        {
            return listAllPunishments( name );
        }
        else
        {
            final List<CompletableFuture<List<PunishmentInfo>>> tasks = Lists.newArrayList();

            for ( String typeStr : action.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( PunishmentType.class, typeStr.toUpperCase(), null );

                if ( type != null )
                {
                    tasks.add( listPunishments( name, type ) );
                }
            }

            return CompletableFuture.allOf( tasks.toArray( new CompletableFuture[tasks.size()] ) )
                    .thenApply( it -> tasks.stream()
                            .map( CompletableFuture::join )
                            .flatMap( List::stream )
                            .toList()
                    );
        }
    }

    private CompletableFuture<List<PunishmentInfo>> listAllPunishments( final String name )
    {
        final PunishmentDao punishmentDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();
        final List<CompletableFuture<List<PunishmentInfo>>> tasks = Lists.newArrayList();

        tasks.add( punishmentDao.getBansDao().getBansExecutedBy( name ) );
        tasks.add( punishmentDao.getMutesDao().getMutesExecutedBy( name ) );
        tasks.add( punishmentDao.getKickAndWarnDao().getWarnsExecutedBy( name ) );
        tasks.add( punishmentDao.getKickAndWarnDao().getKicksExecutedBy( name ) );

        return CompletableFuture.allOf( tasks.toArray( new CompletableFuture[tasks.size()] ) )
                .thenApply( it -> tasks.stream()
                        .map( CompletableFuture::join )
                        .flatMap( List::stream )
                        .toList()
                );
    }

    private CompletableFuture<List<PunishmentInfo>> listPunishments( final String name, final PunishmentType type )
    {
        final Predicate<PunishmentInfo> permanentFilter = punishment -> !punishment.isTemporary();
        final Predicate<PunishmentInfo> temporaryFilter = PunishmentInfo::isTemporary;
        final Predicate<PunishmentInfo> ipFilter = punishment -> punishment.toString().contains( "IP" );
        final Predicate<PunishmentInfo> notIpFilter = punishment -> !punishment.toString().contains( "IP" );

        final PunishmentDao punishmentDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();
        final CompletableFuture<List<PunishmentInfo>> completableFuture;
        if ( type.toString().contains( "BAN" ) )
        {
            completableFuture = punishmentDao.getBansDao().getBansExecutedBy( name );
        }
        else if ( type.toString().contains( "MUTE" ) )
        {
            completableFuture = punishmentDao.getMutesDao().getMutesExecutedBy( name );
        }
        else if ( type == PunishmentType.KICK )
        {
            completableFuture = punishmentDao.getKickAndWarnDao().getKicksExecutedBy( name );
        }
        else
        {
            completableFuture = punishmentDao.getKickAndWarnDao().getWarnsExecutedBy( name );
        }

        return completableFuture.thenApply( punishments ->
        {
            Stream<PunishmentInfo> stream = punishments.stream();

            if ( type.isActivatable() )
            {
                stream = type.isTemporary()
                        ? stream.filter( temporaryFilter )
                        : stream.filter( permanentFilter );
                stream = type.isIP()
                        ? stream.filter( ipFilter )
                        : stream.filter( notIpFilter );
            }

            return stream.toList();
        } );
    }
}
