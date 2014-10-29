package movement;
import java.util.Hashtable;
import java.util.Random;

import core.Coord;



public class Region {
	private int region_id;
	
	public int getRegion_id() {
		return region_id;
	}

	private Hashtable ht_grids; 
	
	public Hashtable getGrids()
	{
		return this.ht_grids;
	}
	
	public Region(int id)
	{
		this.region_id=id;
		this.ht_grids = new Hashtable<Integer,Grid>();
	}
	
	
	public void addGridToRegion(Grid g)
	{
		this.ht_grids.put(g.hashCode(), g);
	}
	
	public boolean isContainGrids(Grid g)
	{
		return this.ht_grids.containsKey(g.hashCode());
	}
	
	public boolean isContainGrids(int key)
	{
		return this.ht_grids.containsKey(key);
	}
	
	private Grid randomGetGrid() {
		Random rd1 = new Random();
		if(this.ht_grids.isEmpty()) return null;
		
		
		Object[] keys =  this.ht_grids.keySet().toArray();
		int key = Integer.parseInt(keys[rd1.nextInt(keys.length)].toString());
		return (Grid)this.ht_grids.get(key);
	}

	public Coord randomGetACoord(double grid_x_length, double grid_y_length) {
		// TODO Auto-generated method stub
		Grid g = randomGetGrid();
		if(g==null)return null;
		
		double x = (double)(g.getX())*grid_x_length;
		double y = (double)(g.getY())*grid_y_length;
		
		Random rng = new Random();
		x=x+rng.nextDouble()*grid_x_length;
		y=y+rng.nextDouble()*grid_y_length;
		
		return formatCoordXY(x, y, g);
		
		
	}

	private Coord formatCoordXY(double x, double y,
			Grid g) {
		
		if(x<0)x=0;
		if(y<0)y=0;
		if(x>24445)x=24444;
		if(y>23584)y=23583;
		Coord c = new Coord(x,y);
		return c;
	}

}
