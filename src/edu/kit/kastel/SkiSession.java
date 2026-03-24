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
     * Checks if the 'next' command was NOT the most recently executed action.
     * This state restricts certain progression commands (like 'take' or 'alternative')
     * from being executed out of order, requiring the user to preview the next step first.
     *
     * @return {@code true} if the user has NOT just queried the next step,
     *      {@code false} if the 'next' command was the last action performed
     */
    public boolean isNextNotCalled() {
        return !nextWasCalled;
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

    /**
     * Aborts the currently active route and resets the skier's navigation state.
     * This clears the planned path and resets the progress tracking to the beginning.
     *
     * @return true if a route was active and successfully cleared, false if no route was planned
     */
    public boolean abortRoute() {
        if (this.plannedRoute == null) {
            return false;
        }

        this.plannedRoute = null;
        this.currentRouteIndex = ROUTE_INDEX_START;
        this.nextWasCalled = false;

        return true;
    }

    /**
     * Resets the current session state to accommodate a newly loaded ski area.
     * This process overrides the old area, clears any actively planned routes, and resets the navigation progress.
     *
     * @param newArea the new ski area graph to be navigated in this session
     */
    public void resetForNewArea(SkiArea newArea) {
        this.skiArea = newArea;
        this.plannedRoute = null;
        this.currentRouteIndex = ROUTE_INDEX_START;
        this.nextWasCalled = false;
    }

    /**
     * Concludes the current route by clearing the planned path and resetting navigation flags.
     * This is typically called when the skier has successfully reached the final destination of their plan.
     */
    public void finishRoute() {
        this.plannedRoute = null;
        this.nextWasCalled = false;
    }

    /**
     * Determines whether the skier has reached or passed the end of their currently planned route.
     *
     * @return true if the current route index meets or exceeds the route's end index, false otherwise
     */
    public boolean isRouteFinished() {
        return this.currentRouteIndex >= getRouteEndIndex();
    }

    /**
     * Checks whether there is currently a valid route assigned to the skier's session.
     *
     * @return true if a route is currently planned, false if the planned route is null
     */
    public boolean hasActiveRoute() {
        return this.plannedRoute != null;
    }

    /**
     * Advances the skier's progression to the next node in the planned route.
     * This action evaluates whether a route is active, unfinished, and if the user has
     * legally previewed the upcoming step before allowing progression.
     *
     * @return true if the skier successfully advanced, false if progression is blocked by invalid state
     */
    public boolean advanceToNextNode() {
        if (plannedRoute == null || currentRouteIndex >= getRouteEndIndex() || !nextWasCalled) {
            return false;
        }

        currentRouteIndex++;
        nextWasCalled = false;
        return true;
    }
}