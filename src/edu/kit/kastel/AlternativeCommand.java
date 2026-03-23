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
        if (parts.length != 1) {
            System.err.println("Error, alternative takes no arguments.");
            return;
        }
        if (session.getPlannedRoute() == null || !session.isNextWasCalled()) {
            System.err.println("Error, alternative must be called immediately after next");
            return;
        }
        if (session.getCurrentRouteIndex() == 0) {
            System.err.println("Error, Cannot calculate alternative for the starting base station");
            return;
        }

        SkiNode nodeToAvoid = session.getPlannedRoute().getPath().get(session.getCurrentRouteIndex());
        Route truncatedRoute = session.getPlannedRoute().getTruncatedRoute(session.getCurrentRouteIndex() - 1);

        RoutePlanner planner = new RoutePlanner(session.getSkiArea(), session.getSkier());
        Route alternativeRoute = planner.findAlternativeRoute(truncatedRoute, session.getActiveEndTime(), nodeToAvoid);

        if (alternativeRoute == null) {
            System.out.println("no alternative found");
        } else {
            System.out.println("avoided " + nodeToAvoid.getId());
            session.setPlannedRoute(alternativeRoute);
        }

        session.setNextWasCalled(false);
    }
}