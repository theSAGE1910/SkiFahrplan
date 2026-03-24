package edu.kit.kastel;

import java.io.IOException;

/**
 * Command responsible for reading and parsing a new ski area map from a specified file.
 * This sets up the fundamental graph structure required for the routing algorithm to function.
 *
 * @author uxuwg
 * @version 0.1
 */
public class LoadCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 3;
    private static final int ARG_SUBCOMMAND_INDEX = 1;
    private static final int ARG_PATH_INDEX = 2;
    private static final int DEFAULT_ROUTE_INDEX = 0;

    private static final String SUBCOMMAND_AREA = "area";
    private static final String ERROR_FILE_NOT_FOUND = "Error, File not found";

    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length == EXPECTED_ARGS_LENGTH && parts[ARG_SUBCOMMAND_INDEX].equals(SUBCOMMAND_AREA)) {
            String filepath = parts[ARG_PATH_INDEX];
            try {
                SkiArea parsedArea = AreaParser.parse(filepath);

                if (parsedArea != null) {
                    session.setSkiArea(parsedArea);
                    session.setPlannedRoute(null);
                    session.setCurrentRouteIndex(DEFAULT_ROUTE_INDEX);
                    session.setNextWasCalled(false);
                }
            } catch (IOException e) {
                System.err.println(ERROR_FILE_NOT_FOUND);
            }
        } else {
            System.err.println(ErrorHandler.ERROR_INVALID_SYNTAX);
        }
    }
}