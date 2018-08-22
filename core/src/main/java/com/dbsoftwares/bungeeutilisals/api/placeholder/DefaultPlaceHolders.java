package com.dbsoftwares.bungeeutilisals.api.placeholder;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.InputPlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
        PlaceHolderAPI.addPlaceHolder("{users-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.users");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friends-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friends");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{friendrequests-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friendrequests");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{bans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.bans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.ipbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.tempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempbans-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.iptempbans");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{mutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.mutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{ipmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.ipmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{tempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.tempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{iptempmutes-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.iptempmutes");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{kicks-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.kicks");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{warns-table}", false, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.warns");
            }
        });
        PlaceHolderAPI.addPlaceHolder("{user}", true, new PlaceHolderEventHandler() {
            @Override
            public String getReplacement(PlaceHolderEvent event) {
                return event.getUser().getName();
            }
        });
        PlaceHolderAPI.addPlaceHolder(false, "timeleft", new InputPlaceHolderEventHandler() {
            @Override
            public String getReplacement(InputPlaceHolderEvent event) {
                IConfiguration configuration = getLanguageConfiguration(event.getUser());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss");

                try {
                    Date date = dateFormat.parse(event.getArgument());
                    long time = date.getTime() - System.currentTimeMillis();

                    return configuration.getString("placeholders.timeleft")
                            .replace("%days%", String.valueOf(getDays(time)))
                            .replace("%hours%", String.valueOf(getHours(time)))
                            .replace("%minutes%", String.valueOf(getMinutes(time)))
                            .replace("%seconds%", String.valueOf(getSeconds(time)));
                } catch (ParseException e) {
                    return "";
                }
            }
        });
    }

    // Utility functions
    private IConfiguration getLanguageConfiguration(User user) {
        if (user == null) {
            return BUCore.getApi().getLanguageManager().getConfig(
                    BUCore.getApi().getPlugin(),
                    BUCore.getApi().getLanguageManager().getDefaultLanguage()
            );
        }
        return user.getLanguageConfig();
    }

    private long getDays(long time) {
        return TimeUnit.MILLISECONDS.toDays(time);
    }

    private long getHours(long time) {
        time = time - TimeUnit.DAYS.toMillis(getDays(time));

        return TimeUnit.MILLISECONDS.toHours(time);
    }

    private long getMinutes(long time) {
        time = time - TimeUnit.DAYS.toMillis(getDays(time));
        time = time - TimeUnit.HOURS.toMillis(getHours(time));

        return TimeUnit.MILLISECONDS.toMinutes(time);
    }

    private long getSeconds(long time) {
        time = time - TimeUnit.DAYS.toMillis(getDays(time));
        time = time - TimeUnit.HOURS.toMillis(getHours(time));
        time = time - TimeUnit.MINUTES.toMillis(getMinutes(time));

        return TimeUnit.MILLISECONDS.toSeconds(time);
    }
}