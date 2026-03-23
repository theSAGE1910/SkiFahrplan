package edu.kit.kastel;

/**
 * Command responsible for gracefully terminating the interactive ski session.
 * When executed, this command signals the main application loop to stop processing
 * further inputs, allowing the program to exit cleanly.
 *
 * @author uxuwg
 * @version 0.1
 */
public class QuitCommand implements Command {

    private static final int MAX_ARGS_LENGTH = 1;
    private static final String ERROR_TOO_MANY_ARGS = "Error, the quit command does not take any arguments.";

    /**
     * Executes the termination sequence by updating the session's running state.
     * It validates that no additional arguments were provided by the user before
     * triggering the shutdown.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length > MAX_ARGS_LENGTH) {
            System.err.println(ERROR_TOO_MANY_ARGS);
        } else {
            session.setRunning(false);
        }
    }
}