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

    /**
     * Outputs the identifier of the next scheduled lift or piste. If the skier has
     * already reached the final destination base station, it formally concludes
     * the route and clears the planning state.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != 1) {
            System.err.println("Error, next takes no arguments.");
            return;
        }
        if (session.getPlannedRoute() == null) {
            System.err.println("Error, No route planned.");
            return;
        }

        if (session.getCurrentRouteIndex() >= session.getRouteEndIndex()) {
            System.out.println("route finished!");
            session.setPlannedRoute(null);
            session.setNextWasCalled(false);
        } else {
            System.out.println(session.getPlannedRoute().getPath().get(session.getCurrentRouteIndex()).getId());
            session.setNextWasCalled(true);
        }
    }
}