package be.dieterblancke.bungeeutilisalsx.common.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyAddMemberJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyCreationJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyRemovalJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyRemoveMemberJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyManager;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.IRedisDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.*;

public class SimplePartyManager implements PartyManager
{

    private final List<Party> parties;

    public SimplePartyManager()
    {
        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            parties = Collections.synchronizedList( BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().getAllParties() );
        }
        else
        {
            parties = Collections.synchronizedList( new ArrayList<>() );
        }

        this.startPartyCleanupTask();
    }

    @Override
    public Party createParty( final User leader ) throws AlreadyInPartyException
    {
        if ( getCurrentPartyFor( leader.getName() ).isPresent() )
        {
            throw new AlreadyInPartyException();
        }

        final Party party = new Party( new Date() );
        party.getPartyMembers().add( new PartyMember(
                leader.getUuid(),
                leader.getName(),
                new Date(),
                leader.getName(),
                true,
                false
        ) );
        final PartyCreationJob partyCreationJob = new PartyCreationJob( party );

        BuX.getInstance().getJobManager().executeJob( partyCreationJob );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().registerParty( party );
        }

        return party;
    }

    @Override
    public Optional<Party> getCurrentPartyFor( final String userName )
    {
        return parties
                .stream()
                .filter( party -> party.getPartyMembers()
                        .stream()
                        .anyMatch( partyMember -> partyMember.getUserName().equals( userName ) ) )
                .findFirst();
    }

    @Override
    public Optional<Party> getCurrentPartyByUuid( final UUID uuid )
    {
        return parties
                .stream()
                .filter( party -> party.getUuid().equals( uuid ) )
                .findFirst();
    }

    @Override
    public void removeParty( final Party party )
    {
        final PartyRemovalJob partyRemovalJob = new PartyRemovalJob( party );

        BuX.getInstance().getJobManager().executeJob( partyRemovalJob );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().unregisterParty( party );
        }
    }

    @Override
    public void removeParty( final UUID uuid )
    {
        this.getCurrentPartyByUuid( uuid ).ifPresent( this::removeParty );
    }

    @Override
    public void registerPartyLocally( final Party party )
    {
        this.parties.add( party );
    }

    @Override
    public void unregisterPartyLocally( final Party party )
    {
        this.parties.removeIf( p -> p.getUuid().equals( party.getUuid() ) );
    }

    @Override
    public void addMemberToParty( final Party party, final PartyMember member )
    {
        final PartyAddMemberJob partyAddMemberJob = new PartyAddMemberJob( party, member );

        BuX.getInstance().getJobManager().executeJob( partyAddMemberJob );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().addMemberToParty( party, member );
        }
    }

    @Override
    public void removeMemberFromParty( final Party party, final PartyMember member )
    {
        if ( party.getPartyMembers().size() <= 1 )
        {
            removeParty( party );
            return;
        }

        if ( member.isPartyOwner() )
        {
            // TODO: ALSO UPDATE OWNER STATUS IN REDIS
            // TODO: assign new owner and call PartySetOwnerJob
        }

        final PartyRemoveMemberJob partyRemoveMemberJob = new PartyRemoveMemberJob( party, member );

        BuX.getInstance().getJobManager().executeJob( partyRemoveMemberJob );

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().removeMemberFromParty( party, member );
        }
    }

    private void startPartyCleanupTask()
    {
        final int period = ConfigFiles.PARTY_CONFIG.getPartyInactivityPeriod();

        BuX.getInstance().getScheduler().runTaskRepeating( period, period, TimeUnit.SECONDS, () ->
        {
            if ( BuX.getInstance().isRedisManagerEnabled() )
            {
                final IRedisDataManager redisDataManager = BuX.getInstance().getRedisManager().getDataManager();

                if ( !redisDataManager.attemptShedLock( "PARTY_CLEANUP", period, TimeUnit.SECONDS ) )
                {
                    return;
                }
            }

            final List<Party> queuedForRemoval = new ArrayList<>();

            for ( Party party : parties )
            {
                final boolean partyInactive = party.getPartyMembers()
                        .stream()
                        .noneMatch( partyMember -> BuX.getApi().getPlayerUtils().isOnline( partyMember.getUserName() ) );

                if ( party.isInactive() && partyInactive )
                {
                    queuedForRemoval.add( party );
                }
                else
                {
                    if ( party.isInactive() != partyInactive && BuX.getInstance().isRedisManagerEnabled() )
                    {
                        BuX.getInstance().getRedisManager().getDataManager()
                                .getRedisPartyDataManager().setInactiveStatus( party, partyInactive );
                    }

                    party.setInactive( partyInactive );
                }
            }

            queuedForRemoval.forEach( this::removeParty );

            for ( Party party : this.parties )
            {
                final List<PartyMember> membersQueuedForRemoval = new ArrayList<>();

                for ( PartyMember partyMember : party.getPartyMembers() )
                {
                    final boolean inactive = BuX.getApi().getPlayerUtils().isOnline( partyMember.getUserName() );

                    if ( partyMember.isInactive() && inactive )
                    {
                        membersQueuedForRemoval.add( partyMember );
                    }
                    else
                    {
                        if ( partyMember.isInactive() != inactive && BuX.getInstance().isRedisManagerEnabled() )
                        {
                            BuX.getInstance().getRedisManager().getDataManager()
                                    .getRedisPartyDataManager().setInactiveStatus( party, partyMember, inactive );
                        }
                        partyMember.setInactive( inactive );
                    }
                }

                membersQueuedForRemoval.forEach( member -> this.removeMemberFromParty( party, member ) );
            }
        } );
    }
}
