package dev.endoy.bungeeutilisalsx.common.api.placeholder.xml;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import org.jsoup.nodes.Document;

public abstract class XMLPlaceHolder
{

    public abstract String format( User user, String original, Document document );

}
