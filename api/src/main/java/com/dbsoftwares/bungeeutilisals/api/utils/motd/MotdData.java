package com.dbsoftwares.bungeeutilisals.api.utils.motd;

/*
 * Created by DBSoftwares on 07 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotdData {

    private ConditionHandler conditionHandler;
    private boolean def;
    private String motd;

}