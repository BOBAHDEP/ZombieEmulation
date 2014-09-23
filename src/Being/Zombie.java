package Being;

public class Zombie extends Being {

    private static final double SPEED = 1;

    public static double getSpeed() {
        return SPEED;
    }

    public Zombie(double x, double y) {
        super(x, y);
    }

    @Override
    public void move(double dx, double dy){
        this.x+=dx*SPEED;
        this.y+=dy*SPEED;
    }
}
