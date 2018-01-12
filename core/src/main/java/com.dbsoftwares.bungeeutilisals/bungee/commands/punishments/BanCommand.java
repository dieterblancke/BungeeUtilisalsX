package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserBanEvent;
import com.dbsoftwares.bungeeutilisals.api.mysql.MySQL;
import com.dbsoftwares.bungeeutilisals.api.mysql.MySQLFinder;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.tables.UserTable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.ban.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ban.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        MySQLFinder<UserTable> finder = MySQL.search(UserTable.class).select("uuid, username, ip")
                .where((args[0].contains("-") ? "uuid" : "username") + " = %s", args[0]).search();

        if (!finder.isPresent()) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserTable table = finder.get();

        PunishmentInfo info = new PunishmentInfo();
        info.setUser(table.getUsername());
        info.setUuid(table.getUuid());
        info.setIP(table.getIp());
        info.setBy(user.getName());
        info.setReason(reason);
        info.setServer(user.getParent().getServer().getInfo().getName());
        info.setType(PunishmentType.BAN);

        Optional<User> optionalUser = BUCore.getApi().getUser(table.getUsername());
        if (optionalUser.isPresent()) {
            User banned = optionalUser.get();

            banned.kick(Utils.formatList(banned.getLanguageConfig().getStringList("punishments.ban.kick"), "\n")
                    .replace("%reason%", reason).replace("%bandate%", Utils.getCurrentDate()
                            + " " + Utils.getCurrentTime()).replace("%bannedby%", user.getName()));
        }

        BUCore.getApi().getPunishmentExecutor().addPunishment(PunishmentType.BAN, info);

        UserBanEvent event = new UserBanEvent(table.getUsername(), table.getUuid(), user.getName(), info);
        BUCore.getApi().getEventLoader().launchEvent(event);

        BUCore.getApi().langBroadcast("punishments.ban.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ban.broadcast"),
                "%banner%", user.getName(), "%banned%", table.getUsername(), "%reason%", reason);
    }
}