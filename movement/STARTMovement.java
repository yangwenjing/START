/**
 * 
 */
package movement;

import java.util.List;
import java.util.Random;

import movement.map.MapNode;
import movement.map.SimMap;
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
	
	/** 记录节点的速度 */
	private double speed;
	/** 记录节点的持续时长 */
	private double duration;
	
	/** 状态0  设置持续时长的参数 */
	private static double DURATION_A_FOR_STATUS0 = 0.971101;
	private static double DURATION_PARA_FOR_STATUS0 = 0.00217593;
	
	/** 状态1 的持续时长参数*/
	private static double DURATION_A_FOR_STATUS1 = 0.988955;
	private static double DURATION_PARA_FOR_STATUS1 = 0.00103644;
		
	private static EventAwareRegions[] event_regions=null;
	
	public static final String TRANSITION_PROB_0 = "TransProbFile0";
	public static final String TRANSITION_PROB_1 = "TransProbFile1";
	public static final String CELLS_0 = "Cell0";
	public static final String CELLS_1 = "Cell1";

	/**
	 * @param settings
	 */
	public STARTMovement(Settings settings) {
		super(settings);
		// TODO Auto-generated constructor stub
		this.status = rng.nextInt(2);
		
		EventAwareRegions.map = getMap();
		initEventRegions(settings);
	}
	
	public static void initEventRegions(Settings settings)
	{
		if(event_regions!=null)return;
		System.out.println("初始化两个区域");
		
		event_regions = new EventAwareRegions[2];
		event_regions[0] = new EventAwareRegions(0,settings.getSetting(CELLS_0),
				settings.getSetting(TRANSITION_PROB_0));
		event_regions[1] = new EventAwareRegions(1,settings.getSetting(CELLS_1),
				settings.getSetting(TRANSITION_PROB_1));
	}

	/**
	 * @param mbm
	 */
	public STARTMovement(STARTMovement mbm) {
		super(mbm);
		// TODO Auto-generated constructor stub
	}
	
	private int reverseStatus(int status)
	{
		return status==1?0:1;
	}
	
	/**
	 * 在这里实现
	 * 1.找到目的节点
	 * 2.获取path
	 * 3.将path返回
	 */
	@Override
	public Path getPath() {
		this.speed = generateSpeed(this.status);
		Path p = new Path(speed);
		
		if(speed==0)
		{
			System.out.println("速度为0的情况");
			p.addWaypoint(this.lastMapNode.getLocation());
			this.status=this.status==0?1:0;//改变车辆状态。
			this.setTimer();
			return p;
		}
		//设置等待时间
		if(SimClock.getIntTime()<this.timer)
		{
			System.out.println("持续时间没用完的情况");
			p.addWaypoint(this.lastMapNode.getLocation());
			p.setSpeed(0);
			return p;
		}

		this.setTimer();
		Cell c = event_regions[this.status].fromMN2Cell(this.lastMapNode);
		MapNode to = event_regions[reverseStatus(this.status)].findMapNodeInDis(this.lastMapNode.getLocation(),
				c.region_id,
				this.speed*this.duration);
		List<MapNode> nodePath = getPathFinder().getShortestPath(lastMapNode, to);
		
		// this assertion should never fire if the map is checked in read phase
		assert nodePath.size() > 0 : "No path from " + lastMapNode + " to " +
			to + ". The simulation map isn't fully connected";
				
		for (MapNode node : nodePath) { // create a Path from the shortest path
			p.addWaypoint(node.getLocation());
		}
		
		lastMapNode = to;
		this.status=this.status==0?1:0;//改变车辆状态。
		return p;
	}	
	
	
	/**
	 * 初始化节点位置
	 * 在DTNHost中被调用
	 */
	@Override
	public Coord getInitialLocation() {

		MapNode node = this.event_regions[this.status].getInitMapNode();
		this.lastMapNode = node;
		return this.lastMapNode.getLocation();
	}
	
	private void setTimer() {
		this.duration = generateLastingTime(this.status);
		this.timer = SimClock.getIntTime()+(int)this.duration;
		
	}
	private double generateLastingTime(int status)
	{
		double seed = Math.random();
		if(status==0)
		{
			while(seed>cumulativeLastingTimeForStatus0(3600))
			{	
				seed = Math.random();
			}
			return generateLastingTimeForStatus0(seed);
		}
		else
		{
			while(seed>cumulativeLastingTimeForStatus1(3600))
			{	
				seed = Math.random();
			}
			return generateLastingTimeForStatus1(seed);
		}
	}
	private double generateLastingTimeForStatus1(double seed)
	{
		int maxLength = 3600;
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
		int maxLength = 3600;
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
	
	/**
	 * 生成速度
	 */
	protected double generateSpeed(double status)
	{
		// TODO get speed by the status
		if(status==0)
			return generateSpeedForStatus0();
		else
			return generateSpeedForStatus1();
			
	}

	private double generateSpeedForStatus0() {
		double  prob = Math.random();
		while(prob>cumulativeSpeedDistributionForStatus0(120))
		{
			prob = Math.random();
		}
		int speed = 0; 
		while(prob>cumulativeSpeedDistributionForStatus0(speed))
		{
			speed++;
		}

		return (double)speed/3.6;
	}
	
	private double generateSpeedForStatus1() {

		double  prob = Math.random();
		while(prob>cumulativeSpeedDistributionForStatus1(120))
		{
			prob = Math.random();
		}
		int speed = 0; 
		while(prob>cumulativeSpeedDistributionForStatus1(speed))
		{
			speed++;
		}

		return (double)speed/3.6;
	}
	
	private double cumulativeSpeedDistributionForStatus0(int v)
	{
		if(v==0)return 0.660763;
		if(v<=40) return 0.0059774*v+0.660763;
		if(v<=120) return 1.0-Math.exp(-0.0644895*v+0.383622);		
		return 1.0;
	}
	
	private double cumulativeSpeedDistributionForStatus1(int v)
	{
		if(v==0)return 0.217714;
		if(v<=40) return 0.0127845*v+0.217714;
		if(v<=120) return 1.0-Math.exp(-0.0642494*v+1.45314);
		return 1.0;
	}
}
