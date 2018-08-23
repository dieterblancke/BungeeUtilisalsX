package com.dbsoftwares.bungeeutilisals.api.placeholder;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.InputPlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
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
        PlaceHolderAPI.addPlaceHolder(false, "getcount", new InputPlaceHolderEventHandler() {
            @Override
            public String getReplacement(InputPlaceHolderEvent event) {
                IConfiguration configuration = getLanguageConfiguration(event.getUser());
                ServerGroup server = FileLocation.SERVERGROUPS.getData(event.getArgument());

                if (server == null) {
                    return "0";
                }

                return String.valueOf(server.getPlayers());
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