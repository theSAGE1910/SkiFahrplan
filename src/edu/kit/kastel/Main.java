package edu.kit.kastel;

/**
 * Serves as the main entry point for the Ski Route Planner application.
 * This class initializes the core components and starts the interactive command loop.
 *
 * @author uxuwg
 * @version 0.1
 */
public final class Main {

    private Main() {
    }

    /**
     * Starts the application by initializing a new {@link SkiArea} and {@link Skier},
     * and then passing them to the {@link CommandProcessor} to begin the interactive loop.
     *
     * @param args the command-line arguments (currently unused in this application)
     */
    public static void main(String[] args) {
        SkiArea skiArea = new SkiArea();
        Skier skier = new Skier();

        CommandProcessor processor = new CommandProcessor(skiArea, skier);
        processor.run();
    }
}