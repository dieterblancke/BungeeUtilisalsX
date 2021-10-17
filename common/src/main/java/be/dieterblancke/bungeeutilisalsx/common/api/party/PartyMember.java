package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PartyMember
{

    private final UUID uuid;
    private final String userName;
    private final Date joinedAt;
    private String nickName;
    private boolean partyOwner;
    private boolean inactive;

}
