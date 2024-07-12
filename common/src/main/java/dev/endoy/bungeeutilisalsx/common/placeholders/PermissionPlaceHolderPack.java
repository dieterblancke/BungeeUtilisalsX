package dev.endoy.bungeeutilisalsx.common.placeholders;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderPack;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

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
