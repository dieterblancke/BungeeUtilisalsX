package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UpdateUserInput
{
    String userName;
    String ip;
    String language;
    LocalDateTime lastLogout;
}
