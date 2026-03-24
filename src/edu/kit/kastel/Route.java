package edu.kit.kastel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a planned or taken route through the ski area.
 * This class tracks the sequence of {@link SkiNode} instances visited and the corresponding
 * arrival times at each node. It provides functionality to calculate the route's utility
 * and preference scores based on a skier's specific goals.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Route {

    private static final int INDEX_FIRST = 0;
    private static final int INDEX_SECOND = 1;
    private static final int INITIAL_SCORE = 0;
    private static final int SCORE_INCREMENT = 1;
    private static final int COMPARE_LESS_THAN = 0;

    private static final String ERROR_UNEXPECTED_GOAL = "Unexpected value: ";
    private static final String SEPARATOR_SPACE = " ";

    private final List<SkiNode> path;
    private final List<LocalTime> times;

    /**
     * Constructs a new initial {@code Route} starting at the specified node and time.
     *
     * @param startNode the initial {@link SkiNode} (usually a base station lift)
     * @param startTime the starting time of the route
     */
    public Route(SkiNode startNode, LocalTime startTime) {
        this.path = new ArrayList<>();
        this.times = new ArrayList<>();
        this.path.add(startNode);
        this.times.add(startTime);
    }

    /**
     * Constructs a new {@code Route} by extending an existing route with a new node and arrival time.
     *
     * @param previousRoute the existing route to build upon
     * @param nextNode the next {@link SkiNode} to append to the route
     * @param newTime the arrival time at the newly appended node
     */
    public Route(Route previousRoute, SkiNode nextNode, LocalTime newTime) {
        this.path = new ArrayList<>(previousRoute.path);
        this.times = new ArrayList<>(previousRoute.times);
        this.path.add(nextNode);
        this.times.add(newTime);
    }

    /**
     * Retrieves the sequence of nodes that make up this route.
     *
     * @return an ordered list of nodes representing the path
     */
    public List<SkiNode> getPath() {
        return new ArrayList<>(path);
    }

    /**
     * Retrieves the arrival time at the final node currently in the route.
     *
     * @return the accumulated time at the end of the route
     */
    public LocalTime getCurrentTime() {
        return this.times.getLast();
    }

    /**
     * Retrieves the final node currently at the end of the route.
     *
     * @return the current node the skier is located at
     */
    public SkiNode getCurrentNode() {
        return path.getLast();
    }

    /**
     * Generates a truncated copy of this route up to the specified index.
     * This is used to branch off and find alternative paths from a specific point.
     *
     * @param upToIndex the inclusive ending index for the truncated path
     * @return a new {@code Route} containing the path and times up to the specified index
     */
    public Route getTruncatedRoute(int upToIndex) {
        Route truncated = new Route(this.path.get(INDEX_FIRST), this.times.get(INDEX_FIRST));

        for (int i = INDEX_SECOND; i <= upToIndex; i++) {
            truncated.path.add(this.path.get(i));
            truncated.times.add(this.times.get(i));
        }

        return truncated;
    }

    /**
     * Calculates the main utility score of the route based on the goal of the specified skier.
     * The score is determined by the specific {@link Goal} (e.g., altitude, distance, number of rides).
     *
     * @param skier the {@link Skier} whose goal is used to evaluate the route
     * @return the calculated utility score as an integer
     */
    private int calculateUtility(Skier skier) {
        Goal goal = skier.getGoal();
        int score = INITIAL_SCORE;
        Set<SkiNode> uniquePistes = new HashSet<>();

        for (SkiNode node : path) {
            if (node instanceof Piste piste) {
                if (goal == Goal.ALTITUDE) {
                    score += piste.getAltitudeDifference();
                } else if (goal == Goal.DISTANCE) {
                    score += piste.getLength();
                } else if (goal == Goal.NUMBER) {
                    score += SCORE_INCREMENT;
                } else if (goal == Goal.UNIQUE) {
                    if (uniquePistes.add(piste)) {
                        score += SCORE_INCREMENT;
                    }
                } else {
                    System.err.println(ERROR_UNEXPECTED_GOAL + goal);
                }
            }
        }

        return score;
    }

    /**
     * Calculates the secondary preference score of the route based on the skier's likes and dislikes.
     * Each liked attribute adds to the score, while each disliked attribute subtracts from it.
     *
     * @param skier the {@link Skier} whose preferences are evaluated
     * @return the calculated preference score as an integer
     */
    private int calculatePreferenceScore(Skier skier) {
        int score = INITIAL_SCORE;

        for (SkiNode node : path) {
            if (node instanceof Piste piste) {
                score += skier.evaluatePreferenceScore(piste); // Delegate to Skier
            }
        }

        return score;
    }

    /**
     * Generates a space-separated string of all node identifiers in the route.
     * This string is used for lexicographical tie-breaking comparisons between equivalent routes.
     *
     * @return the lexicographical string representation of the path
     */
    private String getLexicographicalString() {
        StringBuilder sb = new StringBuilder();

        for (SkiNode node : path) {
            sb.append(node.getId()).append(SEPARATOR_SPACE);
        }

        return sb.toString().trim();
    }


    /**
     * Determines whether this route is strictly superior to another provided route.
     * The comparison evaluates the main utility score first, falls back to the secondary preference
     * score in case of a tie, and ultimately uses a lexicographical comparison of the node paths
     * as a final tie-breaker.
     *
     * @param bestRoute the competing route to compare against, or {@code null} if no route currently exists
     * @param skier the profile whose goals and preferences dictate the utility and preference scoring
     * @return true if this route is strictly better or if the competing route is null, false otherwise
     */
    public boolean isBetterThan(Route bestRoute, Skier skier) {
        boolean isBetter = true;

        if (bestRoute != null) {
            int thisUtility = this.calculateUtility(skier);
            int bestUtility = bestRoute.calculateUtility(skier);

            if (thisUtility != bestUtility) {
                isBetter = thisUtility > bestUtility;
            } else {
                int thisPref = this.calculatePreferenceScore(skier);
                int bestPref = bestRoute.calculatePreferenceScore(skier);

                if (thisPref != bestPref) {
                    isBetter = thisPref > bestPref;
                } else {
                    isBetter = this.getLexicographicalString().compareTo(bestRoute.getLexicographicalString()) < COMPARE_LESS_THAN;
                }
            }
        }

        return isBetter;
    }
}