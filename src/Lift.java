import java.time.LocalTime;

public class Lift implements SkiNode {

    LiftType type;
    LocalTime startTime;
    LocalTime endTime;
    int rideDuration;
    int waitTime;
    boolean isBaseStation;

    @Override
    public String getId() {
        return "";
    }
}
