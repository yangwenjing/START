package movement;

import input.ExternalMovementReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import core.Coord;
import core.Settings;
import core.SettingsError;

public class Regions {
	private Hashtable ht_regions;
	private double[][] transition_prob;
	private double[][] cumulative_transition_prob;
	private int[][] area2Region_index;
	private int region_sum;
	private String Area_matrix_inputFileName;
	private String Transition_probability_inputFileName;
	private double area_left;
	private double area_right;
	private double area_top;
	private double area_bottom;
	private double grid_x_length;
	
	public double getGrid_x_length() {
		return grid_x_length;
	}

	public double getGrid_y_length() {
		return grid_y_length;
	}

	private double grid_y_length;
	
	public static final String TRANSITION_PROB = "Transition_probability";
	public static final String AREA_MATRIX = "Area_matrix";
	public static final String REGIONS_NS = "Regions";
	
	
	
	public Regions()
	{
		
		this.ht_regions = new Hashtable<Integer,Region>();
		
		this.area2Region_index = new int[80][80];
		for(int i=0;i<80;i++)
		{
			for(int j=0;j<80;j++)
			{
				this.area2Region_index[i][j]=0;
			}
		}
		initRegions();
		
	}
	
	private void initCumulativeP()
	{
		for(int i=0;i<this.transition_prob.length;i++)
		{
			this.cumulative_transition_prob[i][0] = this.transition_prob[i][0];
			for(int j=1;j<this.transition_prob[i].length;j++)
			{
				this.cumulative_transition_prob[i][j] = this.cumulative_transition_prob[i][j-1]+this.transition_prob[i][j];
			}
		}
	}
	
	public void initRegions()
	{
		Settings settings = new Settings(REGIONS_NS);
		
		Area_matrix_inputFileName = settings.getSetting(AREA_MATRIX);
		Transition_probability_inputFileName = settings.getSetting(TRANSITION_PROB);
		
		loadAreaMatrix();
		loadTransitionProb();
		initCumulativeP();
		
	}
	public static int getOffsetDirect(double offset) {
		int flag=0;
		if(offset<0)
			flag=-1;
		else if(offset>0)
			flag=1;
		return flag;
	}
	
	public Coord moveXY(Coord c, double x_offset, double y_offset)
	{
		double x_ = Math.abs(x_offset)<this.grid_x_length? x_offset:(this.grid_x_length*getOffsetDirect(x_offset));
		double y_ = Math.abs(y_offset)<this.grid_y_length? y_offset:(this.grid_y_length*getOffsetDirect(y_offset));
		
		c.translate(x_, y_);
		return c;
		
	}
	public double getMoveProb(Coord f, Coord t)
	{
		int f_areaId = this.getAreaIdByCoord(f);
		int t_areaId = this.getAreaIdByCoord(t);
		return this.transition_prob[f_areaId][t_areaId];
		
	}
	
	public double getMoveProb(double f_x, double f_y, double t_x, double t_y)
	{
		int f_areaId = this.getAreaIdByXY(f_x, f_y);
		int t_areaId = this.getAreaIdByXY(t_x, t_y);
		return this.transition_prob[f_areaId][t_areaId];
	}
	
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
		
		this.region_sum = Integer.parseInt(scanner.nextLine().trim());
		
		
		this.transition_prob = new double[region_sum][];
		this.cumulative_transition_prob = new double [region_sum][];
		for(int i=0;i<region_sum;i++)
		{
			this.transition_prob[i] = new double[region_sum];	
			this.cumulative_transition_prob[i]=new double[region_sum];
		}
		System.out.println("Loading transition prob...");
		while(scanner.hasNextLine())
		{
			
			String nextLine = scanner.nextLine().trim();
			String s[] = nextLine.split(" ");
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			double p = Double.parseDouble(s[2]);
			this.transition_prob[i][j] = p;
		}
		System.out.println("fininsh loading transition prob...");
		scanner.close();
			
	}
	public int getAreaIdByCoord(Coord c)
	{
		
		return getAreaIdByXY(c.getX(),c.getY());
		
	}
	
	
	public int getAreaIdByXY(double x, double y)
	{
		int i = (int)Math.floor(x/this.grid_x_length);
		int j = (int)Math.floor(y/this.grid_y_length);
		if(i>100||j>100)return 0;
		return this.area2Region_index[i][j];
	}
	
	private void loadAreaMatrix() {
		// TODO Auto-generated method stub
		File inFile = new File(Area_matrix_inputFileName);
		Scanner scanner;
		try {
			scanner = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			throw new SettingsError("Couldn't find beijingtaxi movement input " +
					"file " + inFile);
		}
		String nextLine = scanner.nextLine().trim();
		String []s = nextLine.split(" ");
		this.area_left = Double.parseDouble(s[0]);
		this.area_right = Double.parseDouble(s[1]);
		this.area_bottom = Double.parseDouble(s[2]);
		this.area_top = Double.parseDouble(s[3]);
		this.grid_x_length = Math.abs(area_left-area_right)/100.0;
		this.grid_y_length = Math.abs(area_top-area_bottom)/100.0;
		System.out.println(this.grid_x_length);
	
		System.out.println("Loading Areas...");
		while (scanner.hasNextLine())
		{
			
			nextLine = scanner.nextLine().trim();
			s = nextLine.split("\t");
			int i = Integer.parseInt(s[0]);
			int j = Integer.parseInt(s[1]);
			int areaId = Integer.parseInt(s[2]);
			this.area2Region_index[i][j] = areaId;
			initRegionHT(i,j,areaId);
			
		}
		System.out.println("Finish Loading Areas...");
		
		scanner.close();
	}
	private boolean gridInArea(int i,int j)
	{
		return true;
	}
	private void initRegionHT(int i, int j, int areaId) {
		// TODO Auto-generated method stub
		boolean valid_grid = gridInArea(i,j);
		
		if(valid_grid)
		{
			addRegion(areaId);
			Region g = (Region)this.ht_regions.get(areaId);		
			g.addGridToRegion(new Grid(i,j));
		}
	}

	public void addRegion(int areaId)
	{
		
		if(!this.ht_regions.containsKey(areaId))
		{
			Region r = new Region(areaId);
			this.ht_regions.put(areaId, r);
		}
	}
	
	public Region randomGetRegion()
	{
		Random rng = new Random();
		Object [] arr_tmp = this.ht_regions.keySet().toArray();
		int key = Integer.parseInt(arr_tmp[rng.nextInt(arr_tmp.length)].toString());
		
		return (Region)this.ht_regions.get(key);
	}
	
	public Region randomGetRegionBased(int region_id)
	{
		Random rng = new Random();
		double prob = rng.nextDouble();
		int to_region = -1;
		for(int i=0;i<this.cumulative_transition_prob[region_id].length;i++)
		{
			if(prob<this.cumulative_transition_prob[region_id][i]&&this.ht_regions.containsKey(i))
			{	
				to_region = i;
				break;
			}
		}
		
		if(to_region==-1)return randomGetRegion();
		
		else return (Region)this.ht_regions.get(to_region);
		
		
	}

	public Coord randomCoordBasedTrsProb(int region_id) {
		
		Region rg = randomGetRegionBased(region_id);
				
		return rg.randomGetACoord(this.grid_x_length,this.grid_y_length);
	}

	public Coord move(Coord c_next, double x_offset, double y_offset) {
		//System.out.println("grid_x"+this.grid_x_length);
		//System.out.println("grid_y:"+this.grid_y_length);
		Coord c_t = new Coord(c_next.getX(),c_next.getY());
		boolean x_flag = false;
		boolean y_flag = false;
		if(Math.abs(x_offset)<=this.grid_x_length)
		{
			//System.out.println("err1");
			c_t.translate(x_offset,0);
			x_flag = true;
		}
		if(Math.abs(y_offset)<=this.grid_y_length)
		{
			//System.out.println("err2");
			c_t.translate(0,y_offset);
			y_flag = true;
		}
		if(x_flag == true&&y_flag==false)
		{
			//System.out.println("err3");
			c_t.translate(0,this.grid_y_length*getOffsetDirect(y_offset));
		}
		else if(x_flag == false&&y_flag==true)
		{
			//System.out.println("err4");
			c_t.translate(this.grid_x_length*getOffsetDirect(x_offset),0);
		}
		else if(x_flag == false&&y_flag==false)
		{
			double p1 = this.getMoveProb(c_t.getX(), c_t.getY(), c_t.getX()+this.grid_x_length*getOffsetDirect(x_offset), c_t.getY());
			double p2 = this.getMoveProb(c_t.getX(), c_t.getY(), c_t.getX(), c_t.getY()+this.grid_y_length*getOffsetDirect(y_offset));
			double p3 = this.getMoveProb(c_t.getX(), c_t.getY(), c_t.getX()+this.grid_x_length*getOffsetDirect(x_offset), c_t.getY()+this.grid_y_length*getOffsetDirect(y_offset));
			//System.out.println("p1:"+p1);
			//System.out.println("p2:"+p2);
			//System.out.println("p3:"+p3);
			
			double p0 = 0;
			if(p0<=p1)
				p0=p1;
			if(p0<p2)
				p0=p2;
			if(p0<p3)
				p0=p3;
			
			if(p0==p1)
			{
				c_t.translate(this.grid_x_length*getOffsetDirect(x_offset),0);
			}
			else if (p0==p2)
				c_t.translate(0,this.grid_y_length*getOffsetDirect(y_offset));
			else
			{
				c_t.translate(this.grid_x_length*getOffsetDirect(x_offset),this.grid_y_length*getOffsetDirect(y_offset));
			}
				
		}
		if(c_t.distance(c_next)==0)
			System.out.println("Error:"+x_offset+","+y_offset);
		
		return c_t;
	}

	public Coord randomCoord() {
		Region rg = this.randomGetRegion();
		return rg.randomGetACoord(grid_x_length, grid_y_length);
	}
	

}
