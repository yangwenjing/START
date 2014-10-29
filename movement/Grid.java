package movement;

import java.util.Random;

import core.Coord;

public class Grid {
	private int x;
	private int y;
	
	private int id;
	private static int idCounter = 0;
	
	
	public Grid(int x_, int y_)
	{
		this.x = x_;
		this.y = y_;
		
		
		id = idCounter;
		idCounter++;
				
	}
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	
	public int hashCode() {
		return (x+","+y).hashCode();
	}
	
	

}
