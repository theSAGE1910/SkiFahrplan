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
        if (parts.length != 1) {
            System.err.println("Error, take takes no arguments.");
            return;
        }
        if (session.getPlannedRoute() == null || session.getCurrentRouteIndex() >= session.getRouteEndIndex()) {
            System.err.println("Error, No route planned or route already finished.");
            return;
        }
        if (!session.isNextWasCalled()) {
            System.err.println("Error, take must be called immediately after next.");
            return;
        }

        session.setCurrentRouteIndex(session.getCurrentRouteIndex() + 1);
        session.setNextWasCalled(false);
    }
}