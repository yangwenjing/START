package movement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import movement.map.MapNode;
import core.Settings;
import core.SettingsError;
import core.SimError;

public class FullConnectedMapTrans extends MapBasedMovement {
	
	/**
	 * ≥ı ºªØ
	 * @param settings
	 */
	public FullConnectedMapTrans(Settings settings) {
		super(settings);
			
	}
	
	private void checkMapConnectedness(List<MapNode> nodes) {
		Set<MapNode> visited = new HashSet<MapNode>();
		Queue<MapNode> unvisited = new LinkedList<MapNode>();
		MapNode firstNode;
		MapNode next = null;
		
		if (nodes.size() == 0) {
			throw new SimError("No map nodes in the given map");
		}
		
		firstNode = nodes.get(0);
		
		visited.add(firstNode);
		unvisited.addAll(firstNode.getNeighbors());
		
		while ((next = unvisited.poll()) != null) {
			visited.add(next);
			for (MapNode n: next.getNeighbors()) {
				if (!visited.contains(n) && ! unvisited.contains(n)) {
					unvisited.add(n);
				}
			}
		}
		
		if (visited.size() != nodes.size()) { // some node couldn't be reached
			MapNode disconnected = null;
			for (MapNode n : nodes) { // find an example node
				if (!visited.contains(n)) {
					disconnected = n;
					break;
				}
			}
			throw new SettingsError("SimMap is not fully connected. Only " + 
					visited.size() + " out of " + nodes.size() + " map nodes " +
					"can be reached from " + firstNode + ". E.g. " + 
					disconnected + " can't be reached");
		}
	}

}
