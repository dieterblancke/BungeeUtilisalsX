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

package com.dbsoftwares.bungeeutilisals.api.experimental.connection;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.Connection;

import java.net.InetSocketAddress;

public class BungeeConnection implements Connection {

    @Override
    public void disconnect(String arg0) { }

    @Override
    public void disconnect(BaseComponent... arg0) { }

    @Override
    public void disconnect(BaseComponent arg0) { }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public InetSocketAddress getAddress() {
        return null;
    }

    @Override
    public Unsafe unsafe() {
        return null;
    }

}