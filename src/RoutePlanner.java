import java.time.LocalTime;

public class RoutePlanner {
    private final SkiArea area;
    private final Skier skier;
    private Route bestRoute = null;
    private int bestScore = -1;

    public RoutePlanner(SkiArea area, Skier skier) {
        this.area = area;
        this.skier = skier;
    }

    public Route plan(String startLiftId, LocalTime startTime, LocalTime endTime) {
        SkiNode startNode = area.getNode(startLiftId);

        if (startNode == null) {
            throw new IllegalArgumentException("ERROR: Unknown start node");
        }
        if (!(startNode instanceof Lift) || !((Lift) startNode).isBaseStation()) {
            throw new IllegalArgumentException("ERROR: Start node must be a base station (TRANSIT lift).");
        }
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("ERROR: Start time must be after end time.");
        }

        Route initialRoute = new Route((Lift) startNode, startTime);
        this.bestRoute = null;

        findBestRoute(initialRoute, endTime);

        return bestRoute;
    }

    private void findBestRoute(Route currentRoute, LocalTime endTime) {
        SkiNode currentNode = currentRoute.getCurrentNode();

        if (currentNode instanceof Lift &&
                ((Lift) currentNode).isBaseStation() &&
                currentRoute.getPath().size() > 1) {

            evaluateAndSaveIfBest(currentRoute);

            int score = currentRoute.getPath().size();
            if (score > bestScore) {
                bestScore = score;
                bestRoute = currentRoute;
            }
        }

        for (SkiNode nextNode : area.getConnections(currentNode)) {
            LocalTime nextTime = calculateNextTime(currentRoute.getCurrentTime(), nextNode);

            if (nextTime != null && !nextTime.isAfter(endTime)) {
                Route nextRoute = new Route(currentRoute, nextNode, nextTime);
                findBestRoute(nextRoute, endTime);
            }
        }

    }

    private LocalTime calculateNextTime(LocalTime arrivalTime, SkiNode nextNode) {
        if (nextNode instanceof Lift) {
            Lift lift = (Lift) nextNode;

            LocalTime readyToBoard = arrivalTime.plusMinutes(lift.getWaitTime());
            LocalTime actualBoardingTime = readyToBoard.isBefore(lift.getStartTime())
                    ? lift.getStartTime() : readyToBoard;

            if (actualBoardingTime.isAfter(lift.getEndTime())) {
                return null;
            }

            return actualBoardingTime.plusMinutes(lift.getRideDuration());
        } else if (nextNode instanceof Piste) {
            Piste piste = (Piste) nextNode;

            int travelSeconds = piste.calculateTravelTime(skier.getSkillLevel());
            return arrivalTime.plusSeconds(travelSeconds);
        }

        return null;
    }

    private void evaluateAndSaveIfBest(Route newRoute) {
        if (bestRoute == null) {
            bestRoute = newRoute;
        }

        int newUtility = newRoute.calculateUtility(skier.getGoal());
        int bestUtility = bestRoute.calculateUtility(skier.getGoal());

        if (newUtility > bestUtility) {
            bestRoute = newRoute;
        } else if (newUtility == bestUtility) {

            int newPreference = newRoute.calculatePreferenceScore(skier);
            int bestPreference = bestRoute.calculatePreferenceScore(skier);

            if (newPreference > bestPreference) {
                bestRoute = newRoute;
            } else if (newPreference == bestPreference) {

                String newString = newRoute.getLexicographicalString();
                String bestString = bestRoute.getLexicographicalString();

                if (newString.compareTo(bestString) < 0) {
                    bestRoute = newRoute;
                }
            }
        }
    }

}
