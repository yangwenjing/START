package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.operation.RegionManager;
import movement.operation.SpeedManager;

import java.util.List;

/**
 * Created by ywj on 15/5/5.
 * <p>
 * 扩展了STARTMovement，添加了时间字段，用来表示仿真是从什么时刻开始的
 */
public class ExSTARTMovement extends MapBasedMovement implements SwitchableMovement {

    /**
     * 区分车辆状态
     */
    private int status;

    /**
     * 判断是否超过持续时长
     */
    //private int timer;

    /**
     * 记录节点的速度
     */
    private double speed;
    /**
     * 记录节点的持续时长
     */
    private double duration;
    /**
     * 引入仿真开始时刻
     */
    private int beginTime;

    /** 其他参数和配置不变   */
    /**
     * 状态0  设置持续时长的参数
     */
    private static double DURATION_A_FOR_STATUS0 = 0.971101;
    private static double DURATION_PARA_FOR_STATUS0 = 0.00217593;

    /**
     * 状态1 的持续时长参数
     */
    private static double DURATION_A_FOR_STATUS1 = 0.988955;
    private static double DURATION_PARA_FOR_STATUS1 = 0.00103644;


    public static final String TRANSITION_PROB_0 = "TransProbFile0";
    public static final String TRANSITION_PROB_1 = "TransProbFile1";
    public static final String CELLS_0 = "Cell0";
    public static final String CELLS_1 = "Cell1";
    /**
     * a=0.11798
     * b=0.0058637
     */
    public static double A0 = 0.11798;
    public static double A1 = 0.0058637;

    public static RegionManager regionManager = null;
    public static SpeedManager speedManager = null;

    private DijkstraPathFinder pathFinder;

    public DijkstraPathFinder getPathFinder() {
        return this.pathFinder;
    }


    public ExSTARTMovement(Settings settings) {
        super(settings);

        if(regionManager==null)
            regionManager = RegionManager.getInstance(settings);
        if(speedManager==null)
            speedManager = SpeedManager.getInstance(settings);
    }

    @Override
    public Path getPath() {

        //每次getPath就转换状态
        reverseStatus();
//
//        this.speed = generateSpeed(this.status);
//        Path p = new Path(speed);
        Path p = new Path();

        MapNode to = getNextMapNode();
        List<MapNode> nodePath = getPathFinder().getShortestPath(this.lastMapNode, to);

        // this assertion should never fire if the map is checked in read phase
        assert nodePath.size() > 0 : "No path from " + this.lastMapNode + " to " +
                to + ". The simulation map isn't fully connected";

        double dis=0;
        MapNode source = this.lastMapNode;
        for (MapNode node : nodePath) { // create a Path from the shortest path
            dis+=distance(source.getLocation(),node.getLocation());//计算实际距离
            p.addWaypoint(node.getLocation());
        }

        double minSpeed = dis/3600;//最多跑一个小时

        //在此处设置速度
        this.speed = minSpeed+speedManager.generateSpeed(this.status);
        p.setSpeed(this.speed);

        //纪律目的节点的位置
        lastMapNode = to;
        return p;
    }




    private static double distance(Coord location, Coord location2) {
        // TODO Auto-generated method stub
        double x = Math.pow(location.getX()-location2.getY(), 2);
        double y = Math.pow(location.getY()-location2.getY(), 2);
        return Math.sqrt(x+y);
    }

    public void reverseStatus()
    {
        this.status = this.status==0?1:0;
    }

    public MapNode getNextMapNode()
    {

        int _time = this.beginTime+ (int) Math.floor(SimClock.getIntTime()/3600);
        return regionManager.fromCoordToNextMapNode(_time,this.status,this.lastMapNode.getLocation());
    }


}
