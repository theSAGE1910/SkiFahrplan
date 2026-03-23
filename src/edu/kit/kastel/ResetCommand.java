package edu.kit.kastel;

/**
 * Command responsible for clearing the skier's surface and difficulty preferences.
 * By wiping the likes and dislikes, this command reverts the tie-breaking behavior
 * of the routing algorithm to its default state and forces a dynamic replan.
 *
 * @author uxuwg
 * @version 0.1
 */
public class ResetCommand implements Command {

    /**
     * Executes the preference reset operation. If a route is currently active,
     * resetting the preferences will immediately trigger a dynamic recalculation
     * of the route from the skier's current position.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length == 2 && parts[1].equals("preferences")) {
            session.getSkier().resetPreferences();
            session.triggerDynamicReplan();
        } else {
            System.err.println("Error, Invalid syntax. Use: reset preferences.");
        }
    }
}