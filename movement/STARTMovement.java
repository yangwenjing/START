/**
 * 
 */
package movement;

import java.util.List;
import java.util.Random;

import movement.map.MapNode;
import core.Coord;
import core.Settings;
import core.SimClock;

/**
 * @author Yang Wenjing
 *
 */
public class STARTMovement extends ShortestPathMapBasedMovement {
	
	/** 区分车辆状态 */
	private int status;
	
	/** 判断是否超过持续时长 */
	private int timer;
	
	private static Regions regions=null;
	
	/** 状态0  设置持续时长的参数 */
	private static double DURATION_A_FOR_STATUS0 = 0.971101;
	private static double DURATION_A_FOR_STATUS1 = 0.988955;
	private static double DURATION_PARA_FOR_STATUS0 = 0.00217593;
	/** 状态1 的持续时长参数*/
	private static double DURATION_PARA_FOR_STATUS1 = 0.00103644;
		

	/**
	 * @param settings
	 */
	public STARTMovement(Settings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param mbm
	 */
	public STARTMovement(ShortestPathMapBasedMovement mbm) {
		super(mbm);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		/**
		 * TODO:在这里实现
		 * 1.找到目的节点
		 * 2.获取path
		 * 3.将path返回
		 */
		MapNode to = selectDestination();
		
		List<MapNode> nodePath = getPathFinder().getShortestPath(lastMapNode, to);
		
		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";
				
		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}
		
		lastMapNode = to;
		
		return p;
	}	
	
	@Override
	public Coord getInitialLocation() {
		/**
		 * 初始化节点位置
		 * 在DTNHost中被调用
		 */
		List<MapNode> nodes = getMap().getNodes();
		MapNode n,n2;
		Coord n2Location, nLocation, placement;
		double dx, dy;
		double rnd = rng.nextDouble();
		
		// choose a random node (from OK types if such are defined)
		//do {
			n = nodes.get(rng.nextInt(nodes.size()));
		//} while (okMapNodeTypes != null && !n.isType(okMapNodeTypes));
		
		// choose a random neighbor of the selected node
		n2 = n.getNeighbors().get(rng.nextInt(n.getNeighbors().size())); 
		
		nLocation = n.getLocation();
		n2Location = n2.getLocation();
		
		placement = n.getLocation().clone();
		
		dx = rnd * (n2Location.getX() - nLocation.getX());
		dy = rnd * (n2Location.getY() - nLocation.getY());
		
		placement.translate(dx, dy); // move coord from n towards n2
		
		this.lastMapNode = n;
		return placement;
	}
	
	/**
	 * 由现有的lastMapNode找到目的节点
	 * @return 目的MapNode节点
	 */
	public MapNode selectDestination()
	{
		//TODO:设置如何写timer
		return null;
		
	}
	
	private void setTimer() {
		this.timer = SimClock.getIntTime()+(int)generateLastingTime(this.status);
	}
	private double generateLastingTime(int status)
	{
		Random rd1 = new Random();
		double seed =  rd1.nextDouble();
		if(status==0)
			return generateLastingTimeForStatus0(seed);
		else
			return generateLastingTimeForStatus1(seed);
		
	}
	private double generateLastingTimeForStatus1(double seed)
	{
		int maxLength = 20000;
		int tmpLen_bak_max = maxLength;
		int tmpLen_bak_min = 0;
		int tmpLen = maxLength/2;
		if(seed>=cumulativeLastingTimeForStatus1(maxLength))return maxLength;
		if(seed<=cumulativeLastingTimeForStatus1(0)) return 0;
		
		
		do{
			if(seed<cumulativeLastingTimeForStatus1(tmpLen))
			{
				tmpLen_bak_max = tmpLen;
				tmpLen = (tmpLen_bak_max-tmpLen_bak_min)/2+tmpLen_bak_min;
			}
			else if(seed>cumulativeLastingTimeForStatus1(tmpLen))
			{
				tmpLen_bak_min = tmpLen;
				tmpLen = (tmpLen_bak_max-tmpLen_bak_min)/2+tmpLen_bak_min;
			}
			else
				return tmpLen;
				
		}
		while(Math.abs(tmpLen_bak_max-tmpLen_bak_min)<=1);
			
		return tmpLen;
		
		
	}
	
	
	private double generateLastingTimeForStatus0(double seed)
	{
		int maxLength = 20000;
		int tmpLen_bak_max = maxLength;
		int tmpLen_bak_min = 0;
		int tmpLen = maxLength/2;
		if(seed>=cumulativeLastingTimeForStatus0(maxLength))return maxLength;
		if(seed<=cumulativeLastingTimeForStatus0(0)) return 0;
		
		//System.out.println("calculate lasting time...");
		do{
			if(seed<cumulativeLastingTimeForStatus0(tmpLen))
			{
				tmpLen_bak_max = tmpLen;
				tmpLen = (tmpLen_bak_max-tmpLen_bak_min)/2+tmpLen_bak_min;
			}
			else if(seed>cumulativeLastingTimeForStatus0(tmpLen))
			{
				tmpLen_bak_min = tmpLen;
				tmpLen = (tmpLen_bak_max-tmpLen_bak_min)/2+tmpLen_bak_min;
			}
			else
				return tmpLen;
				
		}
		while(Math.abs(tmpLen_bak_max-tmpLen_bak_min)<=100);
		//System.out.println("finish calculating lasting time...");
		return tmpLen;
		
		
	}
	
	private double cumulativeLastingTimeForStatus0(int timeLength)
	{
		if(timeLength<0) return 0;
		return DURATION_A_FOR_STATUS0-Math.exp(-DURATION_PARA_FOR_STATUS0*timeLength);
		
	}
	private double cumulativeLastingTimeForStatus1(int timeLength)
	{
		if(timeLength<0) return 0;
		return DURATION_A_FOR_STATUS1-Math.exp(-DURATION_PARA_FOR_STATUS1*timeLength);
		
	}
	

}
