package movement;

import core.Coord;
import core.Settings;


public class TaxiMovementModel extends RandomWaypoint {
	//private static final int PATH_LENGTH = 10;
	private static double a_1 = 0.00792899;
	private static double b_1 = 0.534121;
	private static double c_1 = 0.0644977;
	private static double d_1 = 0.826339;
	
	
	//g(x) = 0.00792899*x+0.534121
	//f(x) = 1-exp( -0.0644977*x+0.826339)
	private Coord lastWaypoint;

	private static Regions regions=null;
	
	public TaxiMovementModel(TaxiMovementModel taximovement) {
		super(taximovement);
		// TODO Auto-generated constructor stub
		
		
	}
	

	public TaxiMovementModel(Settings settings) {
		super(settings);
		// initialization
		if(regions==null)
			init(settings);
		
	}


	private static void init(Settings settings) {
		System.out.println("New BeijingTaxiModel:--init regions...");
		regions = new Regions();
		
	}



	
	@Override
	public Coord getInitialLocation() {
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
		
		
		double speed = this.generateSpeed();
		p = new Path(speed);
		p.addWaypoint(lastWaypoint.clone());
		if(speed==0)
		{	
			p.addWaypoint(lastWaypoint);
			return p;
		}
		Coord  c_dest;
			
		c_dest = randomCoordBasedProb();
		
		
		Coord c_next = this.lastWaypoint;
		
		
		while(c_dest.distance(c_next)>regions.getGrid_x_length()){
		
			double x = c_dest.getX()- c_next.getX();
			double y = c_dest.getY()- c_next.getY();
			Coord c = move(c_next,x,y);
			c_next = c;
			p.addWaypoint(c_next);		
			
		}
		
		p.addWaypoint(c_dest);
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


	protected double generateSpeed() {
		double seed = 0;
		if (rng == null) {
			seed = 0;
		}
		else
			seed = rng.nextDouble();
		return generateSpeedForStatus(seed);
	}



	
	/*
	 * Generate speed
	 */
	
	private double generateSpeedForStatus(double seed) {
		for(int i=0;i<=180;i++)
		{
			if(seed<cumulativeSpeedDistributionForStatus(i))
				return i;
		}
		
		return 0;
	}
	

		
	private double cumulativeSpeedDistributionForStatus(int v)
	{
		if(v<0)return 0.0;
		if(v<=40) return a_1*v+b_1;
		if(v<=180) return 1.0-Math.exp(-c_1*v+d_1);		
		return 1.0;
	}
	
	
	@Override
	public TaxiMovementModel replicate() {
		return new TaxiMovementModel(this);
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
		if(x<0)x=0;
		if(y<0)y=0;
		if(x>24445)x=24445;
		if(y>23584)y=23584;
		Coord c = new Coord(x,y);
		return c;
	}
	
}
