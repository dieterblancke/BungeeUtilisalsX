package dev.endoy.bungeeutilisalsx.webapi.auth;

import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiToken;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.webapi.exception.InsufficientPermissionsException;
import dev.endoy.bungeeutilisalsx.webapi.exception.InvalidApiKeyException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiPermissionAspect
{

    @Around( "@annotation(permission)" )
    public Object onRequiresPermissionExecution( final ProceedingJoinPoint joinPoint, final RequiresPermission permission ) throws Throwable
    {
        if ( !ConfigFiles.CONFIG.getConfig().getBoolean( "auth-enabled", true ) )
        {
            return joinPoint.proceed();
        }
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ( authentication == null || !authentication.isAuthenticated() || !( authentication.getPrincipal() instanceof ApiToken ) )
        {
            throw new InvalidApiKeyException( "An invalid api key was provided" );
        }
        final ApiToken apiToken = (ApiToken) authentication.getPrincipal();

        if ( !apiToken.getPermissions().contains( permission.value() ) && !apiToken.getPermissions().contains( ApiPermission.ALL ) )
        {
            throw new InsufficientPermissionsException(
                    "The provided API key does not have the '" + permission.value() + "' permission!"
            );
        }

        return joinPoint.proceed();
    }
}
