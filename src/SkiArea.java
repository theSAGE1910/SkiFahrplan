import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkiArea {
    Map<SkiNode, List<SkiNode>> connections;

    public SkiArea() {
        this.connections = new HashMap<>();
    }

    public void addNode(SkiNode node) {}

    public void addEdges(SkiNode node1, SkiNode node2) {}

    public boolean validateConnection(SkiNode node1, SkiNode node2) {
        return false;
    }

    public boolean isBaseStation() {
        return false;
    }

    public boolean isPiste() {
        return false;
    }
}
