package be.dieterblancke.bungeeutilisalsx.webapi.query.report;

import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.Report;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.User;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportResolver implements GraphQLResolver<Report>
{

    private final UserService userService;

    public User getUser( final Report report )
    {
        return userService.findByName( report.getUserId() );
    }

    public User getReporter( final Report report )
    {
        return userService.findByName( report.getReporterId() );
    }
}
