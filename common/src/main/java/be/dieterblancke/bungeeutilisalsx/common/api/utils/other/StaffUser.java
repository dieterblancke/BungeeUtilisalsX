package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class StaffUser
{

    private final String name;
    private final UUID uuid;
    private final StaffRankData rank;
    private boolean hidden;
    private boolean vanished;

}
