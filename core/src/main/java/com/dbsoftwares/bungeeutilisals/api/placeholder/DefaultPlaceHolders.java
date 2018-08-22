package com.dbsoftwares.bungeeutilisals.api.placeholder;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;

public class DefaultPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
        PlaceHolderAPI.addPlaceHolder("{users-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.users");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friends-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friends");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friendrequests-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friendrequests");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{bans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.bans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.ipbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.tempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.iptempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{mutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.mutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.ipmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.tempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.iptempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{kicks-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.kicks");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{warns-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.warns");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{user}", true, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return event.getUser().getName();
            }
        });

        // For MOTD, allows to use time left + format (must be able to read input, !!TODO!!)
        PlaceHolderAPI.addPlaceHolder("{timeleft_%time%}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplace(PlaceHolderEvent event) {
                return event.getUser().getName();
            }
        });
    }
}