package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;

public class UserPlaceHolderPack implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{user}", true, this::getUserName );
        PlaceHolderAPI.addPlaceHolder( "{me}", true, this::getUserName );
        PlaceHolderAPI.addPlaceHolder( "{user_prefix}", true, this::getUserPrefix );
        PlaceHolderAPI.addPlaceHolder( "{ping}", true, this::getUserPing );
        PlaceHolderAPI.addPlaceHolder( "{server}", true, this::getServerName );
        PlaceHolderAPI.addPlaceHolder( "{server_online}", true, this::getServerCount );
        PlaceHolderAPI.addPlaceHolder( "{language_short}", true, this::getShortLanguage );
        PlaceHolderAPI.addPlaceHolder( "{language_long}", true, this::getLongLanguage );
    }

    private String getUserName( final PlaceHolderEvent event )
    {
        return event.getUser().getName();
    }

    private String getUserPing( final PlaceHolderEvent event )
    {
        return String.valueOf( event.getUser().getPing() );
    }

    private String getUserPrefix( final PlaceHolderEvent event )
    {
        return StaffUtils.getStaffRankForUser( event.getUser() )
                .map( StaffRankData::getDisplay )
                .orElse( "" );
    }

    private String getServerName( final PlaceHolderEvent event )
    {
        return event.getUser().getServerName();
    }

    private String getServerCount( final PlaceHolderEvent event )
    {
        if ( event.getUser().getServerName().trim().isEmpty() )
        {
            return "0";
        }
        return String.valueOf( BuX.getApi().getPlayerUtils().getPlayerCount( event.getUser().getServerName() ) );
    }

    private String getShortLanguage( final PlaceHolderEvent event )
    {
        return event.getUser().getLanguageTagShort();
    }

    private String getLongLanguage( final PlaceHolderEvent event )
    {
        return event.getUser().getLanguageTagLong();
    }
}
