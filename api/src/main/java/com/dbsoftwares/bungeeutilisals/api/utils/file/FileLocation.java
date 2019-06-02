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

package com.dbsoftwares.bungeeutilisals.api.utils.file;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.data.StaffRankData;
import com.dbsoftwares.bungeeutilisals.api.motd.MotdData;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.DomainConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.NameConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.motd.handlers.VersionConditionHandler;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public enum FileLocation {

    CONFIG("config.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    SERVERGROUPS("servergroups.yml") {
        @Override
        public void loadData() {
            for (ISection group : configuration.getSectionList("groups")) {
                String name = group.getString("name");

                if (group.isList("servers")) {
                    setData(name, new ServerGroup(name, false, group.getStringList("servers")));
                } else {
                    setData(name, new ServerGroup(name, true, Lists.newArrayList()));
                }
            }

            for (String key : ProxyServer.getInstance().getServers().keySet()) {
                if (!hasData(key)) {
                    setData(key, new ServerGroup(key, false, Lists.newArrayList(key)));
                }
            }
        }
    },
    MOTD("motd.yml") {
        @Override
        public void loadData() {
            for (ISection section : configuration.getSectionList("motd")) {
                final String condition = section.getString("condition");
                final String motd = section.getString("motd");

                if (condition.equalsIgnoreCase("default")) {
                    getDataList().add(new MotdData(null, true, motd));
                } else if (condition.toLowerCase().startsWith("domain")) {
                    getDataList().add(new MotdData(new DomainConditionHandler(condition), false, motd));
                } else if (condition.toLowerCase().startsWith("version")) {
                    getDataList().add(new MotdData(new VersionConditionHandler(condition), false, motd));
                } else if (condition.toLowerCase().startsWith("name")) {
                    getDataList().add(new MotdData(new NameConditionHandler(condition), false, motd));
                } else {
                    BUCore.getLogger().warn("An invalid MOTD condition has been entered.");
                    BUCore.getLogger().warn("For all available conditions, see https://docs.dbsoftwares.eu/bungeeutilisals/motd-chat#conditions");
                }
            }
        }
    },
    CUSTOMCOMMANDS("commands/customcommands.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    GENERALCOMMANDS("commands/generalcommands.yml") {
        @Override
        public void loadData() {
            if (configuration.getBoolean("staff.enabled")) {
                final List<StaffRankData> ranks = Lists.newArrayList();
                final List<ISection> sections = configuration.getSectionList("staff.ranks");

                for (ISection section : sections) {
                    final String name = section.getString("name");
                    final String display = section.getString("display");
                    final String permission = section.getString("permission");

                    ranks.add(new StaffRankData(name, display, permission));
                }

                setData("staff_ranks", ranks);
            }
        }
    },
    ANTISWEAR("chat/protection/antiswear.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    ANTICAPS("chat/protection/anticaps.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    ANTIAD("chat/protection/antiadvertise.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    ANTISPAM("chat/protection/antispam.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    UTFSYMBOLS("chat/utfsymbols.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    FRIENDS_CONFIG("friends.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    },
    PUNISHMENTS("punishments.yml") {
        @Override
        public void loadData() {
            for (ISection section : configuration.getSectionList("actions")) {
                try {
                    final PunishmentType type = PunishmentType.valueOf(section.getString("type"));

                    try {
                        final TimeUnit unit = TimeUnit.valueOf(section.getString("time.unit"));

                        if (section.isInteger("time.amount")) {
                            final int amount = section.getInteger("time.amount");
                            final int limit = section.getInteger("limit");

                            final PunishmentAction action = new PunishmentAction(type, unit, amount, limit, section.getStringList("actions"));
                            final List<PunishmentAction> actions = (List<PunishmentAction>) getData().getOrDefault(
                                    type.toString(), Lists.newArrayList()
                            );

                            actions.add(action);
                            setData(type.toString(), actions);
                        } else {
                            BUCore.getApi().getPlugin().getLogger().warning(
                                    "An invalid number has been entered (" + section.getString("time.amount") + ")."
                            );
                        }
                    } catch (IllegalArgumentException e) {
                        BUCore.getApi().getPlugin().getLogger().warning(
                                "An invalid time unit has been entered (" + section.getString("time.unit") + ")."
                        );
                    }
                } catch (IllegalArgumentException e) {
                    BUCore.getApi().getPlugin().getLogger().warning(
                            "An invalid punishment type has been entered (" + section.getString("type") + ")."
                    );
                }
            }
        }
    },
    LANGUAGES_CONFIG("languages/config.yml") {
        @Override
        public void loadData() {
            // do nothing
        }
    };

    @Getter
    protected IConfiguration configuration;
    @Getter
    private String path;
    @Getter
    private LinkedHashMap<String, Object> data;
    private LinkedList<Object> dataList;

    FileLocation(String path) {
        this.path = path;
        this.data = Maps.newLinkedHashMap();
        this.dataList = Lists.newLinkedList();
    }

    public abstract void loadData();

    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    public boolean hasData(String key) {
        return data.containsKey(key);
    }

    public <T> void setData(String key, T data) {
        this.data.put(key, data);
    }

    public void loadConfiguration(File file) {
        this.configuration = IConfiguration.loadYamlConfiguration(file);
    }

    public <T> List<T> getDataList() {
        return (List<T>) dataList;
    }
}