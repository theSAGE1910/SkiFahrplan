import java.io.IOException;
import java.util.Scanner;

public class CommandProcessor {

    private SkiArea skiArea;
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
                handleSet(parts);
            }
            case "load" -> {
                handleLoad(parts);
            }
            case "list" -> {
                handleList(parts);
            }
        }
    }

    private void handleList(String[] parts) {
        if (this.skiArea == null || this.skiArea.getLifts().isEmpty()) {
            System.err.println("ERROR: No ski area loaded.");
            return;
        }

        if (parts.length == 2 && parts[1].equals("lifts")) {
            for (Lift lift : this.skiArea.getLifts()) {
                System.out.println(lift.getId() + " " + lift.getType() + " " +
                        lift.getStartTime() + " " + lift.getEndTime() + " " +
                        lift.getRideDuration() + " " + lift.getWaitTime());

                if (lift.isBaseStation()) {
                    System.out.print("TRANSIT");
                }
                System.out.println();
            }
        } else if (parts.length == 2 && parts[1].equals("slopes")) {
            for (Piste piste : this.skiArea.getPistes()) {
                System.out.println(piste.getId() + " " + piste.getDifficulty() + " " +
                        piste.getSurface() + " " + piste.getLength() + " " + piste.getAltitudeDifference());
            }
        } else {
            System.err.println("ERROR: Invalid syntax.");
        }
    }

    private void handleLoad(String[] parts) {
        if (parts.length == 3 && parts[1].equals("area")) {
            String filepath = parts[2];
            try {
                this.skiArea = AreaParser.parse(filepath);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Invalid ski area definition.");
            } catch (IOException e) {
                System.err.println("Error: File not found");
            }
        } else {
            System.err.println("Error: Invalid syntax.");
        }
    }

    private void handleSet(String[] parts) {
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

    private void handleQuit(String[] parts) {
        if (parts.length > 1) {
            System.err.println("Error: the quit command does not take any arguments.");
        } else  {
            isRunning = false;
        }
    }
}
