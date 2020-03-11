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

package com.dbsoftwares.bungeeutilisals.storage.mongo;

import com.dbsoftwares.bungeeutilisals.TestUtils;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

public class MongoUtils
{

    private MongodExecutable MONGOD_EXEC;
    private MongodProcess MONGOD;
    private MongoClient MONGO;

    public void setup() throws IOException
    {
        TestUtils.setup();
        final MongodStarter starter = MongodStarter.getDefaultInstance();
        final String bindIp = "localhost";
        final int port = 12345;
        final IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version( Version.Main.PRODUCTION )
                .net( new Net( bindIp, port, Network.localhostIsIPv6() ) )
                .build();

        MONGOD_EXEC = starter.prepare( mongodConfig );
        MONGOD = MONGOD_EXEC.start();
        MONGO = new MongoClient( bindIp, port );
        final MongoDatabase db = MONGO.getDatabase( "embedded" );

        new MongoDBStorageManager( MONGO, db );
    }

    public void destroy()
    {
        if ( MONGOD != null )
        {
            MONGOD.stop();
            MONGOD_EXEC.stop();
        }
    }
}
