package edu.kit.kastel;

/**
 * Represents an executable command within the interactive ski planner.
 *
 * @author uxuwg
 * @version 0.1
 */
public interface Command {
    /**
     * Executes the specific logic for this command.
     *
     * @param parts The user input split by spaces
     * @param session The current state of the application
     */
    void execute(String[] parts, SkiSession session);
}
