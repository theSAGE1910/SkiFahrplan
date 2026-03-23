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
        if (parts.length == 2 && parts[1].equals("route")) {
            if (session.getPlannedRoute() == null || session.getCurrentRouteIndex() >= session.getRouteEndIndex()) {
                System.err.println("Error, No route planned or route already finished.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = session.getCurrentRouteIndex(); i < session.getRouteEndIndex(); i++) {
                sb.append(session.getPlannedRoute().getPath().get(i).getId()).append(" ");
            }
            System.out.println(sb.toString().trim());

            session.setNextWasCalled(false);

        } else {
            System.err.println("Error, Invalid syntax. Use: show route");
        }
    }
}