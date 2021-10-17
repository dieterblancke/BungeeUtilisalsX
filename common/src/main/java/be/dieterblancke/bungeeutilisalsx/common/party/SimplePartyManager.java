package be.dieterblancke.bungeeutilisalsx.common.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyCreationJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.PartyRemovalJob;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyManager;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
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
    public void addMemberToParty( Party party, PartyMember member )
    {
        // TODO
    }

    @Override
    public void removeMemberFromParty( Party party, PartyMember member )
    {
        // TODO
    }

    private void startPartyCleanupTask()
    {
        final int period = ConfigFiles.PARTY_CONFIG.getPartyInactivityPeriod();

        BuX.getInstance().getScheduler().runTaskRepeating( period, period, TimeUnit.SECONDS, () ->
        {
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
                        partyMember.setInactive( inactive );
                    }
                }

                membersQueuedForRemoval.forEach( member -> this.removeMemberFromParty( party, member ) );
            }
        } );
    }
}
