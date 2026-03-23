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

    private static final String CMD_QUIT = "quit";
    private static final String CMD_SET = "set";
    private static final String CMD_LOAD = "load";
    private static final String CMD_LIST = "list";
    private static final String CMD_LIKE = "like";
    private static final String CMD_DISLIKE = "dislike";
    private static final String CMD_RESET = "reset";
    private static final String CMD_PLAN = "plan";
    private static final String CMD_SHOW = "show";
    private static final String CMD_NEXT = "next";
    private static final String CMD_TAKE = "take";
    private static final String CMD_ALTERNATIVE = "alternative";
    private static final String CMD_ABORT = "abort";

    private static final String REGEX_SPACE = " ";
    private static final String ERROR_UNKNOWN_COMMAND = "Error, Unknown command: ";

    private static final int COMMAND_INDEX = 0;

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

        commands.put(CMD_QUIT, new QuitCommand());
        commands.put(CMD_SET, new SetCommand());
        commands.put(CMD_LOAD, new LoadCommand());
        commands.put(CMD_LIST, new ListCommand());
        commands.put(CMD_LIKE, new PreferenceCommand(true));
        commands.put(CMD_DISLIKE, new PreferenceCommand(false));
        commands.put(CMD_RESET, new ResetCommand());
        commands.put(CMD_PLAN, new PlanCommand());
        commands.put(CMD_SHOW, new ShowCommand());
        commands.put(CMD_NEXT, new NextCommand());
        commands.put(CMD_TAKE, new TakeCommand());
        commands.put(CMD_ALTERNATIVE, new AlternativeCommand());
        commands.put(CMD_ABORT, new AbortCommand());
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
        String[] parts = line.split(REGEX_SPACE);
        String commandKey = parts[COMMAND_INDEX];

        if (!commandKey.equals(CMD_NEXT) && !commandKey.equals(CMD_TAKE) && !commandKey.equals(CMD_ALTERNATIVE)) {
            session.setNextWasCalled(false);
        }

        Command command = commands.get(commandKey);
        if (command != null) {
            command.execute(parts, session);
        } else {
            System.err.println(ERROR_UNKNOWN_COMMAND + commandKey);
        }
    }
}