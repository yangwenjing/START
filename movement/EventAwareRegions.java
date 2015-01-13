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
	
	public List<Integer>to_regions;
	public List<Integer>from_regions;
	
	
	//public Hashtable<Integer,List<Cell>> region_to_cells;
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
		MapNode mn = null;
		while(mn==null)
		{
			int result = rng.nextInt(200);
			Cell c = this.cells.get(result);
			List<Cell> initcells = new ArrayList<Cell>();
			initcells.add(c);
			mn =  getDestinationMapNode(c.region_id);
		}
		
		return mn;
		
		//List<MapNode> nodes = map.getNodes();
		//return nodes.get(rng.nextInt(nodes.size()));
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
		loadTransitionProb();
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
		if(valid_mapNodes.size()<=0)
		{
			return mapNodes;
		}
		return valid_mapNodes;
	}
	
	
	private MapNode getDestinationMapNode(int region_to)
	{
		
		List<MapNode> mapNodes = this.region2MapNode.get(region_to);
		//System.out.println("**mapNodes Size:"+mapNodes.size());
		//System.out.println("***to region:"+region_to);
		
		if(mapNodes==null||mapNodes.size()==0)
		{
			return null;
		}
		
		int index = rng.nextInt(mapNodes.size());
		return mapNodes.get(index);
	}
	
	private void loadRegion2MapNode() {
		this.region2MapNode = new Hashtable<Integer, List<MapNode>>();
		
		System.out.println("** size of Beijing2:"+map.getNodes().size());
		System.out.println("** region size:"+this.from_regions.size());
		// TODO 对region2MapNode初始化
		System.out.println("LoadRegions to MapNode");
		
		for(int i:this.from_regions)
		{
			List<MapNode> mnList = new ArrayList<MapNode>();
			this.region2MapNode.put(i, mnList);
			//System.out.println("From region id:"+ i);
		}
		
		int flag = 0;
		for(MapNode mn:map.getNodes())
		{
			Cell cell = fromMN2Cell(mn);
			if(!this.region2MapNode.containsKey(cell.region_id))
			{
				//System.out.println("XXXXX region_id:"+cell.region_id);
				flag++;
				continue;
			}
				
			this.region2MapNode.get(cell.region_id).add(mn);
			//System.out.println(String.format("regionId:%d--Size:%d", cell.region_id,this.region2MapNode.get(cell.region_id).size()));
		}
		
		System.out.println("Flag:--"+flag);
	}



	public Cell fromMN2Cell(MapNode mn) {
		Coord c = mn.getLocation();
		int x = (int) (c.getX()/grid_x_length);
		int y = (int) (c.getY()/grid_y_length);
		
		String key = getKey(x,y);
		//System.out.println("Key--"+key);
		if(!this.xy2Cell.containsKey(key))
		{
			Cell ci = new Cell(x,y,0,5000);
			this.cells.add(ci);
			this.xy2Cell.put(key, ci);
		}
		Cell cell = this.xy2Cell.get(key);
		return cell;
	}



	public static double getDistance(Cell c1, Cell c2)
	{
		double sum = Math.pow((c1.x-c2.x)*grid_x_length, 2)+Math.pow((c1.y-c2.y)*grid_y_length, 2);
		return Math.sqrt(sum);
	}
	
	
	public MapNode findMapNodeInDis(Coord coord, int region_from)
	{
		//System.out.println("**找到范围内的mapnode**");
		
		List<Cell>cells_temp = new LinkedList<Cell>();
		List<FromToProb> ftblist_temp = new LinkedList<FromToProb>();
		
		for(int to:this.to_regions)
		{
			String ft_key = getKey(region_from, to);
			if(!this.transition_prob.containsKey(ft_key))
				continue;
			FromToProb ftb = this.transition_prob.get(ft_key);
			if(!ftblist_temp.contains(ftb))
			{
				ftblist_temp.add(ftb);
			}
		}
		
		if(ftblist_temp.size()<=0)
		{
			return  getInitMapNode();
			
		}
		FromToProb ftb_selected = null;
		MapNode m = null;
		while(ftb_selected==null||m==null)
		{
			ftb_selected=randSelectARegion(ftblist_temp);
			m = getDestinationMapNode(ftb_selected.to);
		}

		return m;
	}

	private void newFromToProb(int from, int to) {
		String ft_key = getKey(from,to);
		if(!this.transition_prob.containsKey(ft_key))
		{
			FromToProb ftb_add = new FromToProb(from,to,0);
			this.transition_prob.put(ft_key, ftb_add);
		}
	}
	
	private double getSumFtbProb(List<FromToProb> ftblist_temp)
	{
		double ccdf=0;
		for(FromToProb f:ftblist_temp)
		{
			ccdf+=f.probability;
		}
		return ccdf;
	}



	private FromToProb randSelectARegion(List<FromToProb> ftblist_temp) {
		double cumulativeprob = 0;
		double ccdf = getSumFtbProb(ftblist_temp);
		double result = rng.nextDouble()*ccdf;
		
		if(ftblist_temp==null)return null;
		
		FromToProb ftb_selected=ftblist_temp.get(0);
		for(FromToProb ftb:ftblist_temp)
		{
			cumulativeprob+=ftb.probability;
			if(cumulativeprob>=result)
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
		
		//this.region_to_cells = new Hashtable<Integer,List<Cell>>();
		while(scanner.hasNextLine())
		{
			
			String nextLine = scanner.nextLine().trim();
		
			String s[] = nextLine.split("\t");
			if(s.length<4)continue;
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			if(i<10||j<10||i>90||j>90)
				continue;
			int num = Integer.parseInt(s[2]);
			int region_id = Integer.parseInt(s[3]);
			Cell c = new Cell(i-10,90-j,num,region_id);
			this.cells.add(c);
			this.sum_events+=num;
			String key = getKey(i-10,90-j);
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
		
		this.to_regions = new ArrayList<Integer>();
		this.from_regions = new ArrayList<Integer>();
		while(scanner.hasNextLine())
		{
			String nextLine = scanner.nextLine().trim();
			String s[] = nextLine.split("\t");
			int i = Integer.parseInt(s[1]);
			int j = Integer.parseInt(s[2]);
			if(!this.to_regions.contains(s[2]))
			{
				this.to_regions.add(j);
				
			}
			if(!this.from_regions.contains(i))
			{
				this.from_regions.add(i);
			}
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
