package com.dbsoftwares.bungeeutilisals.api.utils.file;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.motd.MotdData;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.api.utils.time.TimeUnit;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

public enum FileLocation {

    CONFIG("config.yml") {
        @Override
        public void loadData() {
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
            List<MotdData> dataList = Lists.newArrayList();

            for (ISection section : configuration.getSectionList("groups")) {
                String condition = section.getString("condition");
                String motd = section.getString("motd");

                if (condition.equalsIgnoreCase("default")) {
                    dataList.add(new MotdData(null, true, motd));
                } else {
                    if (condition.startsWith("domain")) {
                        // todo
                    }
                    dataList.add(new MotdData(null, false, motd));
                }
            }
        }
    },
    CUSTOMCOMMANDS("commands/customcommands.yml") {
        @Override
        public void loadData() {
        }
    },
    GENERALCOMMANDS("commands/generalcommands.yml") {
        @Override
        public void loadData() {
        }
    },
    ANTISWEAR("chat/protection/antiswear.yml") {
        @Override
        public void loadData() {
        }
    },
    ANTICAPS("chat/protection/anticaps.yml") {
        @Override
        public void loadData() {
        }
    },
    ANTIAD("chat/protection/antiadvertise.yml") {
        @Override
        public void loadData() {
        }
    },
    ANTISPAM("chat/protection/antispam.yml") {
        @Override
        public void loadData() {
        }
    },
    UTFSYMBOLS("chat/utfsymbols.yml") {
        @Override
        public void loadData() {
        }
    },
    FRIENDS_CONFIG("friends/config.yml") {
        @Override
        public void loadData() {
        }
    },
    PUNISHMENTS_CONFIG("punishments/config.yml") {
        @Override @SuppressWarnings("unchecked")
        public void loadData() {
            for (ISection section : configuration.getSectionList("actions")) {
                try {
                    PunishmentType type = PunishmentType.valueOf(section.getString("type"));

                    try {
                        TimeUnit unit = TimeUnit.valueOf(section.getString("time.unit"));

                        if (section.isInteger("time.amount")) {
                            int amount = section.getInteger("time.amount");
                            int limit = section.getInteger("limit");

                            PunishmentAction action = new PunishmentAction(type, unit, amount, limit, section.getStringList("actions"));
                            List<PunishmentAction> actions = (List<PunishmentAction>) getData().getOrDefault(
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
        }
    };

    @Getter
    private String path;

    @Getter
    private LinkedHashMap<String, Object> data;

    @Getter
    protected IConfiguration configuration;

    FileLocation(String path) {
        this.path = path;
        this.data = Maps.newLinkedHashMap();
    }

    public abstract void loadData();

    @SuppressWarnings("unchecked")
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
}