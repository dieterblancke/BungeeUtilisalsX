package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class PunishmentHistoryCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "punishments.punishmenthistory.usage" );
            return;
        }

        final Dao dao = BuX.getApi().getStorageManager().getDao();
        final String username = args.get( 0 );

        dao.getUserDao().getUserData( username ).thenAccept( optionalStorage ->
        {
            final UserStorage storage = optionalStorage.orElse( null );
            if ( storage == null || !storage.isLoaded() )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            final String action = args.size() > 1 ? args.get( 1 ) : "all";
            final List<CompletableFuture<List<PunishmentInfo>>> allTasks = listPunishments( storage, action );

            CompletableFuture.allOf( allTasks.toArray( new CompletableFuture[allTasks.size()] ) )
                    .thenRun( () ->
                    {
                        final List<PunishmentInfo> allPunishments = allTasks
                                .stream()
                                .map( CompletableFuture::join )
                                .flatMap( Collection::stream )
                                .toList();


                        if ( allPunishments.isEmpty() )
                        {
                            user.sendLangMessage(
                                    "punishments.punishmenthistory.no-punishments",
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
                                .append( "{previousPage}", previous )
                                .append( "{currentPage}", page )
                                .append( "{nextPage}", next )
                                .append( "{maxPages}", pages )
                                .append( "{type}", action )
                                .append( "{punishmentAmount}", allPunishments.size() )
                                .append( "{user}", username );
                        List<PunishmentInfo> punishments = allPunishments.subList( minNumber, maxNumber );
                        user.sendLangMessage( "punishments.punishmenthistory.head", messagePlaceholders );

                        punishments.forEach( punishment ->
                                user.sendLangMessage(
                                        "punishments.punishmenthistory.format",
                                        BuX.getApi().getPunishmentExecutor().getPlaceHolders( punishment )
                                )
                        );
                        user.sendLangMessage( "punishments.punishmenthistory.foot", messagePlaceholders );
                    } );
        } );
    }

    @Override
    public String getDescription()
    {
        return "Shows you the punishment history for a specific user.";
    }

    @Override
    public String getUsage()
    {
        return "/punishmenthistory (user) [type / all] [page]";
    }

    private List<CompletableFuture<List<PunishmentInfo>>> listPunishments( final UserStorage storage, final String action )
    {
        final List<CompletableFuture<List<PunishmentInfo>>> list = Lists.newArrayList();

        if ( action.equalsIgnoreCase( "all" ) )
        {
            for ( PunishmentType type : PunishmentType.values() )
            {
                list.add( listPunishments( storage, type ) );
            }
        }
        else
        {
            for ( String typeStr : action.split( "," ) )
            {
                final PunishmentType type = Utils.valueOfOr( typeStr.toUpperCase(), PunishmentType.BAN );

                list.add( listPunishments( storage, type ) );
            }
        }
        return list;
    }

    private CompletableFuture<List<PunishmentInfo>> listPunishments( final UserStorage storage, final PunishmentType type )
    {
        Predicate<PunishmentInfo> permanentFilter = punishment -> !punishment.isTemporary();
        Predicate<PunishmentInfo> temporaryFilter = PunishmentInfo::isTemporary;
        PunishmentDao punishmentDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();

        return switch ( type )
                {
                    case BAN -> punishmentDao.getBansDao().getBans( storage.getUuid() ).thenApply( it -> it.stream().filter( permanentFilter ).toList() );
                    case TEMPBAN -> punishmentDao.getBansDao().getBans( storage.getUuid() ).thenApply( it -> it.stream().filter( temporaryFilter ).toList() );
                    case IPBAN -> punishmentDao.getBansDao().getIPBans( storage.getIp() ).thenApply( it -> it.stream().filter( permanentFilter ).toList() );
                    case IPTEMPBAN -> punishmentDao.getBansDao().getIPBans( storage.getIp() ).thenApply( it -> it.stream().filter( temporaryFilter ).toList() );
                    case MUTE -> punishmentDao.getMutesDao().getMutes( storage.getUuid() ).thenApply( it -> it.stream().filter( permanentFilter ).toList() );
                    case TEMPMUTE -> punishmentDao.getMutesDao().getMutes( storage.getUuid() ).thenApply( it -> it.stream().filter( temporaryFilter ).toList() );
                    case IPMUTE -> punishmentDao.getMutesDao().getIPMutes( storage.getIp() ).thenApply( it -> it.stream().filter( permanentFilter ).toList() );
                    case IPTEMPMUTE -> punishmentDao.getMutesDao().getIPMutes( storage.getIp() ).thenApply( it -> it.stream().filter( temporaryFilter ).toList() );
                    case KICK -> punishmentDao.getKickAndWarnDao().getKicks( storage.getUuid() );
                    case WARN -> punishmentDao.getKickAndWarnDao().getWarns( storage.getUuid() );
                };
    }
}
