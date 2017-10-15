package com.dbsoftwares.bungeeutilisals.api.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPath {
	String value() default "";
}