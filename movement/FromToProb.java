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
		if(this.probability>o.probability)
			return 1;
		else if(this.probability<o.probability)
			return -1;
		else
			return 0;
	}
	
}
