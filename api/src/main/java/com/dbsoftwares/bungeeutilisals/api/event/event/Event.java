package com.dbsoftwares.bungeeutilisals.api.event.event;

/*
 * Created by DBSoftwares on 14/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    int priority() default 0;

    boolean executeIfCancelled() default true;

}