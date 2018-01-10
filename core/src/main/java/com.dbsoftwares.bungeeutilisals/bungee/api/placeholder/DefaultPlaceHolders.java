package com.dbsoftwares.bungeeutilisals.bungee.api.placeholder;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

public class DefaultPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
        PlaceHolderAPI.addPlaceHolder("{users-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.MYSQL).getString("user-table");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friends-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.FRIENDS_CONFIG).getString("tables.friends");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friendrequests-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.FRIENDS_CONFIG).getString("tables.requests");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{bans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.bans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.ipbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.tempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.iptempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{mutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.mutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.ipmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.tempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.iptempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{kicks-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.kicks");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{warns-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getConfigurations().get(FileLocation.PUNISHMENTS_CONFIG).getString("tables.warns");
            }
        });
    }
}