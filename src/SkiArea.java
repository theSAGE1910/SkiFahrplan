import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class SkiArea {

    private final Map<SkiNode, List<SkiNode>> connections;
    private final Map<String, SkiNode> nodesById;

    public SkiArea() {
        this.connections = new HashMap<>();
        this.nodesById = new HashMap<>();
    }

    public void addNode(SkiNode node) {
        if (this.nodesById.containsKey(node.getId())) {
            System.err.println("Error: Node with id " + node.getId() + " already exists");
        }
        this.nodesById.put(node.getId(), node);
        connections.put(node, new ArrayList<>());
    }

    public void addEdges(String fromId, String toId) {
        SkiNode fromNode = this.nodesById.get(fromId);
        SkiNode toNode = this.nodesById.get(toId);

        if (fromNode == null || toNode == null) {
            System.err.println("Error: Node does not exist");
        }
        connections.get(fromNode).add(toNode);
    }

    public List<Lift>  getLifts() {
        List<Lift> lifts = new ArrayList<>();

        for (SkiNode node : this.nodesById.values()) {

            if (node instanceof Lift) {
                lifts.add((Lift) node);
            }
        }

        lifts.sort(new  Comparator<Lift>() {
            @Override
            public int compare(Lift l1, Lift l2) {
                return l1.getId().compareTo(l2.getId());
            }
        });

        return lifts;
    }

    public List<Piste> getPistes() {
        List<Piste> pistes = new ArrayList<>();

        for (SkiNode node : this.nodesById.values()) {

            if (node instanceof Piste) {
                pistes.add((Piste) node);
            }
        }

        pistes.sort(new  Comparator<Piste>() {
            @Override
            public int compare(Piste p1, Piste p2) {
                return p1.getId().compareTo(p2.getId());
            }
        });

        return pistes;
    }

    public SkiNode getNode(String id) {
        return nodesById.get(id);
    }

    public List<SkiNode> getConnections(SkiNode node) {
        return connections.get(node);
        //return connections.getOrDefault(node, new ArrayList<>());
    }

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
