package movement.entity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by ywj on 15/5/7.
 */
public class ExtRegion {
    /**
     * 参数区
     * regionid
     */
    public String region_key;
    public int region_id;
    //一个region对应哪些格子
    public int event;
    public Hashtable<String,ExtGrid> grids;
    public List<Integer> times;


    public ExtRegion(int region_id,int event)
    {
        this.region_key = getRegionKey(region_id,event);
        this.region_id = region_id;
        this.event = event;
        this.grids = new Hashtable<String,ExtGrid>();
    }

    public static String getRegionKey(int region_id,int event)
    {
        return event+"-"+region_id;
    }
}
