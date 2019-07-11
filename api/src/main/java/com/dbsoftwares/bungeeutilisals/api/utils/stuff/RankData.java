package com.dbsoftwares.bungeeutilisals.api.utils.stuff;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankData {

    private String name;
    private String permission;
    private int priority;
    private EventData global;
    private EventData local;

}
