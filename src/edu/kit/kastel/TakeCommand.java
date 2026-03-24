package edu.kit.kastel;

/**
 * Command responsible for advancing the skier to the next node in their planned route.
 * This command simulates the physical action of riding a lift or skiing down a piste,
 * moving the skier's state forward by one step.
 *
 * @author uxuwg
 * @version 0.1
 */
public class TakeCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 1;

    private static final String ERROR_INVALID_ARGS = "Error, take takes no arguments.";
    private static final String ERROR_MISSING_NEXT = "Error, take must be called immediately after next.";

    /**
     * Executes the progression step by incrementing the skier's current position index.
     * Enforces the strict rule that this action can only occur immediately after
     * the user has previewed the upcoming node using the 'next' command.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_ARGS);
            return;
        }

        if (session.isNextNotCalled()) {
            System.err.println(ERROR_MISSING_NEXT);
            return;
        }

        if (!session.advanceToNextNode()) {
            System.err.println(ErrorHandler.ERROR_NO_ROUTE_OR_FINISHED);
        }
    }
}