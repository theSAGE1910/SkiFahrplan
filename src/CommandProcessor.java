import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class CommandProcessor {

    private SkiArea skiArea;
    private final Skier skier;
    private boolean isRunning;
    private Route plannedRoute;
    private int currentRouteIndex;
    private boolean nextWasCalled;

    public CommandProcessor(SkiArea skiArea, Skier skier) {
        this.skiArea = skiArea;
        this.skier = skier;
        this.isRunning = true;
        this.plannedRoute = null;
        this.currentRouteIndex = 0;
        this.nextWasCalled = false;
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
            case "quit" -> handleQuit(parts);
            case "set" -> handleSet(parts);
            case "load" -> handleLoad(parts);
            case "list" -> handleList(parts);
            case "like" -> handlePreference(parts, true);
            case "dislike" -> handlePreference(parts, false);
            case "reset" -> handleReset(parts);
            case "plan" -> handlePlan(parts);
            case "show" -> handleShow(parts);
            case "next" -> handleNext(parts);
            case "take" -> handleTake(parts);
            case "abort" -> handleAbort(parts);
            default -> System.err.println("ERROR: Unknown command: " + command);
        }
    }

    private void handleAbort(String[] parts) {
        if (parts.length != 1) {
            System.err.println("ERROR: abort takes no arguments.");
            return;
        }
        if (plannedRoute == null) {
            System.err.println("ERROR: No route to abort.");
            return;
        }

        this.plannedRoute = null;
        this.currentRouteIndex = 0;
        this.nextWasCalled = false;
        System.out.println("route aborted");
    }

    private void handleTake(String[] parts) {
        if (parts.length != 1) {
            System.err.println("ERROR: take takes no arguments.");
            return;
        }
        if (plannedRoute == null || currentRouteIndex >= plannedRoute.getPath().size()) {
            System.err.println("ERROR: No route planned or route already finished.");
            return;
        }
        if (!nextWasCalled) {
            System.err.println("ERROR: take must be called immediately after next.");
        }

        currentRouteIndex++;
        this.nextWasCalled = false;
    }

    private void handleNext(String[] parts) {
        if (parts.length != 1) {
            System.err.println("ERROR: next takes no arguments.");
            return;
        }
        if (plannedRoute == null) {
            System.err.println("ERROR: No route planned.");
            return;
        }

        if (currentRouteIndex >= plannedRoute.getPath().size()) {
            System.out.println("route finished!");
        } else {
            System.out.println(plannedRoute.getPath().get(currentRouteIndex).getId());
            this.nextWasCalled = true;
        }
    }

    private void handleShow(String[] parts) {
        if (parts.length == 2 && parts[1].equals("route")) {
            if (plannedRoute == null) {
                System.err.println("ERROR: No route planned.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = currentRouteIndex; i < plannedRoute.getPath().size(); i++) {
                sb.append(plannedRoute.getPath().get(i).getId()).append(" ");
            }
            System.out.println(sb.toString().trim());
            this.nextWasCalled = false;

        } else {
            System.err.println("ERROR: Invalid syntax. Use: show route");
        }
    }

    private void handlePlan(String[] parts) {
        if (parts.length != 4) {
            System.err.println("ERROR: Invalid syntax. Use: plan <liftId> <HH:mm> <HH:mm>");
            return;
        }

        if (this.skiArea == null) {
            System.err.println("ERROR: no ski area loaded.");
            return;
        }

        if (this.skier.getGoal() == null || this.skier.getSkillLevel() == null) {
            System.err.println("ERROR: skill and goal must be set before planning.");
            return;
        }

        try {
            String liftId = parts[1];
            LocalTime startTime = LocalTime.parse(parts[2]);
            LocalTime endTime = LocalTime.parse(parts[3]);

            RoutePlanner planner = new RoutePlanner(this.skiArea,  this.skier);
            Route bestRoute = planner.plan(liftId, startTime, endTime);

            if (bestRoute == null) {
                System.err.println("ERROR: no best route found in the given time frame.");
            } else {
                System.out.println("route planned");
                this.plannedRoute = bestRoute;
                this.currentRouteIndex = 0;
                this.nextWasCalled = false;
            }
        } catch (DateTimeParseException e) {
            System.err.println("ERROR: invalid time format.");
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    private void handleReset(String[] parts) {
        if (parts.length == 2 && parts[1].equals("preferences")) {
            skier.resetPreferences();
        } else  {
            System.err.println("ERROR: Invalid syntax. Use: reset preferences.");
        }
    }

    private void handlePreference(String[] parts, boolean isLike) {
        if (parts.length != 2) {
            System.err.println("ERROR: Invalid syntax");
            return;
        }

        String prefString = parts[1];

        try {
            Difficulty difficulty = Difficulty.valueOf(prefString);
            if (isLike) {
                skier.addLikedDifficulty(difficulty);
            } else {
                skier.addDislikedDifficulty(difficulty);
            }
            return;
        } catch (IllegalArgumentException ignored) {}

        try {
            Surface surface = Surface.valueOf(prefString);
            if (isLike) {
                skier.addLikedSurface(surface);
            } else {
                skier.addDislikedSurface(surface);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Invalid invalid preference. Must be a Difficulty or Surface.");
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
