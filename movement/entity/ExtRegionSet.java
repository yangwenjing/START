package movement.entity;

import java.util.Hashtable;

/**
 * Created by ywj on 15/5/7.
 */
public class ExtRegionSet {
    public int time;//对应时刻
    public Hashtable<Integer,ExtRegion> regions;
    public ExtRegionSet(int time){
        this.time = time;
        regions = new Hashtable<Integer,ExtRegion>();
    }
}
