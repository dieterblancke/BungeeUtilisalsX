package com.dbsoftwares.bungeeutilisals.api.utils.stuff;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventData {

    private boolean enabled;
    private String message;
    private String receivePermission;
    private boolean useCommands;
    private List<String> commands;

}
