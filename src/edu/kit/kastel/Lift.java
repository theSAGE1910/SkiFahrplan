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

    private static final String SEPARATOR = " ";
    private static final String TRANSIT_SUFFIX = " TRANSIT";

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
     * Indicates whether this lift serves as a base station (transit lift).
     *
     * @return {@code true} if the lift is a base station, {@code false} otherwise
     */
    public boolean isBaseStation() {
        return baseStation;
    }

    /**
     * Constructs a formatted string representation of this lift's operational details.
     * This includes the identifier, lift type, operational hours, durations, and whether it serves as a base station.
     *
     * @return a formatted string containing the lift's attributes separated by spaces
     */
    public String getFormattedListInfo() {
        return id + SEPARATOR + type + SEPARATOR + startTime + SEPARATOR + endTime + SEPARATOR
            + rideDuration + SEPARATOR + waitTime + (baseStation ? TRANSIT_SUFFIX : "");
    }
}