package com.dbsoftwares.bungeeutilisalsx.common.api.announcer;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.RandomIterator;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@Data
@RequiredArgsConstructor
public class GroupedAnnouncement implements IAnnouncement
{

    private final boolean random;
    private final ServerGroup group;
    private final List<IAnnouncement> announcements;
    private Iterator<IAnnouncement> iterator;

    private IAnnouncement previous;

    @Override
    public void send()
    {
        if ( previous != null )
        {
            previous.clear();
        }
        final IAnnouncement next = getNextAnnouncement();
        next.send();
        previous = next;
    }

    @Override
    public void clear()
    {
        previous.clear();
    }

    private IAnnouncement getNextAnnouncement()
    {
        if ( announcements.isEmpty() )
        {
            return null;
        }
        if ( iterator == null || !iterator.hasNext() )
        {
            iterator = random ? new RandomIterator<>( announcements ) : announcements.iterator();
        }
        return iterator.next();
    }
}
