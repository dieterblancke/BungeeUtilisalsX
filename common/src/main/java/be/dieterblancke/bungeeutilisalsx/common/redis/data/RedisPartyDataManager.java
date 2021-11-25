package be.dieterblancke.bungeeutilisalsx.common.redis.data;

import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyInvite;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyJoinRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.party.PartyMember;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.IRedisPartyDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.RedisManager;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static be.dieterblancke.bungeeutilisalsx.common.redis.data.RedisDataConstants.*;

@RequiredArgsConstructor
public class RedisPartyDataManager implements IRedisPartyDataManager
{

    private final RedisManager redisManager;

    @Override
    public void registerParty( final Party party )
    {
        redisManager.executeAsync( commands ->
        {
            commands.sadd( PARTIES_KEY, party.getUuid().toString() );

            final Map<String, String> partyData = new HashMap<>();
            partyData.put( "createdAt", String.valueOf( party.getCreatedAt().getTime() ) );
            partyData.put( "inactive", String.valueOf( party.isInactive() ) );

            commands.hset( getPartyPrefix( party.getUuid() ), partyData );

            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                this.addMemberToParty( commands, party, partyMember );
            }
        } );
    }

    @Override
    public void unregisterParty( final Party party )
    {
        redisManager.executeAsync( commands ->
        {
            commands.srem( PARTIES_KEY, party.getUuid().toString() );

            commands.hdel(
                    getPartyPrefix( party.getUuid() ),
                    "createdAt",
                    "inactive"
            );

            for ( PartyMember partyMember : party.getPartyMembers() )
            {
                this.removeMemberFromParty( commands, party, partyMember );
            }
        } );
    }

    @Override
    public void addMemberToParty( final Party party, final PartyMember partyMember )
    {
        redisManager.executeAsync( commands ->
        {
            addMemberToParty( commands, party, partyMember );
        } );
    }

    private void addMemberToParty( final RedisClusterAsyncCommands<String, String> commands,
                                   final Party party,
                                   final PartyMember partyMember )
    {
        commands.sadd( getPartyMemberPrefix( party.getUuid() ), partyMember.getUuid().toString() );
        commands.hset( getPartyMemberPrefix( party.getUuid(), partyMember.getUuid() ), partyMember.asMap() );
    }

    @Override
    public void removeMemberFromParty( final Party party, final PartyMember partyMember )
    {
        redisManager.executeAsync( commands ->
        {
            removeMemberFromParty( commands, party, partyMember );
        } );
    }

    private void removeMemberFromParty( final RedisClusterAsyncCommands<String, String> commands,
                                        final Party party,
                                        final PartyMember partyMember )
    {
        commands.srem( getPartyMemberPrefix( party.getUuid() ), partyMember.getUuid().toString() );

        commands.hdel(
                getPartyMemberPrefix( party.getUuid(), partyMember.getUuid() ),
                "userName",
                "joinedAt",
                "nickName",
                "partyOwner",
                "inactive",
                "chat"
        );
    }

    @Override
    public void setInactiveStatus( final Party party, final boolean inactive )
    {
        redisManager.executeAsync( commands ->
        {
            commands.hset( getPartyPrefix( party.getUuid() ), "inactive", String.valueOf( inactive ) );
        } );
    }

    @Override
    public void setInactiveStatus( final Party party, final PartyMember partyMember, final boolean inactive )
    {
        redisManager.executeAsync( commands ->
        {
            commands.hset( getPartyMemberPrefix( party.getUuid(), partyMember.getUuid() ), "inactive", String.valueOf( inactive ) );
        } );
    }

    @Override
    public void setOwnerStatus( final Party party, final PartyMember partyMember, final boolean owner )
    {
        redisManager.executeAsync( commands ->
        {
            commands.hset( getPartyMemberPrefix( party.getUuid(), partyMember.getUuid() ), "partyOwner", String.valueOf( owner ) );
        } );
    }

    @Override
    public void setChatStatus( final Party party, final PartyMember partyMember, final boolean chat )
    {
        redisManager.executeAsync( commands ->
        {
            commands.hset( getPartyMemberPrefix( party.getUuid(), partyMember.getUuid() ), "chat", String.valueOf( chat ) );
        } );
    }

    @Override
    public void addInviteToParty( final Party party, final PartyInvite partyInvite )
    {
        redisManager.executeAsync( commands ->
        {
            commands.sadd( getPartyInvitationPrefix( party.getUuid() ), partyInvite.getInvitee().toString() );
            commands.hset( getPartyInvitationPrefix( party.getUuid(), partyInvite.getInvitee() ), partyInvite.asMap() );
        } );
    }

    @Override
    public void removeInviteFromParty( final Party party, final PartyInvite partyInvite )
    {
        redisManager.executeAsync( commands ->
        {
            commands.srem( getPartyInvitationPrefix( party.getUuid() ), partyInvite.getInvitee().toString() );

            commands.hdel(
                    getPartyInvitationPrefix( party.getUuid(), partyInvite.getInvitee() ),
                    "invitedAt",
                    "invitedBy"
            );
        } );
    }

    @Override
    public void addJoinRequestToParty( final Party party, final PartyJoinRequest partyJoinRequest )
    {
        redisManager.executeAsync( commands ->
        {
            commands.sadd( getPartyJoinRequestPrefix( party.getUuid() ), partyJoinRequest.getRequester().toString() );
            commands.hset( getPartyJoinRequestPrefix( party.getUuid(), partyJoinRequest.getRequester() ), partyJoinRequest.asMap() );
        } );
    }

    @Override
    public void removeJoinRequestFromParty( final Party party, final PartyJoinRequest partyJoinRequest )
    {
        redisManager.executeAsync( commands ->
        {
            commands.srem( getPartyJoinRequestPrefix( party.getUuid() ), partyJoinRequest.getRequester().toString() );

            commands.hdel(
                    getPartyJoinRequestPrefix( party.getUuid(), partyJoinRequest.getRequester() ),
                    "requestedAt"
            );
        } );
    }

    @Override
    public List<Party> getAllParties()
    {
        return redisManager.execute( commands ->
        {
            return commands.smembers( PARTIES_KEY )
                    .stream()
                    .map( partyUuid ->
                    {
                        final Party party = new Party(
                                UUID.fromString( partyUuid ),
                                new Date( Long.parseLong( commands.hget( getPartyPrefix( partyUuid ), "createdAt" ) ) )
                        );

                        party.getPartyMembers().addAll( this.getAllPartyMembers( commands, party ) );
                        party.getSentInvites().addAll( this.getAllPartyInvitations( commands, party ) );
                        party.getJoinRequests().addAll( this.getAllPartyJoinRequests( commands, party ) );

                        return party;
                    } )
                    .collect( Collectors.toList() );
        } );
    }

    private List<PartyMember> getAllPartyMembers( final RedisClusterCommands<String, String> commands, final Party party )
    {
        return commands.smembers( getPartyMemberPrefix( party.getUuid() ) )
                .stream()
                .map( UUID::fromString )
                .map( partyMemberUuid ->
                {
                    final Map<String, String> memberData = commands.hgetall(
                            getPartyMemberPrefix( party.getUuid(), partyMemberUuid )
                    );

                    return PartyMember.fromMap( partyMemberUuid, memberData );
                } )
                .collect( Collectors.toList() );
    }

    private List<PartyInvite> getAllPartyInvitations( final RedisClusterCommands<String, String> commands, final Party party )
    {
        return commands.smembers( getPartyInvitationPrefix( party.getUuid() ) )
                .stream()
                .map( UUID::fromString )
                .map( inviteeUuid ->
                {
                    final Map<String, String> inviteData = commands.hgetall(
                            getPartyInvitationPrefix( party.getUuid(), inviteeUuid )
                    );

                    return PartyInvite.fromMap( inviteeUuid, inviteData );
                } )
                .collect( Collectors.toList() );
    }

    private List<PartyJoinRequest> getAllPartyJoinRequests( final RedisClusterCommands<String, String> commands, final Party party )
    {
        return commands.smembers( getPartyJoinRequestPrefix( party.getUuid() ) )
                .stream()
                .map( UUID::fromString )
                .map( requesterUuid ->
                {
                    final Map<String, String> requestData = commands.hgetall(
                            getPartyJoinRequestPrefix( party.getUuid(), requesterUuid )
                    );

                    return PartyJoinRequest.fromMap( requesterUuid, requestData );
                } )
                .collect( Collectors.toList() );
    }
}
