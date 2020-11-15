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

package com.dbsoftwares.bungeeutilisals;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.utils.player.IPlayerUtils;
import com.dbsoftwares.bungeeutilisalsx.common.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.bossbar.BossBar;
import com.dbsoftwares.bungeeutilisalsx.bungee.bridging.BridgeManager;
import com.dbsoftwares.bungeeutilisalsx.bungee.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisalsx.bungee.bridging.bungee.types.UserActionType;
import com.dbsoftwares.bungeeutilisalsx.bungee.bridging.bungee.util.BridgedUserMessage;
import com.dbsoftwares.bungeeutilisals.event.EventLoader;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.HubBalancer;
import com.dbsoftwares.bungeeutilisals.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisals.punishments.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.utils.APIHandler;
import com.dbsoftwares.bungeeutilisalsx.bungee.utils.player.BungeePlayerUtils;
import com.dbsoftwares.bungeeutilisalsx.bungee.utils.player.RedisPlayerUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.*;

@Getter
public class BUtilisalsAPI implements BUAPI
{

    private final BungeeUtilisals plugin;
    private final ConsoleUser console;
    private final List<User> users;
    private final IChatManager chatManager;
    private final IEventLoader eventLoader;
    private final ILanguageManager languageManager;
    private final IPunishmentExecutor punishmentExecutor;
    private final IPlayerUtils playerUtils;
    private final IBridgeManager bridgeManager;
    private IHubBalancer hubBalancer;

    BUtilisalsAPI( BungeeUtilisals plugin )
    {
        APIHandler.registerProvider( this );

        this.plugin = plugin;
        this.console = new ConsoleUser();
        this.users = Collections.synchronizedList( Lists.newArrayList() );
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new PluginLanguageManager( plugin );
        this.punishmentExecutor = new PunishmentExecutor();
        this.bridgeManager = new BridgeManager();
        this.bridgeManager.setup();
        this.playerUtils = bridgeManager.useBridging() ? new RedisPlayerUtils() : new BungeePlayerUtils();

        if ( ConfigFiles.HUBBALANCER.isEnabled() )
        {
            this.hubBalancer = new HubBalancer();
        }
    }

    @Override
    public Collection<Announcer> getAnnouncers()
    {
        return Announcer.getAnnouncers().values();
    }

    @Override
    public AbstractStorageManager getStorageManager()
    {
        return AbstractStorageManager.getManager();
    }

    @Override
    public IBossBar createBossBar()
    {
        return new BossBar();
    }

    @Override
    public IBossBar createBossBar( BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return createBossBar( UUID.randomUUID(), color, style, progress, message );
    }

    @Override
    public IBossBar createBossBar( UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return new BossBar( uuid, color, style, progress, message );
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return BungeeUtilisals.getInstance().getStaffMembers();
    }
}
