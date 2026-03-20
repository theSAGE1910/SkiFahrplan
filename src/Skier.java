import java.util.HashSet;
import java.util.Set;

public class Skier {

    private SkillLevel skillLevel;
    private Goal goal;
    private final Set<Difficulty> likedDifficulties;
    private final Set<Surface> dislikedSurfaces;

    public Skier() {
        this.likedDifficulties = new HashSet<>();
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

    public void addDislikedSurface(Surface surface) {
        dislikedSurfaces.add(surface);
    }

    public void resetPreferences() {
        likedDifficulties.clear();
        dislikedSurfaces.clear();
    }
}
