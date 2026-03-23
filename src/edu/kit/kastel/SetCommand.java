package edu.kit.kastel;

/**
 * Command responsible for configuring the fundamental traits of the skier's profile.
 * Modifying the skill level or the optimization goal changes how the algorithm
 * calculates travel times and utility scores, triggering an immediate route recalculation.
 *
 * @author uxuwg
 * @version 0.1
 */
public class SetCommand implements Command {

    private static final int EXPECTED_ARGS_LENGTH = 3;
    private static final int ARG_TYPE_INDEX = 1;
    private static final int ARG_VALUE_INDEX = 2;

    private static final String TYPE_SKILL = "skill";
    private static final String TYPE_GOAL = "goal";

    private static final String ERROR_INVALID_COMMAND = "Error, Invalid command";
    private static final String ERROR_INVALID_SKILL = "Error, Invalid skill level";
    private static final String ERROR_INVALID_GOAL = "Error, Invalid goal name";

    /**
     * Executes the profile update logic. Parses the requested skill or goal from
     * the user input and applies it to the active skier object, dynamically replanning
     * the route if one is already active.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (parts.length != EXPECTED_ARGS_LENGTH) {
            System.err.println(ERROR_INVALID_COMMAND);
            return;
        }

        if (parts[ARG_TYPE_INDEX].equals(TYPE_SKILL)) {
            for (SkillLevel skill : SkillLevel.values()) {
                if (skill.name().equals(parts[ARG_VALUE_INDEX])) {
                    session.getSkier().setSkillLevel(skill);
                    session.triggerDynamicReplan();
                    return;
                }
            }
            System.err.println(ERROR_INVALID_SKILL);

        } else if (parts[ARG_TYPE_INDEX].equals(TYPE_GOAL)) {
            for (Goal goal : Goal.values()) {
                if (goal.name().equals(parts[ARG_VALUE_INDEX])) {
                    session.getSkier().setGoal(goal);
                    session.triggerDynamicReplan();
                    return;
                }
            }
            System.err.println(ERROR_INVALID_GOAL);

        } else {
            System.err.println(ERROR_INVALID_COMMAND);
        }
    }
}