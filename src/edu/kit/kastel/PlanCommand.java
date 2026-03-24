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

    private static final int EXPECTED_ARGS_LENGTH = 4;
    private static final int ARG_LIFT_ID_INDEX = 1;
    private static final int ARG_START_TIME_INDEX = 2;
    private static final int ARG_END_TIME_INDEX = 3;
    private static final int DEFAULT_ROUTE_INDEX = 0;

    private static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax. Use: plan <liftId> <HH:mm> <HH:mm>";
    private static final String ERROR_NO_AREA = "Error, no ski area loaded.";
    private static final String ERROR_NO_SKILL_GOAL = "Error, skill and goal must be set before planning.";
    private static final String ERROR_ROUTE_EXISTS = "Error, A route is already planned or running.";
    private static final String ERROR_INVALID_TIME = "Error, invalid time format.";
    private static final String ERROR_UNKNOWN_NODE = "Error, Unknown start node";
    private static final String ERROR_NOT_BASE_STATION = "Error, Start node must be a base station (TRANSIT lift).";
    private static final String ERROR_TIME_ORDER = "Error, Start time must be before end time.";
    private static final String ERROR_NO_ROUTE_FOUND = "Error, no best route found in the given time frame.";
    private static final String MSG_ROUTE_PLANNED = "route planned";

    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_SYNTAX);
        } else if (session.getSkiArea() == null) {
            System.err.println(ERROR_NO_AREA);
        } else if (session.getSkier().getGoal() == null || session.getSkier().getSkillLevel() == null) {
            System.err.println(ERROR_NO_SKILL_GOAL);
        } else if (session.getPlannedRoute() != null) {
            System.err.println(ERROR_ROUTE_EXISTS);
        } else {

            String liftId = parts[ARG_LIFT_ID_INDEX];

            try {
                LocalTime startTime = LocalTime.parse(parts[ARG_START_TIME_INDEX]);
                LocalTime endTime = LocalTime.parse(parts[ARG_END_TIME_INDEX]);

                SkiNode startNode = session.getSkiArea().getNode(liftId);

                if (startNode == null) {
                    System.err.println(ERROR_UNKNOWN_NODE);
                } else if (!(startNode instanceof Lift) || !((Lift) startNode).isBaseStation()) {
                    System.err.println(ERROR_NOT_BASE_STATION);
                } else if (!startTime.isBefore(endTime)) {
                    System.err.println(ERROR_TIME_ORDER);
                } else {
                    RoutePlanner planner = new RoutePlanner(session.getSkiArea(), session.getSkier());
                    Route bestRoute = planner.plan(liftId, startTime, endTime);

                    if (bestRoute == null) {
                        System.err.println(ERROR_NO_ROUTE_FOUND);
                    } else {
                        System.out.println(MSG_ROUTE_PLANNED);
                        session.setPlannedRoute(bestRoute);
                        session.setActiveEndTime(endTime);
                        session.setCurrentRouteIndex(DEFAULT_ROUTE_INDEX);
                        session.setNextWasCalled(false);
                    }
                }
            } catch (DateTimeParseException e) {
                System.err.println(ERROR_INVALID_TIME);
            }
        }
    }
}