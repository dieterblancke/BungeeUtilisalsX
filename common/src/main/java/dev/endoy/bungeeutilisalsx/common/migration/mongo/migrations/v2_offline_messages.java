package dev.endoy.bungeeutilisalsx.common.migration.mongo.migrations;

import dev.endoy.bungeeutilisalsx.common.migration.mongo.MongoMigration;

public class v2_offline_messages implements MongoMigration
{
    @Override
    public boolean shouldRun() throws Exception
    {
        return true;
    }

    @Override
    public void migrate() throws Exception
    {
        db().getCollection( "bu_messagequeue" ).drop();
    }
}
