/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.addon;

import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.File;
import java.util.List;
import java.util.Set;

@Data
@ToString
@EqualsAndHashCode
public class AddonDescription {

    private final String name;
    private final String version;
    private final String main;
    private final String author;
    private final Set<String> requiredDependencies; // using Set to prevent duplicates
    private final Set<String> optionalDependencies; // using Set to prevent duplicates
    private final String description;
    private final String source;
    private final File file;

    public AddonDescription(IConfiguration configuration, File file) {
        this.name = configuration.getString("name");
        this.version = configuration.getString("version");
        this.main = configuration.getString("main");
        this.author = configuration.getString("author");
        this.description = configuration.getString("description");
        this.source = configuration.exists("source") ? configuration.getString("source") : null;

        final List<String> required = configuration.getStringList("dependencies.required");
        final List<String> optional = configuration.getStringList("dependencies.optional");
        this.requiredDependencies = Sets.newHashSet(required == null ? Lists.newArrayList() : required);
        this.optionalDependencies = Sets.newHashSet(optional == null ? Lists.newArrayList() : optional);

        this.file = file;
    }
}
