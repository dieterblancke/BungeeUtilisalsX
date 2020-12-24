package be.dieterblancke.bungeeutilisalsx.spigot.gui.opener;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.Gui;
import be.dieterblancke.bungeeutilisalsx.spigot.api.gui.GuiOpener;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.DefaultGui;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiConfig;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.friend.FriendGuiItemProvider;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FriendGuiOpener extends GuiOpener
{

    // This cache is really just to avoid people spamming the database by spam opening the friends gui
    private static final LoadingCache<UUID, List<FriendData>> USER_FRIENDS_CACHE = CacheHelper.<UUID, List<FriendData>>builder()
            .build(
                    builder -> builder.expireAfterWrite( 15, TimeUnit.SECONDS ),
                    new CacheLoader<UUID, List<FriendData>>()
                    {
                        @Override
                        public List<FriendData> load( final UUID uuid )
                        {
                            return BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( uuid );
                        }
                    }
            );

    public FriendGuiOpener()
    {
        super( "friend" );
    }

    @Override
    public void openGui( final Player player, final String[] args )
    {
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );
        List<FriendData> friends;

        try
        {
            friends = USER_FRIENDS_CACHE.get( player.getUniqueId() );
        }
        catch ( ExecutionException e )
        {
            friends = BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( player.getUniqueId() );
        }

        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            final FriendGuiConfig config = DefaultGui.FRIEND.getConfig();
            final Gui gui = Gui.builder()
                    .itemProvider( new FriendGuiItemProvider( player, config, friends ) )
                    .rows( config.getRows() )
                    .title( config.getTitle() )
                    .players( player )
                    .build();

            gui.open();
        }
    }
}
