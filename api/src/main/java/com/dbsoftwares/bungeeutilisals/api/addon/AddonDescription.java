package com.dbsoftwares.bungeeutilisals.api.addon;

/*
 * Created by DBSoftwares on 26/09/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Sets;
import lombok.Data;

import java.io.File;
import java.util.Set;

@Data
public class AddonDescription {

    private final String name;
    private final String version;
    private final String main;
    private final String author;
    private final Set<String> requiredDependencies; // using Set to prevent duplicates
    private final Set<String> optionalDependencies; // using Set to prevent duplicates
    private final String description;
    private final File file;

    public AddonDescription(IConfiguration configuration, File file) {
        this.name = configuration.getString("name");
        this.version = configuration.getString("version");
        this.main = configuration.getString("main");
        this.author = configuration.getString("author");
        this.description = configuration.getString("description");

        this.requiredDependencies = Sets.newHashSet(configuration.getStringList("dependencies.required"));
        this.optionalDependencies = Sets.newHashSet(configuration.getStringList("dependencies.optional"));

        this.file = file;
    }
}
