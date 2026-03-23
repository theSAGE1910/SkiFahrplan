package edu.kit.kastel;

import java.time.LocalTime;

/**
 * Represents the current state and context of an active interactive ski session.
 * This class holds all data required and modified by the various user commands,
 * acting as the central state manager for the Command Pattern architecture.
 *
 * @author uxuwg
 * @version 0.1
 */
public class SkiSession {

    private static final int ROUTE_INDEX_START = 0;
    private static final int TRUNCATION_OFFSET = 1;
    private static final int EMPTY_ROUTE_LENGTH = 0;

    private SkiArea skiArea;
    private final Skier skier;
    private boolean running;
    private Route plannedRoute;
    private int currentRouteIndex;
    private boolean nextWasCalled;
    private LocalTime activeEndTime;

    /**
     * Initializes a new interactive session for planning and tracking a skier's route.
     * Sets up the initial environment with the specified ski area and skier profile,
     * while initializing the route state to empty and the session loop to running.
     *
     * @param skiArea the initial {@link SkiArea} graph to navigate (can be null if not yet loaded)
     * @param skier the {@link Skier} profile tracking preferences and skills
     */
    public SkiSession(SkiArea skiArea, Skier skier) {
        this.skiArea = skiArea;
        this.skier = skier;
        this.running = true;
        this.plannedRoute = null;
        this.currentRouteIndex = ROUTE_INDEX_START;
        this.nextWasCalled = false;
    }

    /**
     * Dynamically recalculates the optimal route from the skier's current position and time.
     * This is automatically called when the skier's preferences, skill level, or goal change.
     */
    public void triggerDynamicReplan() {
        if (plannedRoute != null) {
            int truncatedIndex = Math.max(ROUTE_INDEX_START, currentRouteIndex - TRUNCATION_OFFSET);
            Route truncated = plannedRoute.getTruncatedRoute(truncatedIndex);

            RoutePlanner planner = new RoutePlanner(this.skiArea, this.skier);
            Route newRoute = planner.replan(truncated, activeEndTime);

            if (newRoute != null) {
                this.plannedRoute = newRoute;
            } else {
                this.plannedRoute = null;
                this.currentRouteIndex = ROUTE_INDEX_START;
                this.nextWasCalled = false;
            }
        }
    }

    /**
     * Calculates the functional end index of the planned route.
     *
     * @return the end index, ignoring the final base station ride as per routing rules
     */
    public int getRouteEndIndex() {
        if (plannedRoute == null) {
            return EMPTY_ROUTE_LENGTH;
        }

        int endIndex = plannedRoute.getPath().size();
        SkiNode lastNode = plannedRoute.getCurrentNode();

        if (lastNode instanceof Lift && ((Lift) lastNode).isBaseStation()) {
            endIndex--;
        }

        return endIndex;
    }

    /**
     * Retrieves the currently loaded ski area graph. This network of lifts and pistes
     * forms the foundation for all route calculations in the current session.
     *
     * @return the active {@link SkiArea} instance, or {@code null} if no area has been loaded yet
     */
    public SkiArea getSkiArea() {
        return skiArea;
    }

    /**
     * Overwrites the currently active ski area with a newly parsed map.
     * This is typically invoked when the user successfully loads a new area file.
     *
     * @param skiArea the new {@link SkiArea} graph to be navigated
     */
    public void setSkiArea(SkiArea skiArea) {
        this.skiArea = skiArea;
    }

    /**
     * Retrieves the profile of the skier associated with this session. The profile contains
     * the active skill level, optimization goal, and surface/difficulty preferences.
     *
     * @return the {@link Skier} instance bound to this session
     */
    public Skier getSkier() {
        return skier;
    }

    /**
     * Checks whether the interactive command loop should continue processing user input.
     * This flag is evaluated before prompting the user for the next command.
     *
     * @return {@code true} if the session is currently active, {@code false} if a termination command was issued
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Updates the execution state of the interactive loop. Setting this to {@code false}
     * causes the application to gracefully terminate after the current command finishes.
     *
     * @param running the new execution state of the session
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Retrieves the route currently planned for the skier. This route dictates the
     * sequence of nodes the skier is expected to visit throughout the timeframe.
     *
     * @return the active {@link Route}, or {@code null} if no route is currently planned
     */
    public Route getPlannedRoute() {
        return plannedRoute;
    }

    /**
     * Assigns a newly calculated route to be the active plan for the current session.
     * This replaces any previously planned path the skier was following.
     *
     * @param plannedRoute the new {@link Route} the skier should follow, or {@code null} to clear the active plan
     */
    public void setPlannedRoute(Route plannedRoute) {
        this.plannedRoute = plannedRoute;
    }

    /**
     * Retrieves the skier's current position index within the planned route's path sequence.
     * This index increments as the skier navigates successive steps along the route.
     *
     * @return the zero-based integer index corresponding to the skier's current node
     */
    public int getCurrentRouteIndex() {
        return currentRouteIndex;
    }

    /**
     * Updates the skier's progress by setting their current position index within the planned route.
     *
     * @param currentRouteIndex the new zero-based index representing the skier's updated position
     */
    public void setCurrentRouteIndex(int currentRouteIndex) {
        this.currentRouteIndex = currentRouteIndex;
    }

    /**
     * Checks if the 'next' command was the most recently executed action. This state restricts
     * certain progression commands (like 'take' or 'alternative') from being executed out of order.
     *
     * @return {@code true} if the user just queried the next step, {@code false} otherwise
     */
    public boolean isNextWasCalled() {
        return nextWasCalled;
    }

    /**
     * Updates the state tracking whether the 'next' command was just executed. This flag is
     * crucial for enforcing the strict chronological order of route navigation commands.
     *
     * @param nextWasCalled the boolean flag indicating if the strict sequence requirement is met
     */
    public void setNextWasCalled(boolean nextWasCalled) {
        this.nextWasCalled = nextWasCalled;
    }

    /**
     * Retrieves the strict deadline by which the skier must finish the currently planned route
     * and safely return to a base station.
     *
     * @return the cut-off {@link LocalTime} for the active route
     */
    public LocalTime getActiveEndTime() {
        return activeEndTime;
    }

    /**
     * Sets the strict deadline for the skier to complete their route. This boundary is used
     * whenever a dynamic replanning event occurs to ensure the new path remains valid.
     *
     * @param activeEndTime the new {@link LocalTime} deadline bounds
     */
    public void setActiveEndTime(LocalTime activeEndTime) {
        this.activeEndTime = activeEndTime;
    }
}