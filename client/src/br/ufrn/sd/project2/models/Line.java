package br.ufrn.sd.project2.models;

/**
 *
 * @author dhiogoboza
 */
public class Line {

	private int x1;
	private int y1;
	
	private int x2;
	private int y2;

	public Line(double x1, double y1, double x2, double y2) {
		//this.x1 = round(x1);
		//this.y1 = round(y1);
        this.x1 = (int) x1;
		this.y1 = (int) y1;
        
        this.x2 = (int) x2;
		this.y2 = (int) y2;
		//this.x2 = round(x2);
		//this.y2 = round(y2);
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public static int round(double value) {
		int aux = ((int) value % 10);
		int aux2;
		
		if (aux > 5) {
			aux2 = 10;
		} else {
			aux2 = 0;
		}
		
		return (((int) value - aux) + aux2);
	}

	@Override
	public String toString() {
		return "[x1: " + x1 + ", y1: " + y1 + ", x2: " + x2 + ", y2: " + y2 + "]";
	}
	
}
