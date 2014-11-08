package movement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import movement.map.MapNode;
import core.SettingsError;

public class EventAwareRegions {
	public static int event;
	
	/** 用于从坐标 x,y 到cell的映射  */
	private Hashtable <String, Cell>xy2Cell;
	
	/**区域转移概率矩阵 */
	private Hashtable <String, FromToProb>transition_prob;//String 为 from_to的组合
	
	
	/**记录 cell中的事件数，用于初始位置的选择 */
	private List cells;
	
	public String Area_matrix_inputFileName;
	public String Transition_probability_inputFileName;
	public static Random rng = new Random();
	
	/** 
	 * 处理地图点
	 * 将地图点与region对应 
	 * 一个regionid 对应多个mapid
	 * 得到regionid就可以找到一个map node
	 */
	public Hashtable<Integer,List<MapNode>> region2MapNode= null;
	
	private static final double area_left=0;
	private static final double area_right=1;
	private static final double area_top=0;
	private static final double area_bottom=1;
	private static final double grid_x_length=(area_right-area_left)/100.0;
	private static final double grid_y_length=(area_bottom-area_top)/100.0;
	
	public MapNode getMapNodeByRegion(int region_id)
	{
		List<MapNode>nodes = this.region2MapNode.get(region_id);
		int index = rng.nextInt(nodes.size());
		return nodes.get(index);
				
	}
	
	public EventAwareRegions(int event){
		this.event = event;
		this.xy2Cell = new Hashtable<String, Cell>();
		this.transition_prob = new Hashtable<String, FromToProb>();
		this.cells = new ArrayList<Cell>();
		

	}
	
	private void loadCells() {
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
			int num = Integer.parseInt(s[2]);
			int region_id = Integer.parseInt(s[3]);
			Cell c = new Cell(i,j,num,region_id);
			cells.add(c);
			
			String key = getKey(i, j);
			this.xy2Cell.put(key, c);
			
			
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
			FromToProb ftp = new FromToProb(i,j,p);
			this.transition_prob.put(key, ftp);
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
		
		
		return this.xy2Cell.get(key).region_id;
	}
	
	public double getTransProb(String from, String to)
	{
		String key = getKey(from,to);
		return transition_prob.get(key).probability;
	}
	
	
	

}
