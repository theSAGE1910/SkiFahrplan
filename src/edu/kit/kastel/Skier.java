package edu.kit.kastel;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user navigating the ski area.
 * This class tracks the skier's {@link SkillLevel}, their optimization {@link Goal},
 * and their specific positive and negative preferences for piste difficulties and surface conditions.
 *
 * @author uxuwg
 * @version 0.1
 */
public class Skier {

    private SkillLevel skillLevel;
    private Goal goal;
    private final Set<Difficulty> likedDifficulties;
    private final Set<Difficulty> dislikedDifficulties;
    private final Set<Surface> likedSurfaces;
    private final Set<Surface> dislikedSurfaces;

    /**
     * Constructs a new {@code Skier} with empty preference sets.
     * The skill level and goal remain uninitialized until explicitly set by the user.
     */
    public Skier() {
        this.likedDifficulties = new HashSet<>();
        this.dislikedDifficulties = new HashSet<>();
        this.likedSurfaces = new HashSet<>();
        this.dislikedSurfaces = new HashSet<>();
    }

    /**
     * Retrieves the current skill level of this skier.
     *
     * @return the active proficiency classification
     */
    public SkillLevel getSkillLevel() {
        return skillLevel;
    }

    /**
     * Updates the proficiency level of this skier. This affects how travel times
     * are mathematically calculated during route planning.
     *
     * @param skillLevel the new {@link SkillLevel} to assign
     */
    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    /**
     * Retrieves the active optimization goal for this skier's route planning.
     *
     * @return the current route planning metric
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Updates the optimization goal for this skier's route planner. This dictates
     * which utility metric the routing algorithm attempts to maximize.
     *
     * @param goal the new {@link Goal} to assign
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    /**
     * Registers a specific difficulty to the skier's positive preferences.
     *
     * @param difficulty the {@link Difficulty} the skier explicitly likes
     */
    public void addLikedDifficulty(Difficulty difficulty) {
        likedDifficulties.add(difficulty);
    }

    /**
     * Registers a specific difficulty to the skier's negative preferences.
     *
     * @param difficulty the {@link Difficulty} the skier explicitly wants to avoid
     */
    public void addDislikedDifficulty(Difficulty difficulty) {
        dislikedDifficulties.add(difficulty);
    }

    /**
     * Evaluates if the skier has a recorded positive preference for the specified difficulty.
     *
     * @param difficulty the difficulty level to check
     * @return {@code true} if the skier likes the difficulty, {@code false} otherwise
     */
    boolean likesDifficulty(Difficulty difficulty) {
        return likedDifficulties.contains(difficulty);
    }

    /**
     * Evaluates if the skier has a recorded negative preference for the specified difficulty.
     *
     * @param difficulty the difficulty level to check
     * @return {@code true} if the skier dislikes the difficulty, {@code false} otherwise
     */
    boolean dislikesDifficulty(Difficulty difficulty) {
        return dislikedDifficulties.contains(difficulty);
    }

    /**
     * Registers a specific surface condition to the skier's positive preferences.
     *
     * @param surface the {@link Surface} condition the skier explicitly likes
     */
    public void addLikedSurface(Surface surface) {
        likedSurfaces.add(surface);
    }

    /**
     * Registers a specific surface condition to the skier's negative preferences.
     *
     * @param surface the {@link Surface} condition the skier explicitly wants to avoid
     */
    public void addDislikedSurface(Surface surface) {
        dislikedSurfaces.add(surface);
    }

    /**
     * Evaluates if the skier has a recorded positive preference for the specified surface condition.
     *
     * @param surface the surface condition to check
     * @return {@code true} if the skier likes the surface, {@code false} otherwise
     */
    boolean likesSurface(Surface surface) {
        return likedSurfaces.contains(surface);
    }

    /**
     * Evaluates if the skier has a recorded negative preference for the specified surface condition.
     *
     * @param surface the surface condition to check
     * @return {@code true} if the skier dislikes the surface, {@code false} otherwise
     */
    boolean dislikesSurface(Surface surface) {
        return dislikedSurfaces.contains(surface);
    }

    /**
     * Wipes all stored positive and negative preferences for both difficulties and surface conditions.
     * This resets the skier to a completely neutral preference state.
     */
    public void resetPreferences() {
        likedDifficulties.clear();
        dislikedDifficulties.clear();
        likedSurfaces.clear();
        dislikedSurfaces.clear();
    }
}