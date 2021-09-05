package be.dieterblancke.bungeeutilisalsx.webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInput
{
    private String userName;
    private String ip;
    private String language;
    private LocalDateTime lastLogout;
}
