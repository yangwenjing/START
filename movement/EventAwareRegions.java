package movement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import movement.map.MapNode;
import movement.map.SimMap;
import core.Coord;
import core.SettingsError;

public class EventAwareRegions {
	public static int event;
	/** 需要获得初始化的 map */
	public static SimMap map=null;
	
	/** 用于从坐标 x,y 到cell的映射  */
	private Hashtable <String, Cell>xy2Cell;
	
	/**区域转移概率矩阵 */
	private Hashtable <String, FromToProb>transition_prob;//String 为 from_to的组合
	
	
	/**记录 cell中的事件数，用于初始位置的选择 */
	private List<Cell> cells;
	private int sum_events;
	public String Area_matrix_inputFileName;
	public String Transition_probability_inputFileName;
	public static Random rng = new Random();
	
	/** 
	 * 处理地图点
	 * 将地图点与region对应 
	 * 一个regionid 对应多个mapid
	 * 得到regionid就可以找到一个map node
	 * 
	 * key: region_id;
	 * 
	 */
	public Hashtable<Integer,List<MapNode>> region2MapNode= null;
	
	private static final double area_left=0;
	private static final double area_right=30557.1976136548;
	private static final double area_top=0;
	private static final double area_bottom=29480.147425641;
	private static final double grid_x_length=(area_right-area_left)/100.0;
	private static final double grid_y_length=(area_bottom-area_top)/100.0;
	
	private MapNode getMapNodeByRegion(int region_id)
	{
		List<MapNode>nodes = this.region2MapNode.get(region_id);
		int index = rng.nextInt(nodes.size());
		return nodes.get(index);				
	}
	
	public MapNode getInitMapNode()
	{
		
		double result = Math.random();
		double prob = 0;
		for(Cell c:this.cells)
		{
			prob += (double)c.num/(double)this.sum_events; 
			if(result<prob)
			{
				List<Cell> initcells = new ArrayList<Cell>(1);
				initcells.add(c);
				return getDestinationMapNode(c.region_id,initcells);
			}
		}
		List<MapNode> nodes = map.getNodes();
		return nodes.get(rng.nextInt(nodes.size()));
	}
	
	
	public EventAwareRegions(int event, String cellsFile, String transFile){
		this.event = event;
		this.xy2Cell = new Hashtable<String, Cell>();
		this.transition_prob = new Hashtable<String, FromToProb>();
		this.cells = new ArrayList<Cell>();
		this.sum_events = 0;
		this.Area_matrix_inputFileName = cellsFile;
		this.Transition_probability_inputFileName = transFile;
		loadCells();
		loadRegion2MapNode();
		
		
		
	}
	
	public List<MapNode> mapNodes_in(List<MapNode> mapNodes,List<Cell>cells_in)
	{
		List<MapNode> valid_mapNodes = new ArrayList<MapNode>();
		for(MapNode mn:mapNodes)
		{
			Cell cell = fromMN2Cell(mn);
			if(cells_in.contains(cell))
				valid_mapNodes.add(mn);
		}
		return valid_mapNodes;
	}
	
	
	private MapNode getDestinationMapNode(int region_to, List<Cell>cells_in)
	{

		List<MapNode> mapNodes = this.region2MapNode.get(region_to);
		List<MapNode> valid_mapNodes = mapNodes_in(mapNodes,cells_in);
		
		int index = rng.nextInt(valid_mapNodes.size()-1);
		return valid_mapNodes.get(index);
	}
	
	
	
	private void loadRegion2MapNode() {
		this.region2MapNode = new Hashtable<Integer, List<MapNode>>();
		// TODO 对region2MapNode初始化
		System.out.println("LoadRegions to MapNode");
		for(MapNode mn:map.getNodes())
		{
			Cell cell = fromMN2Cell(mn);
			if(!this.region2MapNode.contains(cell.region_id))
			{
				List<MapNode> mnList = new ArrayList<MapNode>();
				this.region2MapNode.put(cell.region_id, mnList);
			}
				
			this.region2MapNode.get(cell.region_id).add(mn);
			
		}
	}



	public Cell fromMN2Cell(MapNode mn) {
		Coord c = mn.getLocation();
		int x = (int) (c.getX()/grid_x_length);
		int y = (int) (c.getY()/grid_y_length);
		
		String key = getKey(x,y);
		Cell cell = this.xy2Cell.get(key);
		return cell;
	}



	public static double getDistance(Cell c1, Cell c2)
	{
		double sum = Math.pow((c1.x-c2.x)*grid_x_length, 2)+Math.pow((c1.y-c2.y)*grid_y_length, 2);
		return Math.sqrt(sum);
	}
	
	
	public MapNode findMapNodeInDis(Coord coord, int region_from, double distance)
	{
		String ckey = getKey((int)(coord.getX()/grid_x_length),(int)(coord.getY()/grid_y_length));
		Cell c = this.xy2Cell.get(ckey);
		int x_tix = (int)(distance/grid_x_length);
		int y_tix = (int) (distance/grid_y_length);
		int x_min = (c.x-x_tix)>=0?(c.x-x_tix):0;
		int x_max = (c.x+x_tix)<=79?(c.x+x_tix):79;
		int y_min = (c.y-y_tix)>=0?(c.y-y_tix):0;
		int y_max = (c.y+y_tix)<=79?(c.y+y_tix):79;
		
		
		List<Cell>cells_temp = new LinkedList<Cell>();
		List<FromToProb> ftblist_temp = new LinkedList<FromToProb>();
		
		int x=x_min;
		int y=y_min;
		while(x<=x_max)
		{
			while(y<=y_max)
			{
				Cell cell2 = this.xy2Cell.get(getKey(x,y));
				if(getDistance(c,cell2)>distance)
					continue;
				cells_temp.add(cell2);
				String ft_key = getKey(region_from, cell2.region_id);
				FromToProb ftb = this.transition_prob.get(ft_key);
				if(ftblist_temp.contains(ftb))
					continue;
				ftblist_temp.add(ftb);
				y++;
			}
			x++;
		}
		
		Collections.sort(ftblist_temp);
		FromToProb ftb_selected=null; 
		while(ftb_selected==null)
		{
			ftb_selected=randSelectARegion(ftblist_temp);
		}
		
		
		return getDestinationMapNode(ftb_selected.to, cells_temp);
	}



	private FromToProb randSelectARegion(List<FromToProb> ftblist_temp) {
		double cumulativeprob = 0;
		double result = rng.nextDouble();
		FromToProb ftb_selected=null;
		for(FromToProb ftb:ftblist_temp)
		{
			cumulativeprob+=ftb.probability;
			if(cumulativeprob>result)
			{
				ftb_selected = ftb;
				break;
			}
		}
		
		return ftb_selected;
	}
	
	private void loadCells() {
		// TODO Auto-generated method stub
		File inFile = new File(Area_matrix_inputFileName);
		System.out.println("begin loading cells and region...");
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
		
			String s[] = nextLine.split("\t");
			if(s.length<4)continue;
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			if(i<10||j<10||i>=90||j>=90)
				continue;
			int num = Integer.parseInt(s[2]);
			int region_id = Integer.parseInt(s[3]);
			Cell c = new Cell(i-10,j-10,num,region_id);
			this.cells.add(c);
			this.sum_events+=num;
			String key = getKey(i-10, j-10);
			this.xy2Cell.put(key, c);
			
			
		}
		System.out.println("fininsh loading cells and region...");
		scanner.close();
		Collections.sort(this.cells);	
	}
	
	/**
	 * 从文件中读取区域转移概率矩阵
	 */
	private void loadTransitionProb() {
		// TODO Auto-generated method stub
		File inFile = new File(Transition_probability_inputFileName);
		System.out.println("begin loading transition prob...");
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
			String s[] = nextLine.split("\t");
			int i = Integer.parseInt(s[1]);
			int j = Integer.parseInt(s[2]);
			double p = Double.parseDouble(s[3]);
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
		int y = (int)((lat-area_top)/grid_y_length);
		String key = getKey(x,y);
		
		
		return this.xy2Cell.get(key).region_id;
	}
	
	public double getTransProb(String from, String to)
	{
		String key = getKey(from,to);
		return transition_prob.get(key).probability;
	}
	
	
	

}
