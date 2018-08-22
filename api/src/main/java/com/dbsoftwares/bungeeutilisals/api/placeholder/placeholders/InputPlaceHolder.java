package com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders;

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.InputPlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputPlaceHolder extends PlaceHolder {

    private Pattern pattern;

    public InputPlaceHolder(boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler) {
        super(null, requiresUser, handler);

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
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            final String argument = extractArgumentFromPlaceholder(matcher);
            final InputPlaceHolderEvent event = new InputPlaceHolderEvent(user, this, message, argument);

            message = message.replace(matcher.group(), eventHandler.getReplacement(event));
        }

        return message;
    }
}