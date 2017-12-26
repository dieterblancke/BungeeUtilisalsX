package com.dbsoftwares.bungeeutilisals.api.experimental.inventory;

/*
 * Created by DBSoftwares on 26 december 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.LinkedList;

public class InventoryUnsafe {

    @Getter LinkedList<User> viewers = Lists.newLinkedList();

}