import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AreaParser {

    private static final Pattern TRANSIT_LIFT_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+)\\[\\[\\1<br/>(GONDOLA|CHAIRLIFT);(\\d{2}:\\d{2});(\\d{2}:\\d{2});(\\d+);(\\d+)]]$");
    private static final Pattern REGULAR_LIFT_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+)\\[\\1<br/>(GONDOLA|CHAIRLIFT);(\\d{2}:\\d{2});(\\d{2}:\\d{2});(\\d+);(\\d+)]$");
    private static final Pattern PISTE_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+)\\(\\[\\1<br/>(BLUE|RED|BLACK);(REGULAR|ICY|BUMPY);(\\d+);(\\d+)]\\)$");
    private static final Pattern EDGE_PATTERN = Pattern.compile("^([a-zA-Z0-9_]+)\\s*-->\\s*([a-zA-Z0-9_]+)$");

    public static SkiArea parse(String filepath) throws IOException {

        Path path = Paths.get(filepath);
        List<String> lines = Files.readAllLines(path);

        SkiArea newArea = new SkiArea();

        if (lines.isEmpty() || !lines.get(0).equals("graph")) {
            System.err.println("Error: File must start with 'graph'");
        }

        for (String line : lines) {
            System.out.println(line);

            if (!line.strip().equals("graph")) {
                parseLine(line.strip(), newArea);
            }
        }

        return newArea;
    }

    private static void parseLine(String line, SkiArea newArea) {
        Matcher transitMatcher = TRANSIT_LIFT_PATTERN.matcher(line);
        if (transitMatcher.matches()) {
            newArea.addNode(createLift(transitMatcher, true));
            return;
        }

        Matcher regularMatcher = REGULAR_LIFT_PATTERN.matcher(line);
        if (regularMatcher.matches()) {
            newArea.addNode(createLift(regularMatcher, false));
            return;
        }

        Matcher pisteMatcher = PISTE_PATTERN.matcher(line);
        if (pisteMatcher.matches()) {
            newArea.addNode(createPiste(pisteMatcher));
            return;
        }

        Matcher edgeMatcher = EDGE_PATTERN.matcher(line);
        if (edgeMatcher.matches()) {
            newArea.addEdges(edgeMatcher.group(1), edgeMatcher.group(2));
            return;
        }

        System.err.println("ERROR: Invalid line format");
    }

    private static Lift createLift(Matcher matcher, boolean isTransit) {
        String id = matcher.group(1);
        LiftType type = LiftType.valueOf(matcher.group(2));
        LocalTime startTime = LocalTime.parse(matcher.group(3));
        LocalTime endTime = LocalTime.parse(matcher.group(4));
        int rideDuration = Integer.parseInt(matcher.group(5));
        int waitTime = Integer.parseInt(matcher.group(6));
        return new Lift(id, type, startTime, endTime, rideDuration, waitTime,  isTransit);
    }

    private static Piste createPiste(Matcher matcher) {
        String id = matcher.group(1);
        Difficulty diff = Difficulty.valueOf(matcher.group(2));
        Surface surf = Surface.valueOf(matcher.group(3));
        int length = Integer.parseInt(matcher.group(4));
        int altitude = Integer.parseInt(matcher.group(5));
        return new Piste(id, diff, surf, length, altitude);
    }
}
