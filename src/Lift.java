import java.time.LocalTime;

public class Lift implements SkiNode {

    private final String id;
    private final LiftType type;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int rideDuration;
    private final int waitTime;
    private final boolean isBaseStation;

    public Lift(String id, LiftType type, LocalTime startTime, LocalTime endTime, int rideDuration, int waitTime, boolean isBaseStation) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rideDuration = rideDuration;
        this.waitTime = waitTime;
        this.isBaseStation = isBaseStation;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public LiftType getType() {
        return type;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public int getRideDuration() {
        return rideDuration;
    }
    public int getWaitTime() {
        return waitTime;
    }
    public boolean isBaseStation() {
        return isBaseStation;
    }
}
