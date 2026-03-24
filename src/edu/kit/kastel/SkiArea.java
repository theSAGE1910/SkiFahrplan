package edu.kit.kastel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Represents the entire ski area as a directed graph.
 * This class stores all {@link SkiNode} instances (such as lifts and pistes)
 * and maintains the connections (edges) between them to allow for route planning.
 *
 * @author uxuwg
 * @version 0.1
 */
public class SkiArea {

    private static final String ERROR_NODE_EXISTS_PREFIX = "Error, Node with id ";
    private static final String ERROR_NODE_EXISTS_SUFFIX = " already exists";
    private static final String ERROR_NODE_NOT_EXIST = "Error, Node does not exist";

    private final Map<SkiNode, List<SkiNode>> connections;
    private final Map<String, SkiNode> nodesById;

    /**
     * Constructs a new, empty {@code SkiArea} graph.
     */
    public SkiArea() {
        this.connections = new HashMap<>();
        this.nodesById = new HashMap<>();
    }

    /**
     * Adds a new node to the ski area.
     * If a node with the same identifier already exists, an error is printed.
     *
     * @param node the {@link SkiNode} to be added to the graph
     * @return {@code true} if the node was successfully added, or {@code false} if a node with the same ID already exists
     */
    public boolean addNode(SkiNode node) {
        String id = node.getId();

        if (this.nodesById.containsKey(id)) {
            System.err.println(ERROR_NODE_EXISTS_PREFIX + id + ERROR_NODE_EXISTS_SUFFIX);
            return false;
        }
        this.nodesById.put(id, node);
        connections.put(node, new ArrayList<>());
        return true;
    }

    /**
     * Adds a directed connection (edge) from one node to another within the ski area.
     * Both nodes must already exist in the graph.
     *
     * @param fromId the unique identifier of the starting node
     * @param toId the unique identifier of the destination node
     * @return {@code true} if the edge was successfully added, or {@code false} if either node does not exist
     */
    public boolean addEdges(String fromId, String toId) {
        SkiNode fromNode = this.nodesById.get(fromId);
        SkiNode toNode = this.nodesById.get(toId);

        if (fromNode == null || toNode == null) {
            System.err.println(ERROR_NODE_NOT_EXIST);
            return false;
        }
        connections.get(fromNode).add(toNode);
        return true;
    }

    /**
     * Retrieves a list of all lifts currently in the ski area,
     * sorted lexicographically by their identifiers.
     *
     * @return a sorted list of all {@link Lift} instances
     */
    public List<Lift> getLifts() {
        List<Lift> lifts = new ArrayList<>();

        for (SkiNode node : this.nodesById.values()) {
            if (node instanceof Lift) {
                lifts.add((Lift) node);
            }
        }

        lifts.sort((lift1, lift2) -> lift1.getId().compareTo(lift2.getId()));

        return lifts;
    }

    /**
     * Retrieves a list of all pistes currently in the ski area,
     * sorted lexicographically by their identifiers.
     *
     * @return a sorted list of all {@link Piste} instances
     */
    public List<Piste> getPistes() {
        List<Piste> pistes = new ArrayList<>();

        for (SkiNode node : this.nodesById.values()) {
            if (node instanceof Piste) {
                pistes.add((Piste) node);
            }
        }

        pistes.sort((piste1, piste2) -> piste1.getId().compareTo(piste2.getId()));

        return pistes;
    }

    /**
     * Retrieves a specific node from the ski area by its identifier.
     *
     * @param id the unique identifier of the requested node
     * @return the corresponding {@link SkiNode}, or {@code null} if it does not exist
     */
    public SkiNode getNode(String id) {
        return nodesById.get(id);
    }

    /**
     * Retrieves all immediate outgoing connections from a specific node.
     *
     * @param node the {@link SkiNode} whose outgoing connections are requested
     * @return a list of all connected destination nodes
     */
    public List<SkiNode> getConnections(SkiNode node) {
        return connections.get(node);
    }
}