public class Piste implements SkiNode {

    Difficulty difficulty;
    Surface surface;
    int length;
    int altitudeDifference;

    @Override
    public String getId() {
        return "";
    }

    public int calculateTravelTime() {
        return 0;
    }
}
