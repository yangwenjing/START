package movement.entity;

import core.Coord;
import core.Settings;
import core.SettingsError;
import movement.Cell;
import movement.map.MapNode;
import movement.map.SimMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 一个单例模式
 * Created by ywj on 15/5/7.
 */
public class Scene {
    private static Scene ourInstance = null;

    public static Scene getInstance(Settings settings) {
        if (ourInstance == null) {
            return new Scene(settings);
        }
        return ourInstance;
    }

    /***************begion of 参数区*******************/
    /**
     * setttings parameters
     */
    public static String[][] Area_matrix_inputFileName;
    public static int beginTime;//开始时刻
    public static int timeLength;//仿真几小时
    public static List<List<Integer>> timeList = new ArrayList<List<Integer>>();
    public static SimMap map=null;

    /**
     * grid的x,y长度
     */
    public static double glen_x, glen_y;
    /**
     * grids x,y 方向的个数
     */
    public static int grids_x, grids_y;

    public Hashtable<String, ExtGrid> grids = null;
    public Hashtable<String, ExtRegion> regionPool = new Hashtable<String, ExtRegion>();
    public Hashtable<String, Hashtable<String, ExtRegion>> timeEventRegionSets = null;//建立time－region的关系
    public Hashtable<String, ExtRegion> timeGrid2Region = null;

    public Hashtable<String, List<MapNode>>region2MapNode = null;

    public Hashtable<String, Hashtable<String, Double>> timeRegionTransProbs = null;//记录区域转移概率矩阵之间的关系

    /***************end of 参数区*******************/
    /**
     * 初始化获取grid，region，regionset
     */
    private Scene(Settings settings) {
        //read settings
        settings.setNameSpace("XXX");
        //TODO 读取设置
        initGrid();

        initRegions();

        loadTransProb();
    }

    /**
     * 初始化区域转移矩阵
     */
    private void loadTransProb() {


    }

    /**
     * 初始化Grid
     */
    private void initGrid() {
        grids = new Hashtable<String, ExtGrid>();

        for (int i = 0; i < grids_x; i++) {
            for (int j = 0; j < grids_y; j++) {
                ExtGrid grid = new ExtGrid(i, j);
                grids.put(grid.grid_id, grid);
            }
        }
    }

    /*
     * 初始化区域
     */
    private void initRegions() {
        loadGrid2Region2RegionSet(0);
        loadGrid2Region2RegionSet(1);

    }


    /**
     * 对应区域id和mapnode
     */
    private void loadRegion2MapNode() {
        this.region2MapNode = new Hashtable<String , List<MapNode>>();

        System.out.println("** size of Beijing2:" + map.getNodes().size());
        System.out.println("LoadRegions to MapNode");


        for(MapNode mapNode:map.getNodes())
        {
            Coord coord = mapNode.getLocation();
            String grid_id = ExtGrid.getKeyForGrid((int)coord.getX(),(int)coord.getY());
            for(ExtRegion _region:this.regionPool.values())
            {
                if(_region.grids.contains(grid_id))
                {
                    if(!this.region2MapNode.contains(_region.region_key))
                    {
                        this.region2MapNode.put(_region.region_key,new ArrayList<MapNode>());
                    }
                    this.region2MapNode.get(_region.region_key).add(mapNode);
                }
            }
        }

    }


    public MapNode randomGetMapNode(){
        int len = map.getNodes().size();
        Random random = new Random();
        return map.getNodes().get(random.nextInt(len));
    }





    /**
     * 载入事件对应的区域集合
     *
     * @param event 0 1
     */
    private void loadGrid2Region2RegionSet(int event) {
        // TODO Auto-generated method stub
        //time gain size 24
        int fileIndex = 0;
        for (int i = 0; i < fileIndex; i++) {
            File inFile = new File(Area_matrix_inputFileName[event][i]);
            System.out.println("begin loading cells and region [" + Area_matrix_inputFileName[event][i] + "]...");

            Scanner scanner;
            try {
                scanner = new Scanner(inFile);
            } catch (FileNotFoundException e) {
                throw new SettingsError("Couldn't find external movement input " +
                        "file " + inFile);
            }

            Hashtable<String, ExtRegion> _regions = new Hashtable<String, ExtRegion>();

            while (scanner.hasNextLine()) {

                String nextLine = scanner.nextLine().trim();

                String s[] = nextLine.split("\t");
                if (s.length < 4) continue;
                int x = Integer.parseInt(s[0]);
                int y = Integer.parseInt(s[1]);

                int region_id = Integer.parseInt(s[3]);
                String gridKey = ExtGrid.getKeyForGrid(x, y);
                ExtGrid grid = this.grids.get(gridKey);

                ExtRegion region = this.getRegionFromPool(region_id, event);

                region.grids.put(gridKey, grid);
                _regions.put(ExtRegion.getRegionKey(region_id, event), region);

                /**
                 * 建立反向索引
                 * from time,grid to region
                 */
                for (int j = 0; j < (timeList.get(i)).size(); j++) {
                    String tgKey = getTimeEventGridKey(timeList.get(i).get(j), event, gridKey);
                    this.timeGrid2Region.put(tgKey, region);
                }

            }

            for (int j = 0; j < (timeList.get(i)).size(); j++) {
                int _time = timeList.get(i).get(j);
                String teKey = getTimeEventKey(_time, event);
                this.timeEventRegionSets.put(teKey, _regions);
            }

            //建立索引，从time，grid，到regionId


            System.out.println("fininsh loading cells and region...");
            scanner.close();

        }


    }

    /**
     * 从Region池中获取到region
     *
     * @param region_id
     * @param event
     * @return
     */
    private ExtRegion getRegionFromPool(int region_id, int event) {
        String region_key = ExtRegion.getRegionKey(region_id, event);
        if (!this.regionPool.contains(region_key)) {
            ExtRegion region = new ExtRegion(region_id, event);
            this.regionPool.put(region_key, region);
        }
        return this.regionPool.get(region_key);
    }


    /**
     * 获取从time event构成的索引
     *
     * @param time  时刻
     * @param event 事件 0 1
     * @return 索引
     */
    public static String getTimeEventKey(int time, int event) {
        return time + "-" + event;
    }


    /**
     * 获取从时间 格子到区域的索引
     *
     * @param time    时间
     * @param gridKey 格子key
     * @return 索引
     */
    public static String getTimeEventGridKey(int time, int event, String gridKey) {
        return time + "-" + event + "-" + gridKey;
    }


    public static String getTimeFromRegionKey(int time, String region_f) {
        return time + "-" + region_f;
    }
}
