package edu.kit.kastel;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * Command responsible for generating the initial optimal route for the skier.
 * When executed, this command validates the start parameters and invokes the
 * Depth-First Search routing algorithm to find a path that maximizes utility
 * within the specified time constraints.
 *
 * @author uxuwg
 * @version 0.1
 */
public class PlanCommand implements Command {

    /**
     * Executes the routing algorithm to calculate the best route.
     * Validates that a ski area is loaded, the skier profile is completely configured,
     * and that the starting node is a valid base station before beginning the search.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != 4) {
            System.err.println("Error, Invalid syntax. Use: plan <liftId> <HH:mm> <HH:mm>");
            return;
        }

        if (session.getSkiArea() == null) {
            System.err.println("Error, no ski area loaded.");
            return;
        }

        if (session.getSkier().getGoal() == null || session.getSkier().getSkillLevel() == null) {
            System.err.println("Error, skill and goal must be set before planning.");
            return;
        }

        if (session.getPlannedRoute() != null) {
            System.err.println("Error, A route is already planned or running.");
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

        SkiNode startNode = session.getSkiArea().getNode(liftId);
        if (startNode == null) {
            System.err.println("Error, Unknown start node");
            return;
        }
        if (!(startNode instanceof Lift) || !((Lift) startNode).isBaseStation()) {
            System.err.println("Error, Start node must be a base station (TRANSIT lift).");
            return;
        }
        if (!startTime.isBefore(endTime)) {
            System.err.println("Error, Start time must be before end time.");
            return;
        }

        RoutePlanner planner = new RoutePlanner(session.getSkiArea(), session.getSkier());
        Route bestRoute = planner.plan(liftId, startTime, endTime);

        if (bestRoute == null) {
            System.err.println("Error, no best route found in the given time frame.");
        } else {
            System.out.println("route planned");
            session.setPlannedRoute(bestRoute);
            session.setActiveEndTime(endTime);
            session.setCurrentRouteIndex(0);
            session.setNextWasCalled(false);
        }
    }
}