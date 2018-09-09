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

package com.dbsoftwares.bungeeutilisals.event;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import org.junit.Test;

public class EventLoaderTest {

    @Test
    public void testEventLoader() { // not really a real tester, this is just used to test event priority by console output.
        EventLoader loader = new EventLoader();

        loader.register(TestEvent.class, new EventExecutor() {

            @Event
            public void onChat(TestEvent event) {
                BUCore.log("Chat event 1 executed: " + event.getMessage());
                event.setCancelled(true);
            }

            @Event(priority = Priority.HIGHEST, executeIfCancelled = false)
            public void onChat2(TestEvent event) {
                BUCore.log("Chat event 2 executed: " + event.getMessage());
            }

            @Event(priority = Priority.LOWEST)
            public void onChat3(TestEvent event) {
                BUCore.log("Chat event 3 executed: " + event.getMessage());
            }

        });

        loader.launchEvent(new TestEvent("Hello World!"));
    }
}