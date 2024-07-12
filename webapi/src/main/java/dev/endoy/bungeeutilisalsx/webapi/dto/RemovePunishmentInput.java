package dev.endoy.bungeeutilisalsx.webapi.dto;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemovePunishmentInput
{

    PunishmentType type;
    UUID uuid;
    String ip;
    String removedBy;
    String server;

}
