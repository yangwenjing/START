/**
 * 
 */
package movement;

/**
 * @author yangwenjing
 *
 */
public class Cell implements Comparable<Cell>{
	public int x;
	public int y;
	public int num;
	public int region_id;
	public static int threshold;
	@Override
	public int compareTo(Cell arg0) {
		return this.num>=arg0.num?-1:1;
	}

}
