import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Route {
    private final List<SkiNode> path;
    private final List<LocalTime> times = new ArrayList<>();

    public Route(SkiNode startNode, LocalTime startTime) {
        this.path = new ArrayList<>();
        this.path.add(startNode);
        this.times.add(startTime);
    }

    public Route(Route previousRoute, SkiNode nextNode, LocalTime newTime) {
        this.path = new ArrayList<>(previousRoute.path);
        this.path.add(nextNode);
        this.times.add(newTime);
    }

    public List<SkiNode> getPath() {
        return path;
    }

    public LocalTime getCurrentTime() {
        return this.times.get(this.times.size() - 1);
    }

    public SkiNode getCurrentNode() {
        return path.get(path.size() - 1);
    }

    public Route getTruncatedRoute(int upToIndex) {
        Route truncated = new Route(this.path.get(0), this.times.get(0));

        for (int i = 0; i <= upToIndex; i++) {
            truncated.path.add(this.path.get(i));
            truncated.times.add(this.times.get(i));
        }

        return truncated;
    }

    public int calculateUtility(Skier skier) {
        Goal goal = skier.getGoal();
        int score = 0;
        Set<SkiNode> uniquePistes = new HashSet<>();

        for (SkiNode node : path) {
            if (node instanceof Piste) {
                Piste piste = (Piste) node;

                if (goal == Goal.ALTITUDE) {
                    score += piste.getAltitudeDifference();
                } else if (goal == Goal.DISTANCE) {
                    score += piste.getLength();
                } else if (goal == Goal.UNIQUE) {
                    if (uniquePistes.add(piste)) {
                        score += 1;
                    }
                }

                if (skier.dislikesSurface(piste.getSurface()) || skier.dislikesDifficulty(piste.getDifficulty())) {
                    score -= 100000;
                }
            }
        }

        return score;
    }

    public int calculatePreferenceScore(Skier skier) {
        int score = 0;

        for (SkiNode node : path) {
            if (node instanceof Piste) {
                Piste piste = (Piste) node;

                if (skier.likesDifficulty(piste.getDifficulty())) {
                    score++;
                } else if (skier.dislikesDifficulty(piste.getDifficulty())) {
                    score--;
                } else if (skier.likesSurface(piste.getSurface())) {
                    score++;
                } else if (skier.dislikesSurface(piste.getSurface())) {
                    score--;
                }
            }
        }

        return score;
    }

    public String getLexicographicalString() {
        StringBuilder sb = new StringBuilder();

        for (SkiNode node : path) {
            sb.append(node.getId()).append(" ");
        }

        return sb.toString().trim();
    }
}
