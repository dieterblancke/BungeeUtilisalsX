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

package com.dbsoftwares.bungeeutilisals.commands.addons.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.AddonData;
import com.dbsoftwares.bungeeutilisals.api.chat.message.ChatMessage;
import com.dbsoftwares.bungeeutilisals.api.chat.message.ClickPartim;
import com.dbsoftwares.bungeeutilisals.api.chat.message.HoverPartim;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.List;

public class AddonListSubCommand extends SubCommand {

    public AddonListSubCommand() {
        super("list", 0, 1);
    }

    @Override
    public String getUsage() {
        return "/addons list [page]";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.addons.list";
    }

    @Override
    public void onExecute(User user, String[] args) {
        final List<AddonData> addons = BUCore.getApi().getAddonManager().getAllAddons();
        final int maxPages = (int) Math.ceil(addons.size() / 10.0);
        int page = 1;
        if (args.length != 0) {
            if (!MathUtils.isInteger(args[0])) {
                user.sendLangMessage("no-number");
                return;
            }
            page = Integer.parseInt(args[0]);
        }

        if (page > maxPages) {
            user.sendLangMessage("general-commands.addon.list.wrong-page", "{maxpages}", maxPages);
            return;
        }

        final int begin = ((page - 1) * 10);
        int end = begin + 10;

        if (end > addons.size()) {
            end = addons.size();
        }

        user.sendLangMessage("general-commands.addon.list.header", "{page}", page);
        final ChatMessage chatMessage = new ChatMessage();

        for (int i = begin; i < end; i++) {
            final AddonData data = addons.get(i);
            final ChatColor color = BUCore.getApi().getAddonManager().isRegistered(data.getName()) ? ChatColor.GREEN : ChatColor.RED;
            final String message = user.buildLangMessage("general-commands.addon.list.item.text", "{id}", i + 1, "{addon}", color + data.getName());

            chatMessage.addPartim(
                    message,
                    new HoverPartim(
                            HoverEvent.Action.SHOW_TEXT,
                            user.buildLangMessage("general-commands.addon.list.item.hover",
                                    "{id}", i + 1,
                                    "{name}", data.getName(),
                                    "{version}", data.getVersion(),
                                    "{author}", data.getAuthor(),
                                    "{reqDepends}", data.getRequiredDependencies() == null ? "None" : Utils.formatList(data.getRequiredDependencies(), ", "),
                                    "{optDepends}", data.getOptionalDependencies() == null ? "None" : Utils.formatList(data.getOptionalDependencies(), ", "),
                                    "{description}", data.getDescription()
                            )
                    ),
                    new ClickPartim(
                            ClickEvent.Action.RUN_COMMAND,
                            "/addon info " + data.getName()
                    )
            );
            if (i < end - 1) {
                chatMessage.newLine();
            }
        }
        chatMessage.sendTo(user);
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }

    /*
        if(args.length == 2 && (args[0].contains("install") || args[0].contains("load"))){
            String addonname = args[1];

            JsonObject obj = JSONUtil.readJSONFromURL("http://dbsoftwares.be/plugins/addons/addons.html");
            for(Map.Entry<String, JsonElement> e : obj.entrySet()){
                if(e.getKey().equalsIgnoreCase(addonname)){
                    JsonObject o = e.getValue().getAsJsonObject();

                    String name = e.getKey();
                    String link = o.get("link").getAsString();
                    Integer version = o.get("version").getAsInt();

                    if(AddonManager.getInstance().isRegistered(addonname)){
                        Addon addon = AddonManager.getInstance().getAddon(addonname);
                        user.sendMessage("&6Unregistering " + addonname);
                        AddonManager.getInstance().unregisterAddon(addon);
                    }

                    user.sendMessage("&6Downloading " + name  + " v" + version);
                    File file = AddonManager.getInstance().download(name, link);
                    if(file == null){
                        user.sendMessage("&6Could not download addon! Check console for errors.");
                        return;
                    }
                    user.sendMessage("&6Downloaded addon! Enabling addon now ...");
                    try {
                        Addon addon = AddonManager.getInstance().loadAddon(file);
                        if(addon == null){
                            user.sendMessage("&6An error occured while trying to load the addon ...");
                            return;
                        }
                        CMS.getInstance().getAddonManager().registerAddon(addon);
                        user.sendMessage("&6The addon &b" + name + " &6should have been enabled!");
                    } catch(InvalidAddonException ex){
                        ex.printStackTrace();
                    }
                    return;
                }
            }
            user.sendMessage("&6This addon doesn't exist!");
            return;
        }
        if(args.length == 2 && args[0].contains("enable")){
            String addonname = args[1];

            if(AddonManager.getInstance().isRegistered(addonname)){
                user.sendMessage("&6This addon is already enabled!");
                return;
            }
            File file = AddonManager.getInstance().searchAddon(addonname);
            if(file == null){
                user.sendMessage("&6Could not find this addon ...");
                return;
            }
            try {
                Addon addon = AddonManager.getInstance().loadAddon(file);
                if(addon == null){
                    user.sendMessage("&6An error occured while trying to load the addon ...");
                    return;
                }

                CMS.getInstance().getAddonManager().registerAddon(addon);
                user.sendMessage("&6The addon &b" + addonname + " &6should have been enabled!");
            } catch(InvalidAddonException e){
                BUCore.getLogger().error("An error occured: ", e)();
            }
            return;
        }
        if(args.length == 2 && args[0].contains("disable")){
            String addonname = args[1];

            if(!AddonManager.getInstance().isRegistered(addonname)){
                user.sendMessage("&6This addon is not enabled!");
                return;
            }
            Addon addon = AddonManager.getInstance().getAddon(addonname);
            AddonManager.getInstance().unregisterAddon(addon);
            user.sendMessage("&6The addon &b" + addonname + " &6has been disabled!");
            return;
        }
        if(args.length == 0){
            user.sendMessage("&bAddon help:");
            user.sendMessage("&6- /addon list (page)");
            user.sendMessage("&6- /addon install (name)");
            user.sendMessage("&6- /addon uninstall (name)");
            user.sendMessage("&6- /addon enable (name)");
            user.sendMessage("&6- /addon disable (name)");
            user.sendMessage("&6- /addon reload (name)");
        }

     */
}
