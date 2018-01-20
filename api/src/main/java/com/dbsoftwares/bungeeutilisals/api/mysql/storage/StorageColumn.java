package com.dbsoftwares.bungeeutilisals.api.mysql.storage;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StorageColumn {

    String type() default "VARCHAR(16)";

    boolean nullable() default true;

    boolean autoincrement() default false;

    boolean primary() default false;

    boolean unique() default false;

    String def() default "";

    boolean updateable() default true;

}