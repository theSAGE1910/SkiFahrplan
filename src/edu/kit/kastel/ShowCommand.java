package edu.kit.kastel;

/**
 * Command responsible for outputting the sequence of the currently planned route.
 * It provides the user with a space-separated list of all remaining nodes the skier
 * is scheduled to visit before the end of the day.
 *
 * @author uxuwg
 * @version 0.1
 */
public class ShowCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 2;
    private static final int ARG_TARGET_INDEX = 1;
    private static final String TARGET_ROUTE = "route";

    private static final String SEPARATOR_SPACE = " ";

    private static final String ERROR_NO_ROUTE_OR_FINISHED = "Error, No route planned or route already finished.";
    private static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax. Use: show route";

    /**
     * Executes the route display logic. Constructs a string containing the IDs of
     * the path nodes starting from the skier's current index up to the final destination.
     * Note that executing this command breaks the strict 'next' to 'take' sequence.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length == EXPECTED_ARGS_LENGTH && parts[ARG_TARGET_INDEX].equals(TARGET_ROUTE)) {
            if (session.getPlannedRoute() == null || session.getCurrentRouteIndex() >= session.getRouteEndIndex()) {
                System.err.println(ERROR_NO_ROUTE_OR_FINISHED);
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = session.getCurrentRouteIndex(); i < session.getRouteEndIndex(); i++) {
                sb.append(session.getPlannedRoute().getPath().get(i).getId()).append(SEPARATOR_SPACE);
            }
            System.out.println(sb.toString().trim());

            session.setNextWasCalled(false);

        } else {
            System.err.println(ERROR_INVALID_SYNTAX);
        }
    }
}