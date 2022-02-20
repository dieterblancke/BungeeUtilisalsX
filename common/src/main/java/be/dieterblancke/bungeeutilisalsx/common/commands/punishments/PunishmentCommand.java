package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent.PunishmentRemovalAction;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserKickJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserMuteJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserUnmuteJob;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.List;

public abstract class PunishmentCommand implements CommandCall
{

    private final String messagesPath;
    private final boolean withTime;

    public PunishmentCommand( final String messagesPath, final boolean withTime )
    {
        this.messagesPath = messagesPath;
        this.withTime = withTime;
    }

    public abstract void onPunishmentExecute( User user, List<String> args, List<String> parameters, PunishmentArgs punishmentArgs );

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentArgs punishmentArgs = loadArguments( user, args, withTime );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( messagesPath + ".usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( punishmentArgs.isSelfPunishment() )
        {
            user.sendLangMessage( "punishments.self-punishment" );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        if ( punishmentArgs.isHigherPunishment() )
        {
            user.sendLangMessage( "punishments.higher-punishment" );
            return;
        }

        this.onPunishmentExecute( user, args, parameters, punishmentArgs );
    }

    protected boolean useServerPunishments()
    {
        return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "per-server-punishments" );
    }

    protected Dao dao()
    {
        return BuX.getApi().getStorageManager().getDao();
    }

    protected PunishmentRemovalArgs loadRemovalArguments( final User user, final List<String> args )
    {
        if ( useServerPunishments() )
        {
            if ( args.size() < 2 )
            {
                return null;
            }
        }
        else
        {
            if ( args.size() < 1 )
            {
                return null;
            }
        }
        final PunishmentRemovalArgs punishmentRemovalArgs = new PunishmentRemovalArgs();

        punishmentRemovalArgs.setExecutor( user );
        punishmentRemovalArgs.setPlayer( args.get( 0 ) );

        if ( useServerPunishments() )
        {
            punishmentRemovalArgs.setServer( args.get( 1 ) );
        }

        return punishmentRemovalArgs;
    }

    protected PunishmentArgs loadArguments( final User user, final List<String> args, final boolean withTime )
    {
        if ( withTime )
        {
            if ( useServerPunishments() )
            {
                if ( args.size() < 4 )
                {
                    return null;
                }
            }
            if ( args.size() < 3 )
            {
                return null;
            }
        }
        else
        {
            if ( useServerPunishments() )
            {
                if ( args.size() < 3 )
                {
                    return null;
                }
            }
            if ( args.size() < 2 )
            {
                return null;
            }
        }
        final PunishmentArgs punishmentArgs = new PunishmentArgs();

        punishmentArgs.setExecutor( user );
        punishmentArgs.setPlayer( args.get( 0 ) );

        if ( withTime )
        {
            if ( useServerPunishments() )
            {
                punishmentArgs.setTime( args.get( 1 ) );
                punishmentArgs.setServer( args.get( 2 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 3, args.size() ), " " ) );
            }
            else
            {
                punishmentArgs.setTime( args.get( 1 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 2, args.size() ), " " ) );
            }
        }
        else
        {
            if ( useServerPunishments() )
            {
                punishmentArgs.setServer( args.get( 1 ) );
                punishmentArgs.setReason( Utils.formatList( args.subList( 2, args.size() ), " " ) );
            }
            else
            {
                punishmentArgs.setReason( Utils.formatList( args.subList( 1, args.size() ), " " ) );
            }
        }

        return punishmentArgs;
    }

    protected void attemptKick( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        BuX.getInstance().getJobManager().executeJob( new UserKickJob(
                storage.getUuid(),
                storage.getUserName(),
                info.getType().isIP(),
                storage.getIp(),
                path,
                BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray(),
                info.getType(),
                info.getReason()
        ) );
    }

    protected void attemptMute( final UserStorage storage, final String path, final PunishmentInfo info )
    {
        BuX.getInstance().getJobManager().executeJob( new UserMuteJob(
                storage.getUuid(),
                storage.getUserName(),
                info.getType().isIP(),
                storage.getIp(),
                path,
                BuX.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray(),
                info.getType(),
                info.getReason()
        ) );
    }

    @Data
    public class PunishmentArgs
    {

        private User executor;
        private String player;
        private long time;
        private String server;
        private String reason;

        private UserStorage storage;

        public void setTime( final String time )
        {
            this.time = Utils.parseDateDiff( time );
        }

        public boolean hasJoined()
        {
            return getStorage() != null && getStorage().isLoaded();
        }

        public UserStorage getStorage()
        {
            if ( storage == null )
            {
                return storage = dao().getUserDao().getUserData( player ).join().orElse( null );
            }
            return storage;
        }

        public String getServerOrAll()
        {
            return server == null ? "ALL" : server;
        }

        public boolean launchEvent( final PunishmentType type )
        {
            final UserPunishEvent event = new UserPunishEvent(
                    type,
                    executor,
                    storage.getUuid(),
                    storage.getUserName(),
                    storage.getIp(),
                    reason,
                    useServerPunishments() ? server : "ALL",
                    time
            );
            BuX.getApi().getEventLoader().launchEvent( event );

            if ( event.isCancelled() )
            {
                executor.sendLangMessage( "punishments.cancelled" );
                return true;
            }
            return false;
        }

        public void launchPunishmentFinishEvent( final PunishmentType type )
        {
            final UserPunishmentFinishEvent event = new UserPunishmentFinishEvent(
                    type,
                    executor,
                    storage.getUuid(),
                    storage.getUserName(),
                    storage.getIp(),
                    reason,
                    this.getServerOrAll(),
                    time
            );

            BuX.getApi().getEventLoader().launchEvent( event );
        }

        public boolean isSelfPunishment()
        {
            return player.equalsIgnoreCase( executor.getName() )
                    && ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "allow-self-punishments" );
        }

        public boolean isHigherPunishment()
        {
            return BuX.getApi().getPunishmentExecutor().isHigherPunishment( executor.getUuid(), this.getStorage().getUuid() );
        }
    }

    @Data
    public class PunishmentRemovalArgs
    {

        private User executor;
        private String player;
        private String server;

        private UserStorage storage;

        public boolean hasJoined()
        {
            return getStorage() != null && getStorage().isLoaded();
        }

        public UserStorage getStorage()
        {
            if ( storage == null )
            {
                return storage = dao().getUserDao().getUserData( player ).join().orElse( null );
            }
            return storage;
        }

        public String getServerOrAll()
        {
            if ( !useServerPunishments() )
            {
                return "ALL";
            }
            return server == null ? "ALL" : server;
        }

        public void removeCachedMute()
        {
            final UserStorage storage = getStorage();

            BuX.getInstance().getJobManager().executeJob( new UserUnmuteJob(
                    storage.getUuid(),
                    storage.getUserName(),
                    this.getServerOrAll()
            ) );
        }

        public boolean launchEvent( final PunishmentRemovalAction type )
        {
            final UserPunishRemoveEvent event = new UserPunishRemoveEvent(
                    type,
                    executor,
                    storage.getUuid(),
                    storage.getUserName(),
                    storage.getIp(),
                    useServerPunishments() ? server : "ALL"
            );

            BuX.getApi().getEventLoader().launchEvent( event );

            if ( event.isCancelled() )
            {
                executor.sendLangMessage( "punishments.cancelled" );
                return true;
            }
            return false;
        }
    }
}
