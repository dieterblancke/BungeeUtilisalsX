package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders.PlaceHolder;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PlaceHolderEvent
{

    private User user;
    private PlaceHolder placeHolder;
    private String message;

}