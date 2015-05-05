package movement;

import core.Settings;
import movement.map.DijkstraPathFinder;

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
    private int timer;

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

    private static EventAwareRegions[] event_regions = null;

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

    private DijkstraPathFinder pathFinder;

    public DijkstraPathFinder getPathFinder() {
        return this.pathFinder;
    }


    public ExSTARTMovement(Settings settings) {
        super(settings);
    }

}
