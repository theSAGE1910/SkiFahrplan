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

    /**
     * Attempts to construct a new ski area graph from the provided file path.
     * If parsing is successful, it safely overwrites the currently active map and
     * implicitly aborts any ongoing route to prevent spatial inconsistencies.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length == 3 && parts[1].equals("area")) {
            String filepath = parts[2];
            try {
                SkiArea parsedArea = AreaParser.parse(filepath);

                if (parsedArea != null) {
                    session.setSkiArea(parsedArea);
                    session.setPlannedRoute(null);
                    session.setCurrentRouteIndex(0);
                    session.setNextWasCalled(false);
                }
            } catch (IOException e) {
                System.err.println("Error, File not found");
            }
        } else {
            System.err.println("Error, Invalid syntax.");
        }
    }
}