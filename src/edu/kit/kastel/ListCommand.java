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

    private static final int EXPECTED_ARGS_LENGTH = 2;
    private static final int ARG_TYPE_INDEX = 1;
    private static final String TYPE_LIFTS = "lifts";
    private static final String TYPE_SLOPES = "slopes";

    private static final String SEPARATOR = " ";
    private static final String TRANSIT_SUFFIX = " TRANSIT";

    private static final String ERROR_NO_AREA = "Error, No ski area loaded.";
    private static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax.";

    @Override
    public void execute(String[] parts, SkiSession session) {
        if (session.getSkiArea() == null || session.getSkiArea().getLifts().isEmpty()) {
            System.err.println(ERROR_NO_AREA);
            return;
        }

        if (parts.length == EXPECTED_ARGS_LENGTH && parts[ARG_TYPE_INDEX].equals(TYPE_LIFTS)) {
            for (Lift lift : session.getSkiArea().getLifts()) {
                System.out.println(lift.getId() + SEPARATOR + lift.getType() + SEPARATOR
                    + lift.getStartTime() + SEPARATOR + lift.getEndTime() + SEPARATOR
                    + lift.getRideDuration() + SEPARATOR + lift.getWaitTime()
                    + (lift.isBaseStation() ? TRANSIT_SUFFIX : ""));
            }
        } else if (parts.length == EXPECTED_ARGS_LENGTH && parts[ARG_TYPE_INDEX].equals(TYPE_SLOPES)) {
            for (Piste piste : session.getSkiArea().getPistes()) {
                System.out.println(piste.getId() + SEPARATOR + piste.getDifficulty() + SEPARATOR
                    + piste.getSurface() + SEPARATOR + piste.getLength() + SEPARATOR
                    + piste.getAltitudeDifference());
            }
        } else {
            System.err.println(ERROR_INVALID_SYNTAX);
        }
    }
}