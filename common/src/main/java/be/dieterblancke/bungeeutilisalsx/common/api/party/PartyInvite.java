package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PartyInvite
{

    private final Date invitedAt;
    private final String invitee;
    private final String invitedBy;

}
