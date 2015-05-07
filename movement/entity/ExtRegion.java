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
    public int region_id;
    //一个region对应哪些格子
    public Hashtable<String,ExtGrid> grids;
    public List<Integer> times;

    public ExtRegion(int region_id)
    {
        this.region_id = region_id;
        this.grids = new Hashtable<String,ExtGrid>();
    }
}
