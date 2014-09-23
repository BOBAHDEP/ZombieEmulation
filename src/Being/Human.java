package Being;

public class Human extends Being {
    private static final double SPEED = 0.25;

    public Human(double x, double y) {
        super(x,y);
    }

    @Override
    public void move(double dx, double dy){
        this.x+=dx*SPEED;
        this.y+=dy*SPEED;
    }
}
