package edu.kit.kastel;

import java.time.LocalTime;

/**
 * Represents a generic node within the ski area graph.
 * Implementing classes, such as {@link Lift} and {@link Piste}, form the
 * interconnected vertices of the graph that a skier can navigate.
 *
 * @author uxuwg
 * @version 0.1
 */
public interface SkiNode {

    /**
     * Retrieves the unique identifier of this ski node.
     *
     * @return the unique identifier as a string
     */
    String getId();

    /**
     * Calculates the arrival time at the next node after traversing this node.
     *
     * @param arrivalTime the time the skier arrived at this node
     * @param skier the skier traversing the node (used for skill-based calculations)
     * @return the calculated completion time, or {@code null} if traversal is impossible (e.g., closed lift)
     */
    LocalTime calculateNextTime(LocalTime arrivalTime, Skier skier);
}