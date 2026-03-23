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
        if (parts.length != 3) {
            System.err.println("Error, Invalid command");
            return;
        }

        if (parts[1].equals("skill")) {
            for (SkillLevel skill : SkillLevel.values()) {
                if (skill.name().equals(parts[2])) {
                    session.getSkier().setSkillLevel(skill);
                    session.triggerDynamicReplan();
                    return;
                }
            }
            System.err.println("Error, Invalid skill level");

        } else if (parts[1].equals("goal")) {
            for (Goal goal : Goal.values()) {
                if (goal.name().equals(parts[2])) {
                    session.getSkier().setGoal(goal);
                    session.triggerDynamicReplan();
                    return;
                }
            }
            System.err.println("Error, Invalid goal name");

        } else {
            System.err.println("Error, Invalid command");
        }
    }
}