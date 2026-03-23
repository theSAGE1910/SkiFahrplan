package edu.kit.kastel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a planned or taken route through the ski area.
 * This class tracks the sequence of {@link SkiNode} instances visited and the corresponding
 * arrival times at each node. It also provides functionality to calculate the route's utility
 * and preference scores based on a skier's specific goals and preferences.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Route {
    private final List<SkiNode> path;
    private final List<LocalTime> times = new ArrayList<>();

    /**
     * Constructs a new initial {@code Route} starting at the specified node and time.
     *
     * @param startNode the initial {@link SkiNode} (usually a base station lift)
     * @param startTime the starting time of the route
     */
    public Route(SkiNode startNode, LocalTime startTime) {
        this.path = new ArrayList<>();
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
        this.times.addAll(previousRoute.times);
        this.path.add(nextNode);
        this.times.add(newTime);
    }

    /**
     * Gets the sequence of nodes that make up this route.
     *
     * @return a list of nodes representing the path
     */
    public List<SkiNode> getPath() {
        return path;
    }

    /**
     * Gets the arrival time at the final node currently in the route.
     *
     * @return the current time at the end of the route
     */
    public LocalTime getCurrentTime() {
        return this.times.get(this.times.size() - 1);
    }

    /**
     * Gets the final node currently at the end of the route.
     *
     * @return the current node the skier is located at
     */
    public SkiNode getCurrentNode() {
        return path.get(path.size() - 1);
    }

    /**
     * Generates a truncated copy of this route up to the specified index.
     * This is used to branch off and find alternative paths from a specific point.
     *
     * @param upToIndex the inclusive ending index for the truncated path
     * @return a new {@code Route} containing the path and times up to the specified index
     */
    public Route getTruncatedRoute(int upToIndex) {
        Route truncated = new Route(this.path.get(0), this.times.get(0));

        for (int i = 1; i <= upToIndex; i++) {
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
    public int calculateUtility(Skier skier) {
        Goal goal = skier.getGoal();
        int score = 0;
        Set<SkiNode> uniquePistes = new HashSet<>();

        for (SkiNode node : path) {
            if (node instanceof Piste piste) {

                if (goal == Goal.ALTITUDE) {
                    score += piste.getAltitudeDifference();
                } else if (goal == Goal.DISTANCE) {
                    score += piste.getLength();
                } else if (goal == Goal.NUMBER) {
                    score += 1;
                } else if (goal == Goal.UNIQUE) {
                    if (uniquePistes.add(piste)) {
                        score += 1;
                    }
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
    public int calculatePreferenceScore(Skier skier) {
        int score = 0;

        for (SkiNode node : path) {
            if (node instanceof Piste piste) {

                if (skier.likesDifficulty(piste.getDifficulty())) {
                    score++;
                } else if (skier.dislikesDifficulty(piste.getDifficulty())) {
                    score--;
                }

                if (skier.likesSurface(piste.getSurface())) {
                    score++;
                } else if (skier.dislikesSurface(piste.getSurface())) {
                    score--;
                }
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
    public String getLexicographicalString() {
        StringBuilder sb = new StringBuilder();

        for (SkiNode node : path) {
            sb.append(node.getId()).append(" ");
        }

        return sb.toString().trim();
    }
}