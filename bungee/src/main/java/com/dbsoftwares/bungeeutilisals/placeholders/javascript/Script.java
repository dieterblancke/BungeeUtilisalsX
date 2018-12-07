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

package com.dbsoftwares.bungeeutilisals.placeholders.javascript;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.hash.Hashing;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Data
public class Script {

    private final static File cacheFolder;

    static {
        cacheFolder = new File(BungeeUtilisals.getInstance().getDataFolder(), "scripts" + File.separator + "cache");

        if (!cacheFolder.exists()) {
            cacheFolder.mkdirs();
        }
    }

    private final String file;
    private final String script;
    private IConfiguration storage;
    private ScriptEngine engine;

    public Script(String file, String script) throws ScriptException, IOException {
        this.file = file;
        this.script = script;

        final File storage = new File(cacheFolder, hash(file));

        if (!storage.exists()) {
            storage.createNewFile();
        }

        this.storage = IConfiguration.loadYamlConfiguration(storage);
        this.engine = loadEngine();
    }

    private ScriptEngine loadEngine() throws ScriptException {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

        engine.put("storage", storage);
        engine.put("proxy", ProxyServer.getInstance());
        engine.put("api", BUCore.getApi());

        engine.eval("function isConsole() { return user === null || user.getClass().getSimpleName() !== 'BUser'; }");

        return engine;
    }

    public String getReplacement(User user) {
        final String script = PlaceHolderAPI.formatMessage(user, this.script);

        engine.put("user", user);

        try {
            return String.valueOf(engine.eval(script));
        } catch (ScriptException e) {
            e.printStackTrace();
            return "SCRIPT ERROR";
        }
    }

    public void unload() {
        final File storage = new File(cacheFolder, hash(file));

        if (storage.length() == 0) {
            storage.delete();
        }
    }

    private static String hash(String str) {
        return Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }
}
