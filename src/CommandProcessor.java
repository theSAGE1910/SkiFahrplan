import java.util.Scanner;

public class CommandProcessor {

    private final SkiArea skiArea;
    private final Skier skier;
    private boolean isRunning;

    public CommandProcessor(SkiArea skiArea, Skier skier) {
        this.skiArea = skiArea;
        this.skier = skier;
        this.isRunning = true;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (isRunning) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                continue;
            }
            processCommands(line);
        }
    }

    private void processCommands(String line) {
        String[] parts = line.split(" ");
        String command = parts[0];

        switch (command) {
            case "quit" -> {
                handleQuit(parts);
            }
            case "set" -> {
                if (parts.length == 3 && parts[1].equals("skill")) {
                    try {
                        SkillLevel skill = SkillLevel.valueOf(parts[2]);
                        skier.setSkillLevel(skill);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: Invalid skill level");
                    }
                } else if (parts.length == 3 && parts[1].equals("goal")) {
                    try {
                        Goal goal = Goal.valueOf(parts[2]);
                        skier.setGoal(goal);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: Invalid goal name");
                    }
                } else {
                    System.err.println("Error: Invalid command");
                }
            }
        }
    }

    private void handleQuit(String[] parts) {
        if (parts.length > 1) {
            System.err.println("Error: the quit command does not take any arguments.");
        } else  {
            isRunning = false;
        }
    }
}
