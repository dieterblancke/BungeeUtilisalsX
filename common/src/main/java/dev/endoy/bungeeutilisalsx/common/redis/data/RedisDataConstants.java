package dev.endoy.bungeeutilisalsx.common.redis.data;

import java.util.UUID;

public class RedisDataConstants
{

    public static final String SEPARATOR = ":";

    /* -- Domains -- */
    public static final String DOMAIN_PREFIX = "domain" + SEPARATOR;

    /* -- Shedlock -- */
    public static final String SHEDLOCK_PREFIX = "shedlock_";

    /* -- Parties -- */
    public static final String PARTIES_KEY = "parties";
    private static final String PARTY_PREFIX = "party";
    private static final String PARTY_MEMBER_PREFIX = "members";
    private static final String PARTY_INVITATION_PREFIX = "invitations";
    private static final String PARTY_JOIN_REQUEST_PREFIX = "join_requests";

    public static String getPartyPrefix( final String partyUuid )
    {
        return PARTY_PREFIX + SEPARATOR + partyUuid;
    }

    public static String getPartyPrefix( final UUID partyUuid )
    {
        return getPartyPrefix( partyUuid.toString() );
    }

    public static String getPartyMemberPrefix( final UUID partyUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_MEMBER_PREFIX;
    }

    public static String getPartyMemberPrefix( final UUID partyUuid, final UUID partyMemberUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_MEMBER_PREFIX + SEPARATOR + partyMemberUuid;
    }

    public static String getPartyInvitationPrefix( final UUID partyUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_INVITATION_PREFIX;
    }

    public static String getPartyInvitationPrefix( final UUID partyUuid, final UUID inviteeUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_INVITATION_PREFIX + SEPARATOR + inviteeUuid;
    }

    public static String getPartyJoinRequestPrefix( final UUID partyUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_JOIN_REQUEST_PREFIX;
    }

    public static String getPartyJoinRequestPrefix( final UUID partyUuid, final UUID requesterUuid )
    {
        return getPartyPrefix( partyUuid ) + SEPARATOR + PARTY_JOIN_REQUEST_PREFIX + SEPARATOR + requesterUuid;
    }
}