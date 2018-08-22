package com.dbsoftwares.bungeeutilisals.api.placeholder.event;

import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PlaceHolderEvent {

    private User user;
    private PlaceHolder placeHolder;
    private String message;

}