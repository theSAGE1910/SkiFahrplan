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
    private final SkiArea area;
    private final Skier skier;
    private Route bestRoute = null;
    private int bestScore = -1;

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
     * @throws IllegalArgumentException if the start node is unknown, not a base station, or if the times are invalid
     */
    public Route plan(String startLiftId, LocalTime startTime, LocalTime endTime) {
        SkiNode startNode = area.getNode(startLiftId);

        Route initialRoute = new Route(startNode, startTime);
        this.bestRoute = null;

        findBestRoute(initialRoute, endTime, null, initialRoute.getPath().size());

        return bestRoute;
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
     * @param forbiddenNode a specific node to avoid at a certain depth (used for alternatives)
     * @param avoidDepth the exact path size at which the forbidden node must be avoided
     */
    private void findBestRoute(Route currentRoute, LocalTime endTime, SkiNode forbiddenNode, int avoidDepth) {
        SkiNode currentNode = currentRoute.getCurrentNode();

        if (currentNode instanceof Lift && ((Lift) currentNode).isBaseStation() && currentRoute.getPath().size() > 1) {
            evaluateAndSaveIfBest(currentRoute);
        }

        for (SkiNode nextNode : area.getConnections(currentNode)) {
            if (currentRoute.getPath().size() == avoidDepth && forbiddenNode != null && nextNode.equals(forbiddenNode)) {
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
        if (nextNode instanceof Lift lift) {

            LocalTime readyToBoard = arrivalTime.plusMinutes(lift.getWaitTime());

            if (readyToBoard.isBefore(lift.getStartTime())) {
                readyToBoard = lift.getStartTime();
            }

            if (!readyToBoard.isBefore(lift.getEndTime())) {
                return null;
            }
            return readyToBoard.plusMinutes(lift.getRideDuration());
        } else if (nextNode instanceof Piste piste) {

            int travelSeconds = piste.calculateTravelTime(skier.getSkillLevel());
            return arrivalTime.plusSeconds(travelSeconds);
        }

        return null;
    }

    /**
     * Evaluates a completed route against the current best route.
     * Overwrites the best route if the new route has a higher utility score,
     * a better preference score, or a lexicographically superior path in case of a tie.
     *
     * @param newRoute the newly completed valid route to evaluate
     */
    private void evaluateAndSaveIfBest(Route newRoute) {
        if (bestRoute == null) {
            bestRoute = newRoute;
            return;
        }

        int newUtility = newRoute.calculateUtility(skier);
        int bestUtility = bestRoute.calculateUtility(skier);

        if (newUtility > bestUtility) {
            bestRoute = newRoute;
        } else if (newUtility == bestUtility) {

            int newPreference = newRoute.calculatePreferenceScore(skier);
            int bestPreference = bestRoute.calculatePreferenceScore(skier);

            if (newPreference > bestPreference) {
                bestRoute = newRoute;
            } else if (newPreference == bestPreference) {

                String newString = newRoute.getLexicographicalString();
                String bestString = bestRoute.getLexicographicalString();

                if (newString.compareTo(bestString) < 0) {
                    bestRoute = newRoute;
                }
            }
        }
    }
}