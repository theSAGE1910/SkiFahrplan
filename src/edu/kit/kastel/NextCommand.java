package edu.kit.kastel;

/**
 * Command responsible for previewing the upcoming node on the skier's planned path.
 * This command acts as a prerequisite for progression actions (like 'take' or 'alternative')
 * by revealing what the immediate next step entails.
 *
 * @author uxuwg
 * @version 0.1
 */
public class NextCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 1;
    private static final String ERROR_INVALID_ARGS = "Error, next takes no arguments.";
    private static final String ERROR_NO_ROUTE = "Error, No route planned.";
    private static final String MSG_FINISHED = "route finished!";

    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_ARGS);
            return;
        }
        if (session.getPlannedRoute() == null) {
            System.err.println(ERROR_NO_ROUTE);
            return;
        }

        if (session.getCurrentRouteIndex() >= session.getRouteEndIndex()) {
            System.out.println(MSG_FINISHED);
            session.setPlannedRoute(null);
            session.setNextWasCalled(false);
        } else {
            System.out.println(session.getPlannedRoute().getPath().get(session.getCurrentRouteIndex()).getId());
            session.setNextWasCalled(true);
        }
    }
}