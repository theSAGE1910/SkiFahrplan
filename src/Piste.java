public class Piste implements SkiNode {

    private final String id;
    private final Difficulty difficulty;
    private final Surface surface;
    private final int length;
    private final int altitudeDifference;

    public Piste(String id, Difficulty difficulty, Surface surface, int length, int altitudeDifference) {
        this.id = id;
        this.difficulty = difficulty;
        this.surface = surface;
        this.length = length;
        this.altitudeDifference = altitudeDifference;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public Surface getSurface() {
        return surface;
    }
    public int getLength() {
        return length;
    }
    public int getAltitudeDifference() {
        return altitudeDifference;
    }

    public int calculateTravelTime(SkillLevel skill) {
        return 0;
    }
}
