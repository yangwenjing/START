package movement.entity;

import core.Settings;
import core.SettingsError;
import gui.InfoPanel;
import movement.Cell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * 一个单例模式
 * Created by ywj on 15/5/7.
 */
public class Scene {
    private static Scene ourInstance = null;

    public static Scene getInstance(Settings settings) {
        if(ourInstance==null)
        {
            return new Scene(settings);
        }
        return ourInstance;
    }

    /***************begion of 参数区*******************/
    /**
     * setttings parameters
     *
     */
    public static String[] Area_matrix_inputFileName;
    public static int beginTime;//开始时刻
    public static int timeLength;//仿真几小时

    /**
     * grid的x,y长度
     */
    public static double glen_x,glen_y;
    /**
     * grids x,y 方向的个数
     */
    public static int grids_x,grids_y;

    public Hashtable<String,ExtGrid> grids=null;
    public Hashtable<Integer,ExtRegion> regionPool = new Hashtable<Integer,ExtRegion>();
    public Hashtable<Integer,Hashtable<Integer,ExtRegion>>regionSets = null;//建立time－region的关系

    /***************end of 参数区*******************/
    /**
     *初始化获取grid，region，regionset
     */
    private Scene(Settings settings) {
        //read settings
        settings.setNameSpace("XXX");
        //TODO 读取设置
        initGrid();
    }

    /**
     * 初始化Grid
     */
    private void initGrid()
    {
        grids = new Hashtable<String,ExtGrid>();

        for(int i=0;i<grids_x;i++)
        {
            for(int j=0;j<grids_y;j++)
            {
                ExtGrid grid = new ExtGrid(i,j);
                grids.put(grid.grid_id,grid);
            }
        }
    }
    /*
     * 初始化区域
     */
    private void initRegions()
    {

    }


    private void loadGrid2Region2RegionSet() {
          // TODO Auto-generated method stub
        //time gain size 24
        int fileIndex = 0;
        for(int i=0;i<fileIndex;i++)
        {
            File inFile = new File(Area_matrix_inputFileName[i]);
            System.out.println("begin loading cells and region ["+Area_matrix_inputFileName[i]+"]...");

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
                int x = Integer.parseInt(s[0]);
                int y = Integer.parseInt(s[1]);

//                int num = Integer.parseInt(s[2]);
                int region_id = Integer.parseInt(s[3]);

                String gridKey = ExtGrid.getKeyForGrid(x, y);
                ExtGrid grid = this.grids.get(gridKey);

                ExtRegion region = this.getRegionFromPool(region_id);
                region.grids.put(gridKey,grid);








            }
            System.out.println("fininsh loading cells and region...");
            scanner.close();
            Collections.sort(this.cells);

        }






    }

    private ExtRegion getRegionFromPool(int region_id)
    {
        if(!this.regionPool.contains(region_id))
        {
            ExtRegion region = new ExtRegion(region_id);
            this.regionPool.put(region_id,region);
        }
        return this.regionPool.get(region_id);
    }

}
