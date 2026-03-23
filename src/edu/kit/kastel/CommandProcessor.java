package edu.kit.kastel;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The {@code CommandProcessor} manages the interactive command-line interface.
 *
 * @author uxuwg
 * @version 0.1
 */
public class CommandProcessor {

    private final Map<String, Command> commands = new HashMap<>();
    private final SkiSession session;

    /**
     * Constructs a new {@code CommandProcessor}.
     *
     * @param skiArea the {@link SkiArea} graph containing lifts and pistes
     * @param skier   the {@link Skier} whose preferences and skills are tracked
     */
    public CommandProcessor(SkiArea skiArea, Skier skier) {
        this.session = new SkiSession(skiArea, skier);

        commands.put("quit", new QuitCommand());
        commands.put("set", new SetCommand());
        commands.put("load", new LoadCommand());
        commands.put("list", new ListCommand());
        commands.put("like", new PreferenceCommand(true));
        commands.put("dislike", new PreferenceCommand(false));
        commands.put("reset", new ResetCommand());
        commands.put("plan", new PlanCommand());
        commands.put("show", new ShowCommand());
        commands.put("next", new NextCommand());
        commands.put("take", new TakeCommand());
        commands.put("alternative", new AlternativeCommand());
        commands.put("abort", new AbortCommand());
    }

    /**
     * Starts the main interactive loop. Reads commands from the standard input
     * and processes them until the "quit" command is issued.
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) { //scanner.close()

            while (session.isRunning()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                processCommands(line);
            }
        }
    }

    /**
     * Parses a single command line and delegates them to the appropriate {@link Command} instance.
     *
     * @param line the raw command line string entered by the user
     */
    private void processCommands(String line) {
        String[] parts = line.split(" ");
        String commandKey = parts[0];

        if (!commandKey.equals("next") && !commandKey.equals("take") && !commandKey.equals("alternative")) {
            session.setNextWasCalled(false);
        }

        Command command = commands.get(commandKey);
        if (command != null) {
            command.execute(parts, session);
        } else {
            System.err.println("Error, Unknown command: " + commandKey);
        }
    }
}