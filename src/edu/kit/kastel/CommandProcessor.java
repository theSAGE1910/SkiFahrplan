package edu.kit.kastel;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * The {@code CommandProcessor} manages the interactive command-line interface.
 * It reads user inputs, parses them, and delegates the actions to the underlying
 * {@link SkiArea}, {@link Skier}, and {@link RoutePlanner}.
 *
 * @author uxuwg
 * @version 0.1
 */
public class CommandProcessor {

    private SkiArea skiArea;
    private final Skier skier;
    private boolean isRunning;
    private Route plannedRoute;
    private int currentRouteIndex;
    private boolean nextWasCalled;
    private LocalTime activeEndTime;

    /**
     * Constructs a new {@code CommandProcessor}.
     *
     * @param skiArea the {@link SkiArea} graph containing lifts and pistes
     * @param skier   the {@link Skier} whose preferences and skills are tracked
     */
    public CommandProcessor(SkiArea skiArea, Skier skier) {
        this.skiArea = skiArea;
        this.skier = skier;
        this.isRunning = true;
        this.plannedRoute = null;
        this.currentRouteIndex = 0;
        this.nextWasCalled = false;
    }

    /**
     * Starts the main interactive loop. Reads commands from the standard input
     * and processes them until the "quit" command is issued.
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) { //scanner.close()

            while (isRunning) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    continue;
                }
                processCommands(line);
            }
        }
    }

    /**
     * Parses a single command line and delegates it to the appropriate handler method.
     *
     * @param line the raw command line string entered by the user
     */
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
            case "alternative" -> handleAlternative(parts);
            case "abort" -> handleAbort(parts);
            default -> System.err.println("Error, Unknown command: " + command);
        }
    }

    private void handleAbort(String[] parts) {
        if (parts.length != 1) {
            System.err.println("Error, abort takes no arguments.");
            return;
        }
        if (plannedRoute == null) {
            System.err.println("Error, No route to abort.");
            return;
        }

        this.plannedRoute = null;
        this.currentRouteIndex = 0;
        this.nextWasCalled = false;
        System.out.println("route aborted");
    }

    private void handleAlternative(String[] parts) {
        if (parts.length != 1) {
            System.err.println("Error, alternative takes no arguments.");
            return;
        }
        if (plannedRoute == null || !nextWasCalled) {
            System.err.println("Error, alternative must be called immediately after next");
            return;
        }
        if (currentRouteIndex == 0) {
            System.err.println("Error, Cannot calculate alternative for the starting base station");
            return;
        }

        SkiNode nodeToAvoid = plannedRoute.getPath().get(currentRouteIndex);
        Route truncatedRoute = plannedRoute.getTruncatedRoute(currentRouteIndex - 1);

        RoutePlanner planner = new RoutePlanner(this.skiArea, this.skier);
        Route alternativeRoute = planner.findAlternativeRoute(truncatedRoute, activeEndTime, nodeToAvoid);

        if (alternativeRoute == null) {
            System.err.println("Error, No alternative found.");
        } else {
            System.out.println("avoided " + nodeToAvoid.getId());
            this.plannedRoute = alternativeRoute;
            this.nextWasCalled = false;
        }
    }

    private void handleTake(String[] parts) {
        if (parts.length != 1) {
            System.err.println("Error, take takes no arguments.");
            return;
        }
        if (plannedRoute == null || currentRouteIndex >= getRouteEndIndex()) {
            System.err.println("Error, No route planned or route already finished.");
            return;
        }
        if (!nextWasCalled) {
            System.err.println("Error, take must be called immediately after next.");
            return;
        }

        currentRouteIndex++;
        this.nextWasCalled = false;
    }

    private void handleNext(String[] parts) {
        if (parts.length != 1) {
            System.err.println("Error, next takes no arguments.");
            return;
        }
        if (plannedRoute == null) {
            System.err.println("Error, No route planned.");
            return;
        }

        if (currentRouteIndex >= getRouteEndIndex()) {
            System.out.println("route finished!");
        } else {
            System.out.println(plannedRoute.getPath().get(currentRouteIndex).getId());
            this.nextWasCalled = true;
        }
    }

    private void handleShow(String[] parts) {
        if (parts.length == 2 && parts[1].equals("route")) {
            if (plannedRoute == null) {
                System.err.println("Error, No route planned.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (int i = currentRouteIndex; i < getRouteEndIndex(); i++) {
                sb.append(plannedRoute.getPath().get(i).getId()).append(" ");
            }
            System.out.println(sb.toString().trim());
            this.nextWasCalled = false;

        } else {
            System.err.println("Error, Invalid syntax. Use: show route");
        }
    }

    private void handlePlan(String[] parts) {
        if (parts.length != 4) {
            System.err.println("Error, Invalid syntax. Use: plan <liftId> <HH:mm> <HH:mm>");
            return;
        }

        if (this.skiArea == null) {
            System.err.println("Error, no ski area loaded.");
            return;
        }

        if (this.skier.getGoal() == null || this.skier.getSkillLevel() == null) {
            System.err.println("Error, skill and goal must be set before planning.");
            return;
        }

        String liftId = parts[1];
        LocalTime startTime;
        LocalTime endTime;

        try {
            startTime = LocalTime.parse(parts[2]);
            endTime = LocalTime.parse(parts[3]);
        } catch (DateTimeParseException e) {
            System.err.println("Error, invalid time format.");
            return;
        }

        SkiNode startNode = this.skiArea.getNode(liftId);
        if (startNode == null) {
            System.err.println("Error, Unknown start node");
            return;
        }
        if (!(startNode instanceof Lift) || !((Lift) startNode).isBaseStation()) {
            System.err.println("Error, Start node must be a base station (TRANSIT lift).");
            return;
        }
        if (startTime.isAfter(endTime)) {
            System.err.println("Error, Start time must be after end time.");
            return;
        }

        RoutePlanner planner = new RoutePlanner(this.skiArea, this.skier);
        Route bestRoute = planner.plan(liftId, startTime, endTime);

        if (bestRoute == null) {
            System.err.println("Error, no best route found in the given time frame.");
        } else {
            System.out.println("route planned");
            this.plannedRoute = bestRoute;
            this.activeEndTime = endTime;
            this.currentRouteIndex = 0;
            this.nextWasCalled = false;
        }
    }

    private void handleReset(String[] parts) {
        if (parts.length == 2 && parts[1].equals("preferences")) {
            skier.resetPreferences();
        } else {
            System.err.println("Error, Invalid syntax. Use: reset preferences.");
        }
    }

    private void handlePreference(String[] parts, boolean isLike) {
        if (parts.length != 2) {
            System.err.println("Error, Invalid syntax");
            return;
        }

        String prefString = parts[1];
        boolean matched = false;

        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.name().equals(prefString)) {
                if (isLike) {
                    skier.addLikedDifficulty(difficulty);
                } else {
                    skier.addDislikedDifficulty(difficulty);
                }
                matched = true;
                break;
            }
        }

        if (!matched) {
            for (Surface surface : Surface.values()) {
                if (surface.name().equals(prefString)) {
                    if (isLike) {
                        skier.addLikedSurface(surface);
                    } else {
                        skier.addDislikedSurface(surface);
                    }
                    matched = true;
                    break;
                }
            }
        }

        if (!matched) {
            System.err.println("Error, Invalid preference. Must be a Difficulty or Surface.");
        }
    }

    private void handleList(String[] parts) {
        if (this.skiArea == null || this.skiArea.getLifts().isEmpty()) {
            System.err.println("Error, No ski area loaded.");
            return;
        }

        if (parts.length == 2 && parts[1].equals("lifts")) {
            for (Lift lift : this.skiArea.getLifts()) {
                System.out.println(lift.getId() + " " + lift.getType() + " "
                    + lift.getStartTime() + " " + lift.getEndTime() + " "
                    + lift.getRideDuration() + " " + lift.getWaitTime()
                    + (lift.isBaseStation() ? " TRANSIT" : ""));
            }
        } else if (parts.length == 2 && parts[1].equals("slopes")) {
            for (Piste piste : this.skiArea.getPistes()) {
                System.out.println(piste.getId() + " " + piste.getDifficulty() + " "
                    + piste.getSurface() + " " + piste.getLength() + " " + piste.getAltitudeDifference());
            }
        } else {
            System.err.println("Error, Invalid syntax.");
        }
    }

    private void handleLoad(String[] parts) {
        if (parts.length == 3 && parts[1].equals("area")) {
            String filepath = parts[2];
            try {
                this.skiArea = AreaParser.parse(filepath);
            } catch (IOException e) {
                System.err.println("Error, File not found");
            }
        } else {
            System.err.println("Error, Invalid syntax.");
        }
    }

    private void handleSet(String[] parts) {
        if (parts.length == 3 && parts[1].equals("skill")) {
            boolean matched = false;

            for (SkillLevel skill : SkillLevel.values()) {
                if (skill.name().equals(parts[2])) {
                    skier.setSkillLevel(skill);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                System.err.println("Error, Invalid skill level");
            }

        } else if (parts.length == 3 && parts[1].equals("goal")) {
            boolean matched = false;
            for (Goal goal : Goal.values()) {
                if (goal.name().equals(parts[2])) {
                    skier.setGoal(goal);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                System.err.println("Error, Invalid goal name");
            }

        } else {
            System.err.println("Error, Invalid command");
        }
    }

    private void handleQuit(String[] parts) {
        if (parts.length > 1) {
            System.err.println("Error, the quit command does not take any arguments.");
        } else {
            isRunning = false;
        }
    }

    private int getRouteEndIndex() {
        int endIndex = plannedRoute.getPath().size();
        SkiNode lastNode = plannedRoute.getCurrentNode();

        if (lastNode instanceof Lift && ((Lift) lastNode).isBaseStation()) {
            endIndex--;
        }

        return endIndex;
    }
}