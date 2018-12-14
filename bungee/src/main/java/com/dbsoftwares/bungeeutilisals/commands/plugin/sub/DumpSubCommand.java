/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.commands.plugin.sub;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.dump.Dump;
import com.dbsoftwares.bungeeutilisals.dump.PluginInfo;
import com.dbsoftwares.bungeeutilisals.dump.PluginSchedulerInfo;
import com.dbsoftwares.bungeeutilisals.dump.SystemInfo;
import com.dbsoftwares.bungeeutilisals.utils.TPSRunnable;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.management.OperatingSystemMXBean;
import gnu.trove.map.TIntObjectMap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DumpSubCommand extends SubCommand {

    public DumpSubCommand() {
        super("dump", 0);
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals dump";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.dump";
    }

    @Override
    public void onExecute(User user, String[] args) {
        final Dump dump = getDump();
        ProxyServer.getInstance().getScheduler().runAsync(BungeeUtilisals.getInstance(), () -> {
            final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            try {
                final HttpURLConnection con = (HttpURLConnection) new URL("https://hastebin.com/documents/").openConnection();

                con.addRequestProperty(
                        "User-Agent", "BungeeUtilisals v" + BungeeUtilisals.getInstance().getDescription().getVersion()
                );
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("charset", "utf-8");
                con.setDoOutput(true);

                final OutputStream out = con.getOutputStream();
                out.write(gson.toJson(dump).getBytes(Charset.forName("UTF-8")));
                out.close();

                if (con.getResponseCode() == 429) {
                    user.sendMessage("&eYou have exceeded the allowed amount of dumps per minute.");
                    return;
                }

                final String response = CharStreams.toString(new InputStreamReader(con.getInputStream()));
                con.getInputStream().close();

                final JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

                if (!jsonResponse.has("key")) {
                    throw new IllegalStateException("Could not create dump correctly, did something go wrong?");
                }

                user.sendMessage("&eSuccessfully created a dump at: "
                        + "&bhttps://hastebin.com/" + jsonResponse.get("key").getAsString() + ".dump");
            } catch (IOException e) {
                user.sendMessage("Could not create dump. Please check the console for errors.");
                BUCore.getLogger().warn("Could not create dump request");
                e.printStackTrace();
            }
        });
    }

    private Dump getDump() {
        final OperatingSystemMXBean operatingSystemMXBean = ((OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean());
        final long totalMemory = operatingSystemMXBean.getTotalPhysicalMemorySize() / 1024 / 1024;
        final long freeMemory = operatingSystemMXBean.getFreePhysicalMemorySize() / 1024 / 1024;
        final long usedMemory = totalMemory - freeMemory;

        final SystemInfo systemInfo = new SystemInfo(
                System.getProperty("java.version"),
                System.getProperty("os.name"),
                ProxyServer.getInstance().getName(),
                ProxyServer.getInstance().getVersion(),
                TPSRunnable.getTPS(),
                (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB",
                ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MB",
                (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB",
                new SimpleDateFormat("kk:mm dd-MM-yyyy").format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())),
                totalMemory + " MB",
                usedMemory + " MB",
                freeMemory + " MB",
                ProxyServer.getInstance().getOnlineCount()
        );

        final List<PluginInfo> plugins = Lists.newArrayList();
        for (final Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            final PluginDescription description = plugin.getDescription();
            plugins.add(
                    new PluginInfo(
                            description.getName(),
                            description.getVersion(),
                            description.getAuthor(),
                            description.getDepends(),
                            description.getSoftDepends(),
                            description.getDescription()
                    )
            );
        }

        final Map<String, Map<String, Object>> configurations = Maps.newHashMap();
        for (FileLocation location : FileLocation.values()) {
            final Map<String, Object> values = readValues(location.getConfiguration().getValues());
            configurations.put(location.toString().toLowerCase(), values);
        }

        final Map<String, Map<String, Object>> languages = Maps.newHashMap();
        for (Language language : BUCore.getApi().getLanguageManager().getLanguages()) {
            try {
                IConfiguration config = BUCore.getApi().getLanguageManager().getConfig(BungeeUtilisals.getInstance().getDescription().getName(), language);

                languages.put(language.getName(), readValues(config.getValues()));
            } catch (RuntimeException ignored) {
            }
        }

        return new Dump("BungeeUtilisals", systemInfo, plugins, getTasks(), getScripts(), configurations, languages);
    }

    private Map<String, Object> readValues(Map<String, Object> map) {
        final Map<String, Object> values = Maps.newHashMap();

        map.forEach((key, value) -> {
            if (value instanceof List) {
                final List<Map<String, Object>> sectionList = Lists.newArrayList();

                for (Object item : (List) value) {
                    if (item instanceof ISection) {
                        sectionList.add(readValues(((ISection) item).getValues()));
                    }
                }
                if (!sectionList.isEmpty()) {
                    values.put(key, sectionList);
                } else {
                    values.put(key, value);
                }
            } else if (value instanceof ISection) {
                values.put(key, readValues(((ISection) value).getValues()));
            } else {
                if (key.endsWith("password")) {
                    values.put(key, "***********");
                } else {
                    if (value instanceof String) {
                        value = ((String) value).replace("\r\n", " ").replace("\n", " ");
                    }
                    values.put(key, value);
                }
            }
        });

        return values;
    }

    @SuppressWarnings("unchecked")
    private List<PluginSchedulerInfo> getTasks() {
        try {
            final TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
            final Field tasksField = ReflectionUtils.getField(scheduler.getClass(), "tasks");

            final TIntObjectMap<ScheduledTask> map = (TIntObjectMap<ScheduledTask>) tasksField.get(scheduler);
            final Collection<ScheduledTask> tasks = map.valueCollection();

            int running = 0;
            int total = 0;

            final List<PluginSchedulerInfo> schedulerInfoList = Lists.newLinkedList();

            for (ScheduledTask task : tasks) {
                final Optional<PluginSchedulerInfo> optional = schedulerInfoList.stream()
                        .filter(i -> i.getPlugin().equalsIgnoreCase(task.getOwner().getDescription().getName())).findFirst();

                PluginSchedulerInfo info;
                if (optional.isPresent()) {
                    info = optional.get();
                } else {
                    info = new PluginSchedulerInfo(task.getOwner().getDescription().getName(), 0, 0);

                    schedulerInfoList.add(info);
                }

                total++;
                info.setTotal(info.getTotal() + 1);

                final Method getRunning = ReflectionUtils.getMethod(task.getClass(), "getRunning");
                final AtomicBoolean isRunning = (AtomicBoolean) getRunning.invoke(task);
                if (isRunning.get()) {
                    running++;

                    info.setRunning(info.getRunning() + 1);
                }
            }

            schedulerInfoList.add(0, new PluginSchedulerInfo("All Plugins", running, total));

            return schedulerInfoList;
        } catch (Exception e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }

    private Map<String, String> getScripts() {
        final LinkedHashMap<String, String> scripts = Maps.newLinkedHashMap();

        BungeeUtilisals.getInstance().getScripts().forEach(script -> scripts.put(script.getFile(), script.getScript().replace("\r\n", " ").replace("\t", "    ")));

        return scripts;
    }

    @Override
    public List<String> getCompletions(User user, String[] strings) {
        return ImmutableList.of();
    }
}
