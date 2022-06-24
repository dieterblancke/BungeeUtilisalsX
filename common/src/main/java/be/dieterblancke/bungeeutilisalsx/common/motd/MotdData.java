package be.dieterblancke.bungeeutilisalsx.common.motd;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MotdData
{

    private ConditionHandler conditionHandler;
    private boolean def;
    private String motd;
    private List<String> hoverMessages;

}