package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PunishmentDataCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "punishments.punishmentdata.usage" );
            return;
        }

        final PunishmentType type = Utils.valueOfOr( args.get( 0 ).toUpperCase(), PunishmentType.BAN );
        final String id = args.get( 1 );

        if ( BuX.getApi().getStorageManager().getType().toString().contains( "SQL" ) && !MathUtils.isInteger( id ) )
        {
            user.sendLangMessage( "no-number" );
            return;
        }

        CompletableFuture<PunishmentInfo> completableFuture = null;
        switch ( type )
        {
            case BAN:
            case TEMPBAN:
            case IPBAN:
            case IPTEMPBAN:
                completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getById( id );

                if ( completableFuture == null )
                {
                    completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getByPunishmentId( id );
                }
                break;
            case MUTE:
            case TEMPMUTE:
            case IPMUTE:
            case IPTEMPMUTE:
                completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getById( id );

                if ( completableFuture == null )
                {
                    completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getByPunishmentId( id );
                }
                break;
            case KICK:
                completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKickById( id );
                break;
            case WARN:
                completableFuture = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarnById( id );
                break;
        }

        completableFuture.thenAccept( info ->
        {
            if ( info == null )
            {
                user.sendLangMessage(
                        "punishments.punishmentdata.not-found",
                        MessagePlaceholders.create()
                                .append( "type", type.toString().toLowerCase() )
                                .append( "id", id )
                );
            }
            else
            {
                user.sendLangMessage(
                        "punishments.punishmentdata.found",
                        BuX.getApi().getPunishmentExecutor().getPlaceHolders( info )
                );
            }
        } );
    }

    @Override
    public String getDescription()
    {
        return "Shows you data for a specific punishment type and id.";
    }

    @Override
    public String getUsage()
    {
        return "/punishmentdata (type) (id)";
    }
}
