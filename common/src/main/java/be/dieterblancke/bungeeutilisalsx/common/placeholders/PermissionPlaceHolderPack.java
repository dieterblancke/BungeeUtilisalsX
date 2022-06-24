package be.dieterblancke.bungeeutilisalsx.common.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

public class PermissionPlaceHolderPack implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( "{permission_user_prefix}", true, this::getPermissionUserPrefix );
        PlaceHolderAPI.addPlaceHolder( "{permission_user_suffix}", true, this::getPermissionUserSuffix );
        PlaceHolderAPI.addPlaceHolder( "{permission_user_primary_group}", true, this::getPermissionUserPrimaryGroup );
    }

    private String getPermissionUserPrefix( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getPrefix( event.getUser().getUuid() );
    }

    private String getPermissionUserSuffix( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getSuffix( event.getUser().getUuid() );
    }

    private String getPermissionUserPrimaryGroup( final PlaceHolderEvent event )
    {
        return BuX.getInstance().getActivePermissionIntegration().getGroup( event.getUser().getUuid() ).join();
    }
}
