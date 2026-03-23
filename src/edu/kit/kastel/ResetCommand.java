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

    private static final int EXPECTED_ARGS_LENGTH = 2;
    private static final int ARG_TARGET_INDEX = 1;

    private static final String TARGET_PREFERENCES = "preferences";
    private static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax. Use: reset preferences.";

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
        if (parts.length == EXPECTED_ARGS_LENGTH && parts[ARG_TARGET_INDEX].equals(TARGET_PREFERENCES)) {
            session.getSkier().resetPreferences();
            session.triggerDynamicReplan();
        } else {
            System.err.println(ERROR_INVALID_SYNTAX);
        }
    }
}