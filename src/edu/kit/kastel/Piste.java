package edu.kit.kastel;

/**
 * Represents a ski piste (slope) in the ski area graph.
 * A piste connects different nodes and has specific physical attributes such as
 * length, altitude difference, difficulty, and surface condition, which dictate travel time.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Piste implements SkiNode {

    private static final double BASE_SPEED_DIVISOR = 8.0;
    private static final double GRADIENT_MULTIPLIER = 2.0;

    private final String id;
    private final Difficulty difficulty;
    private final Surface surface;
    private final int length;
    private final int altitudeDifference;

    /**
     * Constructs a new {@code Piste} with the specified physical attributes.
     *
     * @param id the unique identifier of the piste
     * @param difficulty the {@link Difficulty} level of the piste
     * @param surface the {@link Surface} condition of the piste
     * @param length the length of the piste in meters
     * @param altitudeDifference the altitude difference between the start and end of the piste in meters
     */
    public Piste(String id, Difficulty difficulty, Surface surface, int length, int altitudeDifference) {
        this.id = id;
        this.difficulty = difficulty;
        this.surface = surface;
        this.length = length;
        this.altitudeDifference = altitudeDifference;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Retrieves the difficulty level of this piste.
     *
     * @return the difficulty classification (e.g., BLUE, RED, BLACK)
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Retrieves the surface condition of this piste.
     *
     * @return the physical surface state (e.g., ICY, BUMPY)
     */
    public Surface getSurface() {
        return surface;
    }

    /**
     * Retrieves the total physical length of this piste.
     *
     * @return the distance in meters
     */
    public int getLength() {
        return length;
    }

    /**
     * Retrieves the vertical drop of this piste.
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
     * @param skill the {@link SkillLevel} of the skier navigating the piste
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
        double multipliers = mDifficulty * mSurface * (1.0 + GRADIENT_MULTIPLIER * gradient) * mSkill;
        double timeInSeconds = (this.length / BASE_SPEED_DIVISOR) * multipliers;

        return (int) Math.ceil(timeInSeconds);
    }
}