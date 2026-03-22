package edu.kit.kastel;

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
}