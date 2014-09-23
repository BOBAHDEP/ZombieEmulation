package Being;

public abstract class Being {

    protected double x;
    protected double y;

    public abstract void move(double x, double y);

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public Being(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    public String toString(){
        return "(" + x + ", " + y + ")" + this.getClass();
    }

    public boolean isZombie(){
        return this.getClass() == Zombie.class;
    }

    public boolean isHuman(){
        return this.getClass() == Human.class;
    }

    public boolean isDead(){
        return this.getClass() == DeadMan.class;
    }
}
