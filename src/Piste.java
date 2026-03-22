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

        double mDifficulty = switch (this.difficulty) {
            case BLUE -> 1.00;
            case RED -> 1.15;
            case BLACK -> 1.35;
        };

        double mSurface = switch (this.surface) {
            case REGULAR -> 1.00;
            case BUMPY -> 1.20;
            case ICY -> 1.30;
        };

        double mSkill = switch (skill) {
            case BEGINNER -> 1.35;
            case INTERMEDIATE -> 1.10;
            case EXPERT -> 0.90;
        };

        double gradient = (double) this.altitudeDifference / this.length;
        double multipliers = mDifficulty * mSurface * (1.0 + 2.0 * gradient) * mSkill;
        double timeInSeconds = (this.length / 8.0) * multipliers;

        return (int) Math.ceil(timeInSeconds);
    }
}
