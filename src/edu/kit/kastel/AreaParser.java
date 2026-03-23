package edu.kit.kastel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class responsible for parsing a file containing a text representation
 * of a ski area graph and constructing a validated {@link SkiArea} object.
 *
 * @author uxuwg
 * @version 0.1
 */
public final class AreaParser {

    private static final Pattern TRANSIT_LIFT_PATTERN =
        Pattern.compile("^([a-zA-Z0-9_]+)\\[\\[\\1<br/>(GONDOLA|CHAIRLIFT);(\\d{2}:\\d{2});(\\d{2}:\\d{2});(\\d+);(\\d+)]]$");
    private static final Pattern REGULAR_LIFT_PATTERN =
        Pattern.compile("^([a-zA-Z0-9_]+)\\[\\1<br/>(GONDOLA|CHAIRLIFT);(\\d{2}:\\d{2});(\\d{2}:\\d{2});(\\d+);(\\d+)]$");
    private static final Pattern PISTE_PATTERN =
        Pattern.compile("^([a-zA-Z0-9_]+)\\(\\[\\1<br/>(BLUE|RED|BLACK);(REGULAR|ICY|BUMPY);(\\d+);(\\d+)]\\)$");
    private static final Pattern EDGE_PATTERN =
        Pattern.compile("^([a-zA-Z0-9_]+)\\s*-->\\s*([a-zA-Z0-9_]+)$");

    private static final String GRAPH_KEYWORD = "graph";
    private static final String ERROR_FILE_START = "Error, File must start with 'graph'";
    private static final String ERROR_INVALID_AREA = "Error, Invalid Area configuration.";
    private static final String ERROR_INVALID_LINE = "Error, Invalid line format";
    private static final String ERROR_INVALID_TIME = "Error, Invalid time value provided";
    private static final String ERROR_LIFT_TIME = "Error, Lift start time must be before end time";
    private static final String ERROR_PISTE_DIMENSIONS = "Error, Piste length must be > 0 and altitude >= 0";

    private static final int GROUP_ID = 1;

    private static final int LIFT_GROUP_TYPE = 2;
    private static final int LIFT_GROUP_START_TIME = 3;
    private static final int LIFT_GROUP_END_TIME = 4;
    private static final int LIFT_GROUP_RIDE_DURATION = 5;
    private static final int LIFT_GROUP_WAIT_TIME = 6;

    private static final int PISTE_GROUP_DIFFICULTY = 2;
    private static final int PISTE_GROUP_SURFACE = 3;
    private static final int PISTE_GROUP_LENGTH = 4;
    private static final int PISTE_GROUP_ALTITUDE = 5;

    private static final int EDGE_GROUP_FROM = 1;
    private static final int EDGE_GROUP_TO = 2;

    private static final int MINIMUM_VALID_VALUE = 0;

    private AreaParser() {
    }

    /**
     * Parses a text file defining a ski area and constructs the corresponding graph.
     *
     * @param filepath the path to the text file containing the ski area definition
     * @return the fully constructed {@link SkiArea}, or {@code null} if validation fails
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static SkiArea parse(String filepath) throws IOException {
        Path path = Paths.get(filepath);
        List<String> lines = Files.readAllLines(path);

        for (String line : lines) {
            System.out.println(line);
        }

        if (lines.isEmpty() || !lines.getFirst().strip().equals(GRAPH_KEYWORD)) {
            System.err.println(ERROR_FILE_START);
            return null;
        }

        SkiArea newArea = new SkiArea();

        for (String line : lines) {
            String stripped = line.strip();
            if (stripped.isEmpty() || stripped.equals(GRAPH_KEYWORD)) {
                continue;
            }

            boolean success = parseLine(stripped, newArea);
            if (!success) {
                return null;
            }
        }

        if (!validateArea(newArea)) {
            System.err.println(ERROR_INVALID_AREA);
            return null;
        }

        return newArea;
    }

    private static boolean parseLine(String line, SkiArea newArea) {
        try {
            Matcher transitMatcher = TRANSIT_LIFT_PATTERN.matcher(line);
            if (transitMatcher.matches()) {
                Lift lift = createLift(transitMatcher, true);
                if (lift == null) {
                    System.err.println(ERROR_LIFT_TIME);
                    return false;
                }
                return newArea.addNode(lift);
            }

            Matcher regularMatcher = REGULAR_LIFT_PATTERN.matcher(line);
            if (regularMatcher.matches()) {
                Lift lift = createLift(regularMatcher, false);
                if (lift == null) {
                    System.err.println(ERROR_LIFT_TIME);
                    return false;
                }
                return newArea.addNode(lift);
            }

            Matcher pisteMatcher = PISTE_PATTERN.matcher(line);
            if (pisteMatcher.matches()) {
                Piste piste = createPiste(pisteMatcher);
                if (piste == null) {
                    System.err.println(ERROR_PISTE_DIMENSIONS);
                    return false;
                }
                return newArea.addNode(piste);
            }

            Matcher edgeMatcher = EDGE_PATTERN.matcher(line);
            if (edgeMatcher.matches()) {
                return newArea.addEdges(edgeMatcher.group(EDGE_GROUP_FROM), edgeMatcher.group(EDGE_GROUP_TO));
            }
        } catch (DateTimeParseException e) {
            System.err.println(ERROR_INVALID_TIME);
            return false;
        }

        System.err.println(ERROR_INVALID_LINE);
        return false;
    }

    private static Lift createLift(MatchResult matcher, boolean isTransit) {
        String id = matcher.group(GROUP_ID);
        LiftType type = LiftType.valueOf(matcher.group(LIFT_GROUP_TYPE));
        LocalTime startTime = LocalTime.parse(matcher.group(LIFT_GROUP_START_TIME));
        LocalTime endTime = LocalTime.parse(matcher.group(LIFT_GROUP_END_TIME));

        if (!startTime.isBefore(endTime)) {
            return null;
        }

        int rideDuration = Integer.parseInt(matcher.group(LIFT_GROUP_RIDE_DURATION));
        int waitTime = Integer.parseInt(matcher.group(LIFT_GROUP_WAIT_TIME));
        return new Lift(id, type, startTime, endTime, rideDuration, waitTime, isTransit);
    }

    private static Piste createPiste(MatchResult matcher) {
        String id = matcher.group(GROUP_ID);
        Difficulty diff = Difficulty.valueOf(matcher.group(PISTE_GROUP_DIFFICULTY));
        Surface surf = Surface.valueOf(matcher.group(PISTE_GROUP_SURFACE));
        int length = Integer.parseInt(matcher.group(PISTE_GROUP_LENGTH));
        int altitude = Integer.parseInt(matcher.group(PISTE_GROUP_ALTITUDE));

        if (length <= MINIMUM_VALID_VALUE || altitude < MINIMUM_VALID_VALUE) {
            return null;
        }

        return new Piste(id, diff, surf, length, altitude);
    }

    private static boolean validateArea(SkiArea area) {
        if (!hasRequiredNodes(area)) {
            return false;
        }

        List<SkiNode> allNodes = new ArrayList<>();
        allNodes.addAll(area.getLifts());
        allNodes.addAll(area.getPistes());

        return hasValidEdges(area, allNodes) && isStronglyConnected(area, allNodes);
    }

    private static boolean isStronglyConnected(SkiArea area, List<SkiNode> allNodes) {
        for (SkiNode startNode : allNodes) {
            if (!canReachAll(area, allNodes, startNode)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canReachAll(SkiArea area, List<SkiNode> allNodes, SkiNode startNode) {
        Set<SkiNode> visited = new HashSet<>();
        Queue<SkiNode> queue = new LinkedList<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            SkiNode current = queue.poll();
            List<SkiNode> connections = area.getConnections(current);

            if (connections != null) {
                for (SkiNode next : connections) {
                    if (visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
        }

        return visited.size() == allNodes.size();
    }

    private static boolean hasValidEdges(SkiArea area, List<SkiNode> allNodes) {
        for (SkiNode node : allNodes) {
            List<SkiNode> connections = area.getConnections(node);

            if (connections == null || connections.isEmpty()) {
                return false;
            }

            for (SkiNode target : connections) {
                if (node.equals(target) || hasInvalidSymmetry(area, node, target)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasInvalidSymmetry(SkiArea area, SkiNode node, SkiNode target) {
        List<SkiNode> targetConnections = area.getConnections(target);
        return targetConnections != null && targetConnections.contains(node) && node.getClass() == target.getClass();
    }

    private static boolean hasRequiredNodes(SkiArea area) {
        List<Lift> lifts = area.getLifts();
        List<Piste> pistes = area.getPistes();

        if (lifts == null || pistes == null || lifts.isEmpty() || pistes.isEmpty()) {
            return false;
        }

        for (Lift lift : lifts) {
            if (lift.isBaseStation()) {
                return true;
            }
        }
        return false;
    }
}