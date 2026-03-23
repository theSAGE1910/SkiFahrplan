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

        if (lines.isEmpty() || !lines.getFirst().strip().equals("graph")) {
            System.err.println("Error, File must start with 'graph'");
            return null;
        }

        SkiArea newArea = new SkiArea();

        for (String line : lines) {
            String stripped = line.strip();
            if (stripped.isEmpty() || stripped.equals("graph")) {
                continue;
            }

            boolean success = parseLine(stripped, newArea);
            if (!success) {
                System.err.println("Error, Invalid line format");
                return null;
            }
        }

        if (!validateArea(newArea)) {
            System.err.println("Error, Invalid Area configuration.");
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
                    return false;
                }
                newArea.addNode(lift);
                return true;
            }

            Matcher regularMatcher = REGULAR_LIFT_PATTERN.matcher(line);
            if (regularMatcher.matches()) {
                Lift lift = createLift(regularMatcher, false);
                if (lift == null) {
                    return false;
                }
                newArea.addNode(lift);
                return true;
            }

            Matcher pisteMatcher = PISTE_PATTERN.matcher(line);
            if (pisteMatcher.matches()) {
                Piste piste = createPiste(pisteMatcher);
                if (piste == null) {
                    return false;
                }
                newArea.addNode(piste);
                return true;
            }

            Matcher edgeMatcher = EDGE_PATTERN.matcher(line);
            if (edgeMatcher.matches()) {
                newArea.addEdges(edgeMatcher.group(1), edgeMatcher.group(2));
                return true;
            }
        } catch (DateTimeParseException e) {
            return false;
        }
        return false;
    }

    private static Lift createLift(Matcher matcher, boolean isTransit) {
        String id = matcher.group(1);
        LiftType type = LiftType.valueOf(matcher.group(2));
        LocalTime startTime = LocalTime.parse(matcher.group(3));
        LocalTime endTime = LocalTime.parse(matcher.group(4));

        if (!startTime.isBefore(endTime)) {
            return null;
        }

        int rideDuration = Integer.parseInt(matcher.group(5));
        int waitTime = Integer.parseInt(matcher.group(6));
        return new Lift(id, type, startTime, endTime, rideDuration, waitTime, isTransit);
    }

    private static Piste createPiste(Matcher matcher) {
        String id = matcher.group(1);
        Difficulty diff = Difficulty.valueOf(matcher.group(2));
        Surface surf = Surface.valueOf(matcher.group(3));
        int length = Integer.parseInt(matcher.group(4));
        int altitude = Integer.parseInt(matcher.group(5));

        if (length <= 0 || altitude < 0) {
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