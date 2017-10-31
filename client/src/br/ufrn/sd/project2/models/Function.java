
package br.ufrn.sd.project2.models;

/**
 *
 * @author dhiogoboza
 */
public class Function {
    
    // time in seconds
    private double start;
    private double end;
    
    private double a;
    private double b;

    public Function() {
        
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "y = " + a + "x + " + b;
    }
}
