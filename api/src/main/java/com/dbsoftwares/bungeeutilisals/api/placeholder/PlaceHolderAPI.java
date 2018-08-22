package com.dbsoftwares.bungeeutilisals.api.placeholder;

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.DefaultPlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.InputPlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.Lists;

import java.util.List;

public class PlaceHolderAPI {

    private static List<PlaceHolder> placeholders = Lists.newArrayList();

    public static String formatMessage(User user, String message) {
        if (user == null) {
            return formatMessage(message);
        }
        try {
            for (PlaceHolder placeholder : placeholders) {
                message = placeholder.format(user, message);
            }
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }

    public static String formatMessage(String message) {
        try {
            for (PlaceHolder placeholder : placeholders) {
                if (placeholder.requiresUser()) {
                    continue;
                }
                message = placeholder.format(null, message);
            }
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return message;
        }
    }

    public static void loadPlaceHolderPack(PlaceHolderPack pack) {
        pack.loadPack();
    }

    public static void addPlaceHolder(String placeholder, boolean requiresUser, PlaceHolderEventHandler handler) {
        placeholders.add(new DefaultPlaceHolder(placeholder, requiresUser, handler));
    }

    public static void addPlaceHolder(boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler) {
        placeholders.add(new InputPlaceHolder(requiresUser, prefix, handler));
    }

    public static PlaceHolder getPlaceHolder(String placeholder) {
        for (PlaceHolder ph : placeholders) {
            if (ph.getPlaceHolder().toLowerCase().equals(placeholder.toLowerCase())) {
                return ph;
            }
        }
        return null;
    }
}