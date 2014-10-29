package movement;

import core.Coord;
import core.Settings;
import core.SimClock;

import java.util.Random;

public class BeijingTaxiMovementModel extends RandomWaypoint {
	//private static final int PATH_LENGTH = 10;
	private static double a_1 = 0.00629689;
	private static double b_1 = 0.644711;
	private static double c_1 = 0.0644895;
	private static double d_1 = 0.383622;
	
	private static double a_2 = 0.0127845;
	private static double b_2 = 0.217714;
	private static double c_2 = 0.0642494;
	private static double d_2 = 1.45314;
	
	private static double e_1 = 0.00149701;
	private static double e_2 = 0.000969622;
		
	private static int count=0;
	
	private boolean flag ;
	private int status;
	//To get a timer.
	private int timer;
	private Coord lastWaypoint;
	private Coord nextWaypoint;
	private static Regions regions=null;
	
	public BeijingTaxiMovementModel(BeijingTaxiMovementModel taximovement) {
		super(taximovement);
		// TODO Auto-generated constructor stub
		Random rng = new Random();
		if(rng.nextBoolean()==true)
			this.status = 1;
		else
			this.status = 0;
		
		setTimer();
		this.flag=false;
		
		
	}
	

	public BeijingTaxiMovementModel(Settings settings) {
		super(settings);
		// initialization
		if(regions==null)
			init(settings);
		
		//init original location
		Random rng = new Random();
		if(rng.nextBoolean()==true)
			this.status = 1;
		else
			this.status = 0;
		
		setTimer();
		
	}


	private static void init(Settings settings) {
		System.out.println("New BeijingTaxiModel:--init regions...");
		regions = new Regions();
		
	}


	private void setTimer() {
		this.timer = SimClock.getIntTime()+(int)generateLastingTime(this.status);
	}
	
	@Override
	public Coord getInitialLocation() {
		//System.out.println("initaliating location...");
		assert rng != null : "BeijingTaxiMovementModel not initialized!";
		Coord c = this.randomCoord();
		if(c.getX()>getMaxX()||c.getY()>getMaxY())
			System.out.println("Error:"+c.toString());
		this.lastWaypoint = c;
		//System.out.println(count++);
		return c;
	}
	
	
	
	public Path getPath() {
		Path p;
		if(this.timer<SimClock.getIntTime())
		{
			if(this.status==0)
				this.status=1;
			else
				this.status=0;
			
			setTimer();
		}
		
		double speed = this.generateSpeed(this.status);
		p = new Path(speed);
		p.addWaypoint(lastWaypoint.clone());
		if(speed==0)
		{	
			p.addWaypoint(lastWaypoint);
			return p;
		}
		Coord  c_dest;
		
		if(!this.flag)
		{
			c_dest = randomCoordBasedProb();
		}
		else
		{
			c_dest = this.nextWaypoint;
		}
		
		double distance = 0;
		
		Coord c_next = this.lastWaypoint;
		//System.out.println("#######################");
		//System.out.println("C_dest(x,y):"+c_dest.getX()+","+c_dest.getY());
		//TODO 循环，获得同一个相邻区域
		while(c_dest.distance(c_next)>regions.getGrid_x_length()&&distance<(double)speed*(this.timer-SimClock.getIntTime()))
		{
		
			double x = c_dest.getX()- c_next.getX();
			double y = c_dest.getY()- c_next.getY();
			Coord c = move(c_next,x,y);
			distance += c_next.distance(c);
			
			//if(c_next.distance(c)==0)
				//System.out.println("Error!");
			//System.out.println("c(x,y):"+c.getX()+","+c.getY()+":"+distance);
			c_next = c;
			p.addWaypoint(c_next);		
			
		}
		
		if(c_dest.distance(c_next)<regions.getGrid_x_length())		
		{
			p.addWaypoint(c_dest);
			this.flag=false;
			this.lastWaypoint = c_dest;
		}
		else
		{
			this.flag= true;
			this.nextWaypoint = c_dest;
			this.lastWaypoint = c_next;
		}
		
		//System.out.println("get a path!");
		return p;
		
		
	}
	public static int getOffsetDirect(double offset) {
		int flag=0;
		if(offset<0)
			flag=-1;
		else if(offset>0)
			flag=1;
		return flag;
	}
	private Coord move(Coord c_next, double x_offset, double y_offset) {
		
		return regions.move(c_next, x_offset,y_offset);
		
	}





	/*
	@Override

	public Path getPath() {
		Path p;
		if(this.timer<SimClock.getIntTime())
		{
			if(this.status==0)
				this.status=1;
			else
				this.status=0;
			
			setTimer();
		}
		
		double speed = this.generateSpeed(this.status);
		p = new Path(speed);
		p.addWaypoint(lastWaypoint.clone());
		double distance;
		
		if(flag)
		{
			distance = this.nextWaypoint.distance(this.lastWaypoint);
					
			double percent = getDistancePercent(speed, distance);
			if(percent!=1)
			{
				double x = (this.nextWaypoint.getX()-this.lastWaypoint.getX())*percent+this.lastWaypoint.getX();
				double y = (this.nextWaypoint.getY()-this.lastWaypoint.getY())*percent+this.lastWaypoint.getY();
				Coord c_dest = new Coord(x,y);
				p.addWaypoint(c_dest);
				this.lastWaypoint = c_dest;
				
			}
			else
			{				
				p.addWaypoint(this.nextWaypoint);
				this.lastWaypoint = this.nextWaypoint;
				this.flag=false;
			}
			
		}
		else
		{
		
			Coord  c_dest = randomCoordBasedProb();
			distance = c_dest.distance(this.lastWaypoint);
		
			// then calculated destination
			
			double percent = getDistancePercent(speed, distance);
			if(percent!=1)
			{
				this.flag=true;
				this.nextWaypoint = c_dest;
			}
			
			double x = (c_dest.getX()-this.lastWaypoint.getX())*percent+this.lastWaypoint.getX();
			double y = (c_dest.getY()-this.lastWaypoint.getY())*percent+this.lastWaypoint.getY();
			
			Coord c2 = new Coord(x,y);
			p.addWaypoint(c2);
			
			
			this.lastWaypoint = c2;
		}
		return p;
	}

	*/
	
	private double getDistancePercent(double speed, double distance) {
		double percent = 1;
		if(distance>(this.timer-SimClock.getIntTime())*speed)
		{
			flag = true;
			percent =(double)(this.timer-SimClock.getIntTime())*speed/distance;
		}
		return percent;
	}
	
	
	/*
	 * Generate speed
	 */
	
	protected double generateSpeed(double status)
	{
		// TODO get speed by the status
		Random rd1 = new Random();
		double seed =  rd1.nextDouble();
		if(status==0)
			return generateSpeedForStatus0(seed);
		else
			return generateSpeedForStatus1(seed);
			
	}

	private double generateSpeedForStatus0(double seed) {
		for(int i=0;i<=180;i++)
		{
			if(seed<cumulativeSpeedDistributionForStatus0(i))
				return i;
		}
		
		return 0;
	}
	
	private double generateSpeedForStatus1(double seed) {
		for(int i=0;i<=180;i++)
		{
			if(seed<cumulativeSpeedDistributionForStatus1(i))
				return i;
		}
		
		return 0;
	}
		
	private double cumulativeSpeedDistributionForStatus0(int v)
	{
		if(v<0)return 0.0;
		if(v<=40) return a_1*v+b_1;
		if(v<=180) return 1.0-Math.exp(-c_1*v+d_1);		
		return 1.0;
	}
	
	private double cumulativeSpeedDistributionForStatus1(int v)
	{
		if(v<0) return 0;
		if(v<=40) return a_2*v+b_2;
		if(v<=180) return 1.0-Math.exp(-c_2*v+d_2);
		return 1.0;
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
		return 1-Math.exp(-e_1*timeLength);
		
	}
	private double cumulativeLastingTimeForStatus1(int timeLength)
	{
		if(timeLength<0) return 0;
		return 1-Math.exp(-e_2*timeLength);
		
	}
	
	@Override
	public BeijingTaxiMovementModel replicate() {
		return new BeijingTaxiMovementModel(this);
	}
	
	
	private int getRegionIdBy(Coord c)
	{
		return this.regions.getAreaIdByCoord(c);
	}
	
	protected Coord randomCoord() {
		return regions.randomCoord();
	}
	
	protected Coord randomCoordBasedProb() {
		
		//System.out.println(this.getClass().toString()+":randomCoordBasedProb");
		
		int region_id = getRegionIdBy(this.lastWaypoint);
		
		Coord c =  regions.randomCoordBasedTrsProb(region_id);
		return formatCoordXY(c.getX(),c.getY());
		
	}
	private Coord formatCoordXY(double x, double y) {
		//24445,23584
		if(x<0)x=0;
		if(y<0)y=0;
		if(x>24445)x=24445;
		if(y>23584)y=23584;
		Coord c = new Coord(x,y);
		return c;
	}
	
}
