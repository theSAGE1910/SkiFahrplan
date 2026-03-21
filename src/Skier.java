import java.util.HashSet;
import java.util.Set;

public class Skier {

    private SkillLevel skillLevel;
    private Goal goal;
    private final Set<Difficulty> likedDifficulties;
    private final Set<Difficulty> dislikedDifficulties;
    private final Set<Surface> likedSurfaces;
    private final Set<Surface> dislikedSurfaces;

    public Skier() {
        this.likedDifficulties = new HashSet<>();
        this.dislikedDifficulties = new HashSet<>();
        this.likedSurfaces = new HashSet<>();
        this.dislikedSurfaces = new HashSet<>();
    }

    public SkillLevel getSkillLevel() {
        return skillLevel;
    }
    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Goal getGoal() {
        return goal;
    }
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public void addLikedDifficulty(Difficulty difficulty) {
        likedDifficulties.add(difficulty);
    }

    public void addDislikedDifficulty(Difficulty difficulty) {
        dislikedDifficulties.add(difficulty);
    }

    public boolean likesDifficulty(Difficulty d) {
        return likedDifficulties.contains(d);
    }

    public boolean dislikesDifficulty(Difficulty d) {
        return dislikedDifficulties.contains(d);
    }

    public void addLikedSurface(Surface surface) {
        likedSurfaces.add(surface);
    }

    public void addDislikedSurface(Surface surface) {
        dislikedSurfaces.add(surface);
    }

    public boolean likesSurface(Surface d) {
        return likedSurfaces.contains(d);
    }

    public boolean dislikesSurface(Surface d) {
        return dislikedSurfaces.contains(d);
    }

    public void resetPreferences() {
        likedDifficulties.clear();
        dislikedSurfaces.clear();
    }
}
