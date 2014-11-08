/**
 * 
 */
package movement;

/**
 * @author chnhideyoshi
 *
 */
public class FromToProb implements Comparable<FromToProb> {
	public int from;
	public int to;
	public double probability;
	public double cumulativeprob;
	
	public FromToProb(int f, int t, double prob)
	{
		this.from = f;
		this.to = t;
		this.probability = prob;
	}

	@Override
	public int compareTo(FromToProb o) {
		return this.probability>=o.probability?1:-1;
	}
	
}
