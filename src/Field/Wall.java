package Field;

public class Wall {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public Wall(double x1, double y1, double x2, double y2){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean crossedWall(double x, double dx, double y, double dy){
        if (x1 == x2){
            return inSection(x, x1, x2, y, y1, y2) && (x-x1)*(x1-x-dx)>0;
        }
        if ( y1 == y2) {
            return inSection(x, x1, x2, y, y1, y2) && (y-y1)*(y1-y-dy)>0;
        }
        double k = (y1 - y2)/(x1 - x2);
        double b = y1 - k * x1;
        double b1 = (y + dy) - k * (x + dx);
        double b2 = y - k * x;
        return (((b - b1)*(b - b2) <= 0) && inSection(x, x1, x2, y, y1, y2));
    }

    private boolean inSection(double x, double x1, double x2, double y, double y1, double y2){
        if (Math.abs(x1 - x2) > 0.1) {
            return (((x >= x1) && (x <= x2)) || ((x <= x1) && (x >= x2)));
        }else {
            return (((y >= y1) && (y <= y2)) || ((y <= y1) && (y >= y2)));
        }
    }
}
