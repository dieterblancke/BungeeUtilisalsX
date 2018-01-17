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
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

public class DefaultPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
        PlaceHolderAPI.addPlaceHolder("{users-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.users");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friends-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.friends");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friendrequests-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.friendrequests");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{bans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.bans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.ipbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.tempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.iptempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{mutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.mutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.ipmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.tempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.iptempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{kicks-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.kicks");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{warns-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schema.warns");
            }
        });
    }
}