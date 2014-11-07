package movement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import core.SettingsError;

public class EventAwareRegions {
	public static int event;
	
	/** 用于从cell到region的映射  */
	private Hashtable <String, Integer>cell_region;
	private Hashtable <String, Double>transition_prob;
	/**记录 cell中的事件数，用于初始位置的选择 */
	private List cells;
	
	private String Area_matrix_inputFileName;
	private String Transition_probability_inputFileName;
	
	private static final double area_left=0;
	private static final double area_right=1;
	private static final double area_top=0;
	private static final double area_bottom=1;
	private static final double grid_x_length=(area_right-area_left)/100.0;
	private static final double grid_y_length=(area_bottom-area_top)/100.0;
	
	public EventAwareRegions(int event){
		this.event = event;
		this.cell_region = new Hashtable<String, Integer>();
		this.transition_prob = new Hashtable<String, Double>();

	}
	
	private void loadCellRegionMap() {
		// TODO Auto-generated method stub
		File inFile = new File(Transition_probability_inputFileName);
		Scanner scanner;
		try {
			scanner = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			throw new SettingsError("Couldn't find external movement input " +
					"file " + inFile);
		}
		
		while(scanner.hasNextLine())
		{
			
			String nextLine = scanner.nextLine().trim();
			String s[] = nextLine.split(" ");
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			//int region_id = Double.parseDouble(s[3]);
			
			
		}
		System.out.println("fininsh loading transition prob...");
		scanner.close();
			
	}
	
	
	/**
	 * 从文件中读取区域转移概率矩阵
	 */
	private void loadTransitionProb() {
		// TODO Auto-generated method stub
		File inFile = new File(Transition_probability_inputFileName);
		Scanner scanner;
		try {
			scanner = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			throw new SettingsError("Couldn't find external movement input " +
					"file " + inFile);
		}
		System.out.println("Loading transition prob...");
		while(scanner.hasNextLine())
		{
			String nextLine = scanner.nextLine().trim();
			String s[] = nextLine.split(" ");
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			double p = Double.parseDouble(s[2]);
			String key = getKey(i,j);
			this.transition_prob.put(key, p);
		}
		System.out.println("fininsh loading transition prob...");
		scanner.close();
			
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
