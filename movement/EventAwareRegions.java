package movement;

import java.util.Hashtable;

public class EventAwareRegions {
	public static int event;
	
	
	private Hashtable cell_region;
	private Hashtable transition_prob;
	
	
	private String Area_matrix_inputFileName;
	private String Transition_probability_inputFileName;
	
	private static double area_left;
	private static double area_right;
	private static double area_top;
	private static double area_bottom;
	private static double grid_x_length;
	private static double grid_y_length;
	
	public EventAwareRegions(int event){
		this.event = event;

	}
	
	private static String getKey(int x, int y)
	{
		return String.format("%d_%d", x,y);
	}
	
	private static String getKey(String from, String to)
	{
		return String.format("%s_%s", from, to);
	}
	
	public int getRegionIdByLocation(double lon,double lat)
	{
		int x = (int)((lon-area_left)/grid_x_length);
		int y = (int)((lat-area_right)/grid_y_length);
		String key = getKey(x,y);
		
		
		return (int)this.cell_region.get(key);
	}
	
	public double getTransProb(String from, String to)
	{
		String key = getKey(from,to);
		return (double)transition_prob.get(key);
	}
	

}
