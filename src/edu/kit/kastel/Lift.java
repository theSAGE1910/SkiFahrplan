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

    private static final int TIME_START = 0;
    private static final int TIME_END = 1;
    private static final int DUR_RIDE = 0;
    private static final int DUR_WAIT = 1;

    private final String id;
    private final LiftType type;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int rideDuration;
    private final int waitTime;
    private final boolean baseStation;

    /**
     * Constructs a new {@code Lift} with the specified operational parameters.
     *
     * @param id the unique identifier of the lift
     * @param type the specific {@link LiftType}
     * @param times an array containing [startTime, endTime]
     * @param durations an array containing [rideDuration, waitTime]
     * @param baseStation {@code true} if this lift is a transit point
     */
    public Lift(String id, LiftType type, LocalTime[] times, int[] durations, boolean baseStation) {
        this.id = id;
        this.type = type;
        this.startTime = times[TIME_START];
        this.endTime = times[TIME_END];
        this.rideDuration = durations[DUR_RIDE];
        this.waitTime = durations[DUR_WAIT];
        this.baseStation = baseStation;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public LocalTime calculateNextTime(LocalTime arrivalTime, Skier skier) {
        LocalTime readyToBoard = arrivalTime.plusMinutes(this.waitTime);

        if (readyToBoard.isBefore(this.startTime)) {
            readyToBoard = this.startTime;
        }

        if (!readyToBoard.isBefore(this.endTime)) {
            return null;
        }
        return readyToBoard.plusMinutes(this.rideDuration);
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
        return baseStation;
    }
}