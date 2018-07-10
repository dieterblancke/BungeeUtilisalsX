package com.dbsoftwares.bungeeutilisals.api.utils.file;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum FileLocation {

    CONFIG("config.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    SERVERGROUPS("servergroups.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
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
    ANTISWEAR("chat/antiswear.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    ANTICAPS("chat/anticaps.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    ANTIAD("chat/antiadvertise.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    ANTISPAM("chat/antispam.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    UTFSYMBOLS("chat/utfsymbols.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    FRIENDS_CONFIG("friends/config.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    },
    PUNISHMENTS_CONFIG("punishments/config.yml") {
        @Override @SuppressWarnings("unchecked")
        public void loadData(IConfiguration configuration) {
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

            for (Object group : configuration.getList("actions")) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) group;

                try {
                    PunishmentType type = PunishmentType.valueOf((String) map.get("type"));


                } catch (IllegalArgumentException e) {
                    BUCore.getApi().getPlugin().getLogger().warning(
                            "An invalid punishment type has been entered (" + map.get("type") + ")."
                    );
                }
            }
        }
    },
    LANGUAGES_CONFIG("languages/config.yml") {
        @Override
        public void loadData(IConfiguration configuration) {
        }
    };

    @Getter
    private String path;

    @Getter
    private Map<String, Object> data;

    FileLocation(String path) {
        this.path = path;
        this.data = Maps.newHashMap();
    }

    public abstract void loadData(IConfiguration configuration);

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
}