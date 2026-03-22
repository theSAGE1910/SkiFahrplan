package edu.kit.kastel;

import java.time.LocalTime;

/**
 * Represents a ski lift in the ski area graph.
 * A lift transports skiers between different nodes and operates within specific hours.
 * It can also serve as a base station (transit lift) for entering or exiting the ski area.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Lift implements SkiNode {

    private final String id;
    private final LiftType type;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int rideDuration;
    private final int waitTime;
    private final boolean isBaseStation;

    /**
     * Constructs a new {@code Lift} with the specified operational parameters.
     *
     * @param id the unique identifier of the lift
     * @param type the specific {@link LiftType} (e.g., gondola or chairlift)
     * @param startTime the operational opening time of the lift
     * @param endTime the operational closing time of the lift
     * @param rideDuration the duration of the lift ride in minutes
     * @param waitTime the expected queueing time before boarding in minutes
     * @param isBaseStation {@code true} if this lift is a transit point to enter or exit the area, {@code false} otherwise
     */
    public Lift(String id, LiftType type, LocalTime startTime, LocalTime endTime, int rideDuration, int waitTime, boolean isBaseStation) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rideDuration = rideDuration;
        this.waitTime = waitTime;
        this.isBaseStation = isBaseStation;
    }

    /**
     * Gets the unique identifier of this lift.
     *
     * @return the lift identifier as a string
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Gets the specific type of this lift.
     *
     * @return the {@link LiftType} of the lift
     */
    public LiftType getType() {
        return type;
    }

    /**
     * Gets the operational opening time of this lift.
     *
     * @return the time the lift starts operating
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the operational closing time of this lift.
     *
     * @return the time the lift stops operating
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Gets the duration it takes to travel on this lift.
     *
     * @return the ride duration in minutes
     */
    public int getRideDuration() {
        return rideDuration;
    }

    /**
     * Gets the expected queueing or waiting time before boarding this lift.
     *
     * @return the waiting time in minutes
     */
    public int getWaitTime() {
        return waitTime;
    }

    /**
     * Indicates whether this lift serves as a base station (transit lift).
     *
     * @return {@code true} if the lift is a base station, {@code false} otherwise
     */
    public boolean isBaseStation() {
        return isBaseStation;
    }
}