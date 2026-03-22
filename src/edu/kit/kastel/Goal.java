package edu.kit.kastel;

/**
 * Defines the optimization goals a skier can choose for their route planning.
 *
 * @author uxuwg
 * @version 0.1
 */
public enum Goal {

    /**
     * Maximizes the total altitude difference covered during the route.
     */
    ALTITUDE,

    /**
     * Maximizes the total distance skied during the route.
     */
    DISTANCE,

    /**
     * Maximizes the total number of piste rides, including repeated pistes.
     */
    NUMBER,

    /**
     * Maximizes the total number of unique pistes ridden during the route.
     */
    UNIQUE;
}