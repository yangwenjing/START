package movement.operation;

import core.Coord;
import core.Settings;
import movement.entity.ExtGrid;
import movement.entity.Scene;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 目的是由当前区域找到对应地图上的目的节点
 * TODO 从coord找到region
 * TODO 从to region中按概率选区
 * TODO 从to region中选取选取Coord
 *
 * Created by ywj on 15/5/7.
 */
public class RegionManager {
    private static RegionManager ourInstance = null;

    public static RegionManager getInstance(Settings settings) {
        if(ourInstance==null)
        {
            ourInstance = new RegionManager(settings);
        }
        return ourInstance;
    }

    public Scene scene = null;

    private RegionManager(Settings settings) {
        this.scene = Scene.getInstance(settings);
    }

    /**
     * 从coord找到region
     * @param coord
     * @return ExtGrid的id
     */
    public String fromCoordToGrid(Coord coord)
    {
        return scene.grids.get(ExtGrid.getKeyForGrid((int)coord.getX(),(int)coord.getY())).grid_id;
    }

    /**
     *  * TODO 从to region中按概率选区
     * @param time 时刻
     * @param gridKey 格子的key
     * @return Region Key
     */
    public String fromTimeEventGridToRegion(int time, int event, String gridKey)
    {
        String timeEventGridKey = Scene.getTimeEventGridKey(time, event,gridKey);
        return scene.timeGrid2Region.get(timeEventGridKey).region_key;
    }

    /**
     *  TODO 从to region中按概率选区
     */
    public String toRegion(int time, int event,String fromRegion_id, double prob) throws Exception {
        String timeEventKey = Scene.getTimeFromRegionKey(time, fromRegion_id);
        Hashtable<String,Double>toRegionProbs = this.scene.timeRegionTransProbs.get(timeEventKey);

        for(String key:toRegionProbs.keySet())
        {
            if(prob>toRegionProbs.get(key))
            {
                prob -= toRegionProbs.get(key);
            }
            else{
                return key;
            }
        }

        throw new Exception("No region select");
    }


}
