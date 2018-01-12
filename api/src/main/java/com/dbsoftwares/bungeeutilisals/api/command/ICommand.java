package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.user.User;

public interface ICommand {

    void onExecute(User user, String[] args);

}