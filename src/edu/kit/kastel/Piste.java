package edu.kit.kastel;

/**
 * Represents a ski piste (slope) in the ski area graph.
 * A piste connects different nodes and has specific physical attributes such as
 * length, altitude difference, difficulty, and surface condition.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Piste implements SkiNode {

    private final String id;
    private final Difficulty difficulty;
    private final Surface surface;
    private final int length;
    private final int altitudeDifference;

    /**
     * Constructs a new {@code Piste} with the specified attributes.
     *
     * @param id the unique identifier of the piste
     * @param difficulty the {@link Difficulty} level of the piste
     * @param surface the {@link Surface} condition of the piste
     * @param length the length of the piste in meters
     * @param altitudeDifference ornate the altitude difference between the start and end of the piste in meters
     */
    public Piste(String id, Difficulty difficulty, Surface surface, int length, int altitudeDifference) {
        this.id = id;
        this.difficulty = difficulty;
        this.surface = surface;
        this.length = length;
        this.altitudeDifference = altitudeDifference;
    }

    /**
     * Gets the unique identifier of this piste.
     *
     * @return the piste identifier as a string
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Gets the difficulty level of this piste.
     *
     * @return the difficulty level
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the surface condition of this piste.
     *
     * @return the surface condition
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * Gets the total length of this piste.
     *
     * @return the length in meters
     */
    public int getLength() {
        return length;
    }

    /**
     * Gets the difference in altitude between the start and end of this piste.
     *
     * @return the altitude difference in meters
     */
    public int getAltitudeDifference() {
        return altitudeDifference;
    }

    /**
     * Calculates the time required for a skier to travel down this piste.
     * The calculation factors in the piste's length, gradient, difficulty, surface condition,
     * and the skier's specific skill level according to the defined mathematical formula.
     *
     * @param skill the {@link SkillLevel} of the skier evaluating the piste
     * @return the calculated travel time in seconds, rounded up to the nearest whole second
     */
    public int calculateTravelTime(SkillLevel skill) {

        double mDifficulty = switch (this.difficulty) {
            case BLUE -> 1.00;
            case RED -> 1.15;
            case BLACK -> 1.35;
        };

        double mSurface = switch (this.surface) {
            case REGULAR -> 1.00;
            case BUMPY -> 1.20;
            case ICY -> 1.30;
        };

        double mSkill = switch (skill) {
            case BEGINNER -> 1.35;
            case INTERMEDIATE -> 1.10;
            case EXPERT -> 0.90;
        };

        double gradient = (double) this.altitudeDifference / this.length;
        double multipliers = mDifficulty * mSurface * (1.0 + 2.0 * gradient) * mSkill;
        double timeInSeconds = (this.length / 8.0) * multipliers;

        return (int) Math.ceil(timeInSeconds);
    }
}