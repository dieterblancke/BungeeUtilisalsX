package com.dbsoftwares.bungeeutilisals.api.mysql.storage;

/*
 * Created by DBSoftwares on 05/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StorageTable {

    String name();

    String[] indexes() default {};

    String engine() default "InnoDB";

    int autoincrement() default 1;

    String charset() default "UTF8";

    String[] foreign() default {};

    String[] primary() default {};

}