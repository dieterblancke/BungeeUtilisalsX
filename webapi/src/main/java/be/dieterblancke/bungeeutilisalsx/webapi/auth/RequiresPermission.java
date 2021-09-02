package be.dieterblancke.bungeeutilisalsx.webapi.auth;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface RequiresPermission
{
    ApiPermission value();
}
