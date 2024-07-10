package dev.endoy.bungeeutilisalsx.webapi.auth;

import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface RequiresPermission
{
    ApiPermission value();
}
