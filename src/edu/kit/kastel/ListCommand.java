package edu.kit.kastel;

/**
 * Command responsible for displaying the components of the loaded ski area graph.
 * Provides the user with detailed, formatted information about all available
 * lifts or slopes within the currently active environment.
 *
 * @author uxuwg
 * @version 0.1
 */
public class ListCommand implements Command {

    /**
     * Outputs a formatted list of either all lifts or all pistes contained in the
     * current ski area, detailing their physical properties and operational parameters.
     *
     * @param parts the raw user input split by spaces
     * @param session the current state context of the application
     */
    @Override
    public void execute(String[] parts, SkiSession session) {
        if (session.getSkiArea() == null || session.getSkiArea().getLifts().isEmpty()) {
            System.err.println("Error, No ski area loaded.");
            return;
        }

        if (parts.length == 2 && parts[1].equals("lifts")) {
            for (Lift lift : session.getSkiArea().getLifts()) {
                System.out.println(lift.getId() + " " + lift.getType() + " "
                    + lift.getStartTime() + " " + lift.getEndTime() + " "
                    + lift.getRideDuration() + " " + lift.getWaitTime()
                    + (lift.isBaseStation() ? " TRANSIT" : ""));
            }
        } else if (parts.length == 2 && parts[1].equals("slopes")) {
            for (Piste piste : session.getSkiArea().getPistes()) {
                System.out.println(piste.getId() + " " + piste.getDifficulty() + " "
                    + piste.getSurface() + " " + piste.getLength() + " " + piste.getAltitudeDifference());
            }
        } else {
            System.err.println("Error, Invalid syntax.");
        }
    }
}