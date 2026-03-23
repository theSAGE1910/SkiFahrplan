package edu.kit.kastel;

/**
 * Command responsible for calculating an alternative path for the skier.
 * When executed, this command attempts to replace the remainder of the active route
 * by finding a new optimal path that explicitly avoids the immediate next node
 * that was previously scheduled.
 *
 * @author uxuwg
 * @version 0.1
 */
public class AlternativeCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 1;
    private static final int STARTING_INDEX = 0;
    private static final int TRUNCATION_OFFSET = 1;

    private static final String ERROR_INVALID_ARGS = "Error, alternative takes no arguments.";
    private static final String ERROR_WRONG_STATE = "Error, alternative must be called immediately after next";
    private static final String ERROR_STARTING_NODE = "Error, Cannot calculate alternative for the starting base station";
    private static final String MSG_NO_ALTERNATIVE = "no alternative found";
    private static final String MSG_AVOIDED_PREFIX = "avoided ";

    /**
     * Executes the alternative route calculation. Validates that the command was
     * called strictly after a 'next' command and that the skier is not currently
     * at the starting base station before invoking the routing algorithm.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_ARGS);
            return;
        }
        if (session.getPlannedRoute() == null || !session.isNextWasCalled()) {
            System.err.println(ERROR_WRONG_STATE);
            return;
        }
        if (session.getCurrentRouteIndex() == STARTING_INDEX) {
            System.err.println(ERROR_STARTING_NODE);
            return;
        }

        SkiNode nodeToAvoid = session.getPlannedRoute().getPath().get(session.getCurrentRouteIndex());
        Route truncatedRoute = session.getPlannedRoute().getTruncatedRoute(session.getCurrentRouteIndex() - TRUNCATION_OFFSET);

        RoutePlanner planner = new RoutePlanner(session.getSkiArea(), session.getSkier());
        Route alternativeRoute = planner.findAlternativeRoute(truncatedRoute, session.getActiveEndTime(), nodeToAvoid);

        if (alternativeRoute == null) {
            System.out.println(MSG_NO_ALTERNATIVE);
        } else {
            System.out.println(MSG_AVOIDED_PREFIX + nodeToAvoid.getId());
            session.setPlannedRoute(alternativeRoute);
        }

        session.setNextWasCalled(false);
    }
}