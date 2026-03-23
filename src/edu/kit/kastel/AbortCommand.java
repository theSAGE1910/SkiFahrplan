package edu.kit.kastel;

/**
 * Command responsible for manually cancelling the currently active route plan.
 * By aborting the route, the skier's path state is cleared, allowing them to
 * start fresh and plan a completely new route.
 *
 * @author uxuwg
 * @version 0.1
 */
public class AbortCommand implements Command {

    /**
     * Terminates the active route and completely resets the skier's progress state.
     * Validates that a route is actually running before attempting to clear it.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != 1) {
            System.err.println("Error, abort takes no arguments.");
            return;
        }
        if (session.getPlannedRoute() == null) {
            System.err.println("Error, No route to abort.");
            return;
        }

        session.setPlannedRoute(null);
        session.setCurrentRouteIndex(0);
        session.setNextWasCalled(false);
        System.out.println("route aborted");
    }
}