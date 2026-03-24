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

    private static final int EXPECTED_ARGS_LENGTH = 1;

    private static final String ERROR_INVALID_ARGS = "Error, abort takes no arguments.";
    private static final String ERROR_NO_ROUTE = "Error, No route to abort.";
    private static final String SUCCESS_MESSAGE = "route aborted";

    /**
     * Terminates the active route and completely resets the skier's progress state.
     * Validates that a route is actually running before attempting to clear it.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_ARGS);
        } else if (!session.abortRoute()) {
            System.err.println(ERROR_NO_ROUTE);
        } else {
            System.out.println(SUCCESS_MESSAGE);
        }
    }
}