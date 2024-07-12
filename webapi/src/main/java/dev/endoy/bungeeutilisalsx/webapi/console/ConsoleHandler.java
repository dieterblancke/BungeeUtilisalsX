package dev.endoy.bungeeutilisalsx.webapi.console;

import dev.endoy.bungeeutilisalsx.common.BuX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ConsoleHandler
{

    public ConsoleHandler()
    {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute( this::readForInput );
    }

    private void readForInput()
    {
        final Scanner scanner = new Scanner( System.in );

        while ( true )
        {
            final String cmd = scanner.nextLine();
            final String[] args = cmd.split( " " );

            BuX.getInstance().getCommandManager().findCommandByName( args[0] )
                    .ifPresent( command -> command.execute( BuX.getApi().getConsoleUser(), Arrays.copyOfRange( args, 1, args.length ) ) );
        }
    }
}
