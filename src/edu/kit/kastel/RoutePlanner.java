package edu.kit.kastel;

import java.time.LocalTime;

/**
 * Computes the optimal {@link Route} through a {@link SkiArea} for a specific {@link Skier}.
 * This class uses a recursive Depth-First Search (DFS) algorithm to explore all possible
 * valid paths within a given time frame, evaluating them against the skier's goals and preferences.
 *
 * @author uxuwg
 * @version 0.1
 */
public class RoutePlanner {

    private static final int MIN_PATH_LENGTH_FOR_EVALUATION = 1;
    private static final int INDEX_FIRST = 0;

    private final SkiArea area;
    private final Skier skier;
    private Route bestRoute = null;

    /**
     * Constructs a new {@code RoutePlanner} for the specified area and skier.
     *
     * @param area the ski area graph to traverse
     * @param skier the skier whose goals and preferences determine the best route
     */
    public RoutePlanner(SkiArea area, Skier skier) {
        this.area = area;
        this.skier = skier;
    }

    /**
     * Plans the optimal route starting from a specific base station within a given time frame.
     *
     * @param startLiftId the unique identifier of the starting base station lift
     * @param startTime the time the skier begins the route
     * @param endTime the strict deadline by which the skier must return to a base station
     * @return the optimal {@code Route}, or {@code null} if no valid route exists
     */
    public Route plan(String startLiftId, LocalTime startTime, LocalTime endTime) {
        SkiNode startNode = area.getNode(startLiftId);

        LocalTime timeAfterFirstLift = calculateNextTime(startTime, startNode);

        if (timeAfterFirstLift == null || timeAfterFirstLift.isAfter(endTime)) {
            return null;
        }

        Route initialRoute = new Route(startNode, timeAfterFirstLift);
        this.bestRoute = null;

        findBestRoute(initialRoute, endTime, null, initialRoute.getPath().size());

        return bestRoute;
    }

    /**
     * Dynamically recalculates the optimal route from the skier's current position and time.
     *
     * @param currentRouteSoFar the truncated history of the route taken so far
     * @param endTime the strict deadline by which the skier must return to a base station
     * @return the newly optimized {@code Route}, or {@code null} if no valid route exists
     */
    public Route replan(Route currentRouteSoFar, LocalTime endTime) {
        return findAlternativeRoute(currentRouteSoFar, endTime, null);
    }

    /**
     * Calculates an alternative route from a specific point in an existing route,
     * explicitly avoiding a specified next node.
     *
     * @param currentRouteSoFar the truncated history of the route taken so far
     * @param endTime the strict deadline to finish the route
     * @param forbiddenNextNode the {@link SkiNode} that the algorithm must not visit on its first step
     * @return the optimal alternative {@code Route}, or {@code null} if no alternative exists
     */
    public Route findAlternativeRoute(Route currentRouteSoFar, LocalTime endTime, SkiNode forbiddenNextNode) {
        this.bestRoute = null;
        int currentDepth = currentRouteSoFar.getPath().size();

        findBestRoute(currentRouteSoFar, endTime, forbiddenNextNode, currentDepth);

        return this.bestRoute;
    }

    /**
     * Recursively explores the ski area graph using Depth-First Search to find the best route.
     *
     * @param currentRoute the path and times accumulated so far
     * @param endTime the strict deadline to finish the route
     * @param forbiddenNode a specific node to avoid at a certain depth
     * @param avoidDepth the exact path size at which the forbidden node must be avoided
     */
    private void findBestRoute(Route currentRoute, LocalTime endTime, SkiNode forbiddenNode, int avoidDepth) {
        SkiNode currentNode = currentRoute.getCurrentNode();

        boolean reachesBaseStation = false;
        SkiNode startNode = currentRoute.getPath().get(INDEX_FIRST);

        for (SkiNode nextNode : area.getConnections(currentNode)) {
            if (currentRoute.getPath().size() == avoidDepth && nextNode.equals(forbiddenNode)) {
                continue;
            }

            if (nextNode.equals(startNode)) {
                reachesBaseStation = true;
                break;
            }
        }

        if (reachesBaseStation && currentRoute.getPath().size() > MIN_PATH_LENGTH_FOR_EVALUATION) {
            evaluateAndSaveIfBest(currentRoute);
        }

        for (SkiNode nextNode : area.getConnections(currentNode)) {
            if (currentRoute.getPath().size() == avoidDepth && nextNode.equals(forbiddenNode)) {
                continue;
            }

            LocalTime nextTime = calculateNextTime(currentRoute.getCurrentTime(), nextNode);

            if (nextTime != null && !nextTime.isAfter(endTime)) {
                Route nextRoute = new Route(currentRoute, nextNode, nextTime);
                findBestRoute(nextRoute, endTime, forbiddenNode, avoidDepth);
            }
        }
    }

    /**
     * Calculates the arrival time at the next node based on travel, wait, and operational times.
     *
     * @param arrivalTime the time the skier arrived at the current node
     * @param nextNode the target node to travel to
     * @return the calculated arrival time, or {@code null} if the lift is closed or the deadline is missed
     */
    private LocalTime calculateNextTime(LocalTime arrivalTime, SkiNode nextNode) {
        return nextNode.calculateNextTime(arrivalTime, skier);
    }

    /**
     * Evaluates a completed route against the current best route.
     *
     * @param newRoute the newly completed valid route to evaluate
     */
    private void evaluateAndSaveIfBest(Route newRoute) {
        if (newRoute.isBetterThan(bestRoute, skier)) {
            bestRoute = newRoute;
        }
    }
}