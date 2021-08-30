package be.dieterblancke.bungeeutilisalsx.webapi.auth;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiToken;
import be.dieterblancke.bungeeutilisalsx.webapi.exception.InsufficientPermissionsException;
import be.dieterblancke.bungeeutilisalsx.webapi.exception.InvalidApiKeyException;
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
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ( authentication == null || !authentication.isAuthenticated() || !( authentication.getPrincipal() instanceof ApiToken ) )
        {
            throw new InvalidApiKeyException( "An invalid api key was provided" );
        }
        final ApiToken apiToken = (ApiToken) authentication.getPrincipal();

        if ( !apiToken.getPermissions().contains( permission.value() ) )
        {
            throw new InsufficientPermissionsException(
                    "The provided API key does not have the '" + permission.value() + "' permission!"
            );
        }

        return joinPoint.proceed();
    }
}
