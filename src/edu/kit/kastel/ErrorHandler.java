package edu.kit.kastel;

/**
 * Utility class containing shared error messages used across multiple commands.
 *
 * @author uxuwg
 * @version 0.1
 */
public final class ErrorHandler {

    /**
     * Shared error message for invalid syntax.
     */
    public static final String ERROR_INVALID_SYNTAX = "Error, Invalid syntax.";

    /**
     * Shared error message for when no route is planned or it has finished.
     */
    public static final String ERROR_NO_ROUTE_OR_FINISHED = "Error, No route planned or route already finished.";

    private ErrorHandler() {
    }
}
