package com.dbsoftwares.bungeeutilisals.bungee.newevent;

import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.bungee.event.EventLoader;
import org.junit.Test;


/*
 * Created by DBSoftwares on 14/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class EventLoaderTest {

    @Test
    public void testEventLoader() {
        EventLoader loader = new EventLoader();

        loader.register(TestEvent.class, new EventExecutor() {

            @Event
            public void onChat(TestEvent event) {
                System.out.println("Chat event 1 executed: " + event.getMessage());
                event.setCancelled(true);
            }

            @Event(priority = Priority.HIGHEST, executeIfCancelled = false)
            public void onChat2(TestEvent event) {
                System.out.println("Chat event 2 executed: " + event.getMessage());
            }

            @Event(priority = Priority.LOWEST)
            public void onChat3(TestEvent event) {
                System.out.println("Chat event 3 executed: " + event.getMessage());
            }

        });

        loader.launchEvent(new TestEvent("Hello World!"));
    }
}