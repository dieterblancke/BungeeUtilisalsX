package com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders;


import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputPlaceHolder extends PlaceHolder {

    private Pattern pattern;

    public InputPlaceHolder(String placeHolder, boolean requiresUser, String prefix, PlaceHolderEventHandler handler) {
        super(placeHolder, requiresUser, handler);

        this.pattern = makePlaceholderWithArgsPattern(prefix);
    }

    private static Pattern makePlaceholderWithArgsPattern(String prefix) {
        return Pattern.compile("(\\{" + Pattern.quote(prefix) + ":)(.+?)(\\})");
    }

    private static String extractArgumentFromPlaceholder(Matcher matcher) {
        return matcher.group(2).trim();
    }

    @Override
    public String format(User user, String message) {
        if (placeHolder == null || !message.contains(placeHolder)) {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent(user, this, message);
        return message.replace(placeHolder, Utils.c(eventHandler.getReplacement(event)));
    }
}