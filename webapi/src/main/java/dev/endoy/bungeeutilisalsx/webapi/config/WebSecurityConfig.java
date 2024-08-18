package dev.endoy.bungeeutilisalsx.webapi.config;

import dev.endoy.bungeeutilisalsx.webapi.auth.ApiTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

    private final ApiTokenFilter apiTokenFilter;

    @Override
    protected void configure( HttpSecurity http ) throws Exception
    {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS )
            .and()
            .addFilterBefore( apiTokenFilter, RequestHeaderAuthenticationFilter.class );
    }
}