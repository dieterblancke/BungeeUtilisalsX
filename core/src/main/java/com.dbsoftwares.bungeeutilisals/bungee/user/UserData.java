package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.user.DatabaseUser;

import java.util.List;

public class UserData implements DatabaseUser {

    @Override
    public Boolean exists(String user) {
        return null;
    }

    @Override
    public List<String> getPlayersOnIP(String IP) {
        return null;
    }

    @Override
    public String getIP(String user) {
        return null;
    }

    @Override
    public void setIP(String user, String IP) {

    }

    @Override
    public void addIgnoredPlayer(String user, String ignoredplayer) {

    }

    @Override
    public void removeIgnoredPlayer(String user, String ignoredplayer) {

    }
}
