package Field;

import Being.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;

public class Field {
    public static final double PEOPLE_DENSITY = 4;
    public static final double X_AREA = 10;
    public static final double Y_AREA = 10;
    public static final double RADIUS_DETECT_ZOMBIE = 2;
    public static final double RADIUS_DETECT_HUMAN = 1.5;
    public static final double RADIUS_FIGHT = 0.2;
    public static final double PROBABILITY_OF_ZOMBIE_TO_DIE = 0.2;
    public static final double RADIUS_TOUCH_DETECT = 0.3;
    public static final double RADIUS_TO_DETECT_WALL_POINT = 0.3;
    private List<Being> beings = new ArrayList<Being>();
    private final List<Being> zeroBeings = new ArrayList<Being>();
    private boolean canEscape = true;
    private List<Wall> walls = new ArrayList<Wall>();

    public Field(){
        for (double d = 1/sqrt(PEOPLE_DENSITY)/3; d < X_AREA-1/sqrt(PEOPLE_DENSITY)/3; d+= 1/sqrt(PEOPLE_DENSITY) ){
            for (double d1 = 1/sqrt(PEOPLE_DENSITY)/2; d1 < Y_AREA; d1+= 1/sqrt(PEOPLE_DENSITY )){
                beings.add(new Human(d,d1));
            }
        }
        for (Being being: beings){
            if (being.isHuman()){
                zeroBeings.add(new Human(being.getX(), being.getY()));
            }
            if (being.isZombie()){
                zeroBeings.add(new Zombie(being.getX(), being.getY()));
            }
        }

    }

    public Field(boolean canEscape){
        this();
        this.canEscape = canEscape;

    }

    public void consoleOut(){
        for (Being being: beings){
            System.out.println(being);
        }
        System.out.println("------------------------------------------------------");
        for (Being being: zeroBeings){
            System.out.println(being);
        }
        System.out.println("Number of beings = " + beings.size());
    }

    private void turnIntoZombie(int number){
        double x = beings.get(number).getX();
        double y = beings.get(number).getY();
        beings.set(number, new Zombie(x,y));
    }

    private void turnIntoHuman(int number){
        double x = beings.get(number).getX();
        double y = beings.get(number).getY();
        beings.set(number, new Human(x,y));
    }

    private void randomMove(int number){
        Random random = new Random();
        double dx = random.nextDouble()*pow(-1, random.nextInt())/20;
        double dy = random.nextDouble()*pow(-1, random.nextInt())/20;
        if (canNotPassWall(beings.get(number).getX(), dx, beings.get(number).getY(), dy)){
            dx*=-1;
            dy*=-1;
        }

        if (beings.get(number).getY() > Y_AREA && !canEscape){        //todo doesn't work
            dy = (Y_AREA - beings.get(number).getY());
        }
        if (beings.get(number).getY() < 0 && !canEscape){
            dy = -1* beings.get(number).getY();
        }
        if (beings.get(number).getX() > X_AREA && !canEscape){
            dx =  (X_AREA - beings.get(number).getX());
        }
        if (beings.get(number).getX() < 0 && !canEscape){
            dx = -1 * beings.get(number).getX();
        }

        beings.get(number).move(dx, dy);
    }

    private void move(int number){
        if (hasEnemyInAreaToMove(number)){
            Vector vector = getDirection(number);
            if (canNotPassWall(beings.get(number).getX(), vector.getX(), beings.get(number).getY(), vector.getY())){
                vector.setX(vector.getX()*-1);
                vector.setY(vector.getY()*-1);
            }

            if (beings.get(number).getY() > Y_AREA && !canEscape){        //todo doesn't work
                vector.setY(2 * (Y_AREA - beings.get(number).getY()) );
            }
            if (beings.get(number).getY() < 0 && !canEscape){
                vector.setY(-2 * beings.get(number).getY() );
            }
            if (beings.get(number).getX() > X_AREA && !canEscape){
                vector.setX(2 * (X_AREA - beings.get(number).getX()));
            }
            if (beings.get(number).getX() < 0 && !canEscape){
                vector.setX(-2 * beings.get(number).getX());
            }

            beings.get(number).move(vector.getX(), vector.getY());
        }else {
            randomMove(number);
        }
    }

    private boolean outOfBound(double x, double dx, double xBound){
        return  (x + dx < 0) || (x + dx > xBound );
    }

    private void fight(int number){
        if (hasEnemyInAreaToFight(number)){
            int numberOfEnemies = numberOfEnemiesToFight(number);
            if (beings.get(number).isZombie() && random() / numberOfEnemies < PROBABILITY_OF_ZOMBIE_TO_DIE) {
                turnToDeadMan(number);
            }else{
                turnIntoZombie(number);
            }
        }
    }

    private void turnToDeadMan(int number){
        double x = beings.get(number).getX();
        double y = beings.get(number).getY();
        beings.set(number, new DeadMan(x,y));
    }

    private int numberOfEnemiesToFight(int number){
        int res = 0;
        for (int i = 0; i < beings.size(); i++){
            if (((beings.get(i).isZombie() && beings.get(number).isHuman()) || (beings.get(i).isHuman() && beings.get(number).isZombie())) && getDistance(i, number) < RADIUS_FIGHT){
                res++;
            }
        }

        return res;
    }

    private boolean hasEnemyInArea(int number, double radius){
        for (int i = 0; i < beings.size(); i++){
            if (((beings.get(i).isZombie() && beings.get(number).isHuman()) || (beings.get(i).isHuman() && beings.get(number).isZombie())) && getDistance(i, number) < radius){
                return true;
            }
        }
        return  false;
    }

    private boolean hasEnemyInAreaToMove(int number){
        double radius = beings.get(number).isZombie() ? RADIUS_DETECT_HUMAN : RADIUS_DETECT_ZOMBIE;
        return hasEnemyInArea(number, radius);
    }

    private boolean hasEnemyInAreaToFight(int number){
        return hasEnemyInArea(number, RADIUS_FIGHT);
    }

    private Vector getDirection(int number){
        Vector res = new Vector(0,0);
        if (!hasEnemyInAreaToMove(number)){
            return res;
        }
        double radius;
        int count = 1;
        for (int i = 0; i < beings.size(); i++){
            radius = beings.get(number).isZombie() ? RADIUS_DETECT_HUMAN : RADIUS_DETECT_ZOMBIE;
            if ((beings.get(i).isZombie() ^ beings.get(number).isZombie()) && getDistance(i, number) < radius){
                double dx = beings.get(i).getX()-beings.get(number).getX();
                double dy = beings.get(i).getY()-beings.get(number).getY();
                double r = sqrt(dx*dx + dy*dy);
                if (r > Zombie.getSpeed()) {
                    dx /= (r);
                    dy /= (r);
                }
                count++;
                if (beings.get(number).isZombie()){
                    res.plus(dx, dy);
                }else {
                   res.plus(-1*dx, -1*dy);
                }
            }
        }
        res.del(count*2);
        return res;
    }

    private void timeStep(){
        for (int i = 0; i < beings.size(); i++) {
            fight(i);
        }
        for (int i = 0; i < beings.size(); i++){
            move(i);
        }
    }

    private double getDistance(int i, int j){
        double x1 = beings.get(i).getX();
        double x2 = beings.get(j).getX();
        double y1 = beings.get(i).getY();
        double y2 = beings.get(j).getY();
        return sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    private Vector[] getArray(){
        Vector res[] = new Vector[beings.size()];
        for (int i = 0; i < beings.size(); i++){
            res[i] = new Vector(beings.get(i).getX(), beings.get(i).getY(), beings.get(i));
        }
        return  res;
    }

    public Vector[] getZeroArray(){
        return getArray();
    }

    public Vector[][] getAnimation(int maxTime){
        Vector res[][] = new Vector[maxTime][beings.size()];
        for(int i = 0; i < maxTime; i++){
            res[i] = getArray();
            timeStep();
        }
        setToZero();
        return res;
    }

    public void remember(){
        zeroBeings.clear();
        for (Being being: beings){
            if (being.isHuman()){
                zeroBeings.add(new Human(being.getX(), being.getY()));
            }
            if (being.isZombie()){
                zeroBeings.add(new Zombie(being.getX(), being.getY()));
            }
        }
    }

    public void setIll(int number){
        if (beings.get(number).isHuman()) {
            turnIntoZombie(number);
        } else if (beings.get(number).isZombie()){
            turnIntoHuman(number);
        } else if (beings.get(number).isDead()){
            turnIntoZombie(number);
        }
    }

    public void setDead(int number){
        if (beings.get(number).isHuman() || beings.get(number).isZombie()) {
            turnToDeadMan(number);
        } else if (beings.get(number).isDead()){
            turnIntoHuman(number);
        }
    }

    public void setIll(int[] numbers){
        for (int i: numbers){
            turnIntoZombie(i);
        }
    }

    public int getBeingByPosition(double x, double y){
        for (int i = 0; i < beings.size(); i++){
            if ( sqrt( (x - beings.get(i).getX())*(x - beings.get(i).getX()) + (y - beings.get(i).getY())*(y - beings.get(i).getY())) < RADIUS_TOUCH_DETECT  ){
                return i;
            }
        }
        return -1;
    }

    public void setToZero(){
        beings.clear();
        for (Being being: zeroBeings){
            if (being.isHuman()){
                beings.add(new Human(being.getX(), being.getY()));
            }
            if (being.isZombie()){
                beings.add(new Zombie(being.getX(), being.getY()));
            }
        }
    }

    public void setToZeroPosition(){
        for (int i = 0; i < beings.size(); i++){
            beings.get(i).setPosition(zeroBeings.get(i).getX(), zeroBeings.get(i).getY());
        }
    }

    public int getSize(){
        return beings.size();
    }

    public int getNumberOfAlive(){
        int res = 0;
        for (Being being: beings){
            if (being.isHuman()){
                res++;
            }
        }
        return res;
    }

    public int getNumberOfZombie(){
        int res = 0;
        for (Being being: beings){
            if (being.isZombie()){
                res++;
            }
        }
        return res;
    }

    public int getNumberOfDead(){
        int res = 0;
        for (Being being: beings){
            if (being.isDead()){
                res++;
            }
        }
        return res;
    }

    public void addWall(double x1, double y1, double x2, double y2){
        walls.add(new Wall(x1, y1, x2, y2));
    }

    public Wall[] getWalls(){
        Wall[] wallsRes = new Wall[walls.size()];
        for (int i = 0; i < walls.size(); i++){
            wallsRes[i] = walls.get(i);
        }
        return wallsRes;
    }

    private boolean canNotPassWall(double x, double dx, double y, double dy){
        for (Wall wall: walls){
            if (wall.crossedWall(x, dx, y, dy)){
                return true;
            }
        }
        return false;
    }

    public Vector findPointForWall(double x, double y){
        double xRes = -1, yRes = -1,r = 100000;
        for (Wall wall:walls){

            if (sqrt((x - wall.getX1()) * (x - wall.getX1()) + (y - wall.getY1()) * (y - wall.getY1())) < RADIUS_TO_DETECT_WALL_POINT &&
                    r > sqrt((x - wall.getX1()) * (x - wall.getX1()) + (y - wall.getY1()) * (y - wall.getY1()))){
                r = sqrt((x - wall.getX1()) * (x - wall.getX1()) + (y - wall.getY1()) * (y - wall.getY1()));
                xRes = wall.getX1();
                yRes = wall.getY1();
            }
            if (sqrt((x - wall.getX2()) * (x - wall.getX2()) + (y - wall.getY2()) * (y - wall.getY2())) < RADIUS_TO_DETECT_WALL_POINT &&
                    r > sqrt((x - wall.getX2()) * (x - wall.getX2()) + (y - wall.getY2()) * (y - wall.getY2()))){
                xRes = wall.getX2();
                yRes = wall.getY2();
            }
        }
        if (xRes != -1){
            return new Vector(xRes, yRes);
        }
        return null;
    }

    public static void main(String[] args) {
        Field field = new Field();
        field.consoleOut();
        System.out.println("");
        field.timeStep();
        field.consoleOut();
    }
}
