package Field;

import Being.Being;

public class Vector {
    private double x;
    private double y;
    private Being being;

    public Being getBeing() {
        return being;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(double x, double y, Being being) {
        this.x = x;
        this.y = y;
        this.being = being;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void plus(double dx, double dy){
        x += dx;
        y += dy;
    }

    public void del(int number){
        x /= number;
        y /= number;
    }
}
