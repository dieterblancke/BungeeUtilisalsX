package dev.endoy.bungeeutilisalsx.webapi.auth;

import dev.endoy.bungeeutilisalsx.common.BuX;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ApiTokenFilter extends OncePerRequestFilter
{

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Pattern BEARER_PATTERN = Pattern.compile( "^Bearer (.+?)$" );

    @Override
    protected void doFilterInternal( @NotNull final HttpServletRequest request,
                                     @NotNull final HttpServletResponse response,
                                     final FilterChain filterChain ) throws IOException, ServletException
    {
        getToken( request )
                .flatMap( BuX.getApi().getStorageManager().getDao().getApiTokenDao()::findApiToken )
                .map( apiToken -> new PreAuthenticatedAuthenticationToken(
                        apiToken,
                        new WebAuthenticationDetailsSource().buildDetails( request ),
                        null
                ) )
                .ifPresent( authentication -> SecurityContextHolder.getContext().setAuthentication( authentication ) );

        filterChain.doFilter( request, response );
    }

    private Optional<String> getToken( final HttpServletRequest request )
    {
        return Optional
                .ofNullable( request.getHeader( AUTHORIZATION_HEADER ) )
                .filter( Strings::isNotEmpty )
                .map( BEARER_PATTERN::matcher )
                .filter( Matcher::find )
                .map( matcher -> matcher.group( 1 ) );
    }
}
