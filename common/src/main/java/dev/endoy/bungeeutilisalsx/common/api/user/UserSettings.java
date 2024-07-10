package dev.endoy.bungeeutilisalsx.common.api.user;

import dev.endoy.bungeeutilisalsx.common.BuX;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserSettings
{

    private final UUID uuid;
    private final List<UserSetting> settings;

    public Optional<UserSetting> getUserSetting( UserSettingType userSettingType )
    {
        return settings
                .stream()
                .filter( it -> it.getSettingType() == userSettingType )
                .findFirst();
    }

    public void upsertUserSetting( UserSetting userSetting )
    {
        this.getUserSetting( userSetting.getSettingType() )
                .ifPresentOrElse( setting ->
                {
                    setting.setValue( userSetting.getValue() );
                    BuX.getApi().getStorageManager().getDao().getUserDao().updateSetting(
                            uuid,
                            setting.getSettingType(),
                            setting.getValue()
                    );
                }, () ->
                {
                    this.settings.add( userSetting );

                    BuX.getApi().getStorageManager().getDao().getUserDao().registerSetting(
                            uuid,
                            userSetting.getSettingType(),
                            userSetting.getValue()
                    );
                } );
    }

    public void removeUserSetting( UserSetting userSetting )
    {
        if ( this.settings.contains( userSetting ) )
        {
            this.settings.remove( userSetting );

            BuX.getApi().getStorageManager().getDao().getUserDao().removeSetting( uuid, userSetting.getSettingType() );
        }
    }
}
