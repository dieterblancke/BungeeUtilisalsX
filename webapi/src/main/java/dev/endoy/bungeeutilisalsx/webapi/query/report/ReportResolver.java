package dev.endoy.bungeeutilisalsx.webapi.query.report;

import dev.endoy.bungeeutilisalsx.webapi.dto.Report;
import dev.endoy.bungeeutilisalsx.webapi.dto.User;
import dev.endoy.bungeeutilisalsx.webapi.service.UserService;
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
