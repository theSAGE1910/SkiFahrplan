package edu.kit.kastel;

/**
 * Command responsible for adding a preference (like or dislike) to the skier's profile.
 * Upon successfully registering a new preference, this command dynamically triggers
 * a recalculation of the active route.
 *
 * @author uxuwg
 * @version 0.1
 */
public class PreferenceCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 2;
    private static final int ARG_PREF_INDEX = 1;

    private static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax";
    private static final String ERROR_INVALID_PREF = "Error, Invalid preference. Must be a Difficulty or Surface.";

    private final boolean isLike;

    /**
     * Constructs a newly allocated {@code PreferenceCommand} object.
     *
     * @param isLike {@code true} if this command should add a "like" preference, {@code false} if it should add a "dislike" preference.
     */
    public PreferenceCommand(boolean isLike) {
        this.isLike = isLike;
    }

    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_SYNTAX);
            return;
        }

        String prefString = parts[ARG_PREF_INDEX];

        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.name().equals(prefString)) {
                if (this.isLike) {
                    session.getSkier().addLikedDifficulty(difficulty);
                } else {
                    session.getSkier().addDislikedDifficulty(difficulty);
                }
                session.triggerDynamicReplan();
                return;
            }
        }

        for (Surface surface : Surface.values()) {
            if (surface.name().equals(prefString)) {
                if (this.isLike) {
                    session.getSkier().addLikedSurface(surface);
                } else {
                    session.getSkier().addDislikedSurface(surface);
                }
                session.triggerDynamicReplan();
                return;
            }
        }

        System.err.println(ERROR_INVALID_PREF);
    }
}