package be.dieterblancke.bungeeutilisalsx.common.api.party;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PartyJoinRequest
{

    private final Date requestedAt;
    private final String requester;

}
