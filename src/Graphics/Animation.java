package Graphics;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.peer.TextAreaPeer;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import Being.Being;
import Field.*;
import Field.Vector;

@SuppressWarnings("serial")
class Animation extends JComponent implements ActionListener {
    public static final int MAX_TIME = 400;
    private Vector[][] vectors;
    private int scale = 1;
    private Color color;
    private Timer timer;
    private int time = 0;
    private static boolean moving = false;
    private static JButton buttonStart = new JButton("Start");
    private static JButton buttonReset = new JButton("Reset");

    private static TextField textFieldAlive = new TextField(9);
    private static TextField textFieldZombie = new TextField(9);
    private static TextField textFieldDead = new TextField(9);
    private static boolean realLifeShowNumbers = true;
    private static boolean needToCount = true;
    private static JCheckBox checkBox = new JCheckBox("Result");

    private Field field ;
    private static Animation animation;
    private static JFrame frame = new JFrame("Zombie emulation");
    private static JPanel panel = new JPanel();
    private int width = 500;                     //todo
    private int height = 500;
    private int[] alive = new int[MAX_TIME];
    private int[] zombie = new int[MAX_TIME];
    private int[] dead = new int[MAX_TIME];

    private static int xPositionOfLine = -1;
    private static int yPositionOfLine = -1;
    private static boolean isDrawingALine = false;

    ActionListener taskPerformer = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if(time < vectors.length - 1 && moving) {
                time++;
            } else {
                time = 0;
                moving = false;
                stop();
                buttonStart.setText("Start");
                animation.field.setToZero();
            }
            needToCount = false;
            setText();
            repaint();
        }
    };


    public Animation(Vector[][] vectors, int delay) {
        timer = new Timer(delay, taskPerformer);
        this.vectors = vectors;
        setPreferredSize(new Dimension(500, 500));
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        setText();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);
        width = 500;                     //todo
        height = 500;
        g.fillRect(0, 0, width, height);
        g2d.setColor(Color.black);
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.scale(scale, scale);

        for (int i = 0; i < vectors[0].length; i++) {
            if (vectors[time][i].getBeing().isHuman()){
                g.setColor(Color.black);
            } else if (vectors[time][i].getBeing().isZombie()) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.green);
            }
            g.fillRect((int) (vectors[time][i].getX()*(width-2)/10 - 1.5), (int) (vectors[time][i].getY()*(height-2)/10 - 1.5), 3, 3);
        }
        g.setColor(Color.black);
        for (Wall wall: animation.field.getWalls()){
            g.drawLine((int) (((double)width - 2) / 10 * wall.getX1()), (int) (((double)height - 2) / 10 * wall.getY1()), (int) (((double)width - 2) / 10 * wall.getX2()), (int) (((double)height - 2) / 10 * wall.getY2()));
        }
        if (isDrawingALine){
            g.fillRect(xPositionOfLine-2, yPositionOfLine-2, 4, 4);
        }
    }

    private int getNumberOfAlive(int time){
        if (vectors[0][0] == null){
            return vectors[0].length;
        }
        int res = 0;
        for (Vector vector: vectors[time]){
            if (vector.getBeing().isHuman()){
                res++;
            }
        }
        return res;
    }

    private int getNumberOfDead(int time){
        if (vectors[0][0] == null){
            return 0;
        }
        int res = 0;
        for (Vector vector: vectors[time]){
            if (vector.getBeing().isDead()){
                res++;
            }
        }
        return res;
    }

    private int getNumberOfZombie(int time){
        if (vectors[0][0] == null){
            return 0;
        }
        int res = 0;
        for (Vector vector: vectors[time]){
            if (vector.getBeing().isZombie()){
                res++;
            }
        }
        return res;
    }

    private void setText(){
        if (!realLifeShowNumbers && needToCount){
            textFieldAlive.setText("? Alive");
            textFieldDead.setText("? Alive");
            textFieldZombie.setText("? Alive");
        } else if (realLifeShowNumbers){
            textFieldAlive.setText(animation.getNumberOfAlive(animation.time) + " Alive");
            textFieldDead.setText(animation.getNumberOfDead(animation.time) + " Dead");
            textFieldZombie.setText(animation.getNumberOfZombie(animation.time) + " Zombie");
        }else {
            textFieldAlive.setText(animation.getNumberOfAlive(MAX_TIME - 1) + " Alive");
            textFieldDead.setText(animation.getNumberOfDead(MAX_TIME - 1) + " Dead");
            textFieldZombie.setText(animation.getNumberOfZombie(MAX_TIME - 1) + " Zombie");
        }
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                animation = new Animation(new Vector[MAX_TIME][new Field().getSize()], 100);
                animation.field = new Field(false);

                animation.vectors[0] = animation.field.getZeroArray();
                animation.alive[0] = animation.getNumberOfAlive(animation.time);
                animation.dead[0] = animation.getNumberOfDead(animation.time);
                animation.zombie[0] = animation.getNumberOfZombie(animation.time);
                animation.addMouseListener(new MouseListener (){
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (animation.time != 0){
                            return;
                        }
                        if (e.getButton() != 3 && animation.field.getBeingByPosition((double) e.getX() / (animation.width - 2) * 10, (double) e.getY() / (animation.height - 2) * 10) >= 0) {

                        if (e.getButton() == 1) {
                            animation.field.setIll(animation.field.getBeingByPosition((double) e.getX() / (animation.width - 2) * 10, (double) e.getY() / (animation.height - 2) * 10));
                        }
                        if (e.getButton() == 2){
                            animation.field.setDead(animation.field.getBeingByPosition((double) e.getX() / (animation.width - 2) * 10, (double) e.getY() / (animation.height - 2) * 10));
                        }
                        animation.field.remember();


                        animation.vectors[0] = animation.field.getZeroArray();
                        animation.alive[0] = animation.getNumberOfAlive(animation.time);
                        animation.dead[0] = animation.getNumberOfDead(animation.time);
                        animation.zombie[0] = animation.getNumberOfZombie(animation.time);
                        animation.setText();
                        animation.time = 0;
                        animation.stop();

                        moving = false;
                        buttonStart.setText("Start");
                        } else if (e.getButton() == 3){
                            if (!isDrawingALine){
                                isDrawingALine = true;
                                xPositionOfLine = e.getX();
                                yPositionOfLine = e.getY();
                                if (animation.field.findPointForWall((double)xPositionOfLine/(animation.width-2)*10, (double)yPositionOfLine/(animation.height-2)*10) != null){
                                    int x = xPositionOfLine;
                                    xPositionOfLine = (int)(((double)animation.width - 2) / 10 * animation.field.findPointForWall((double)xPositionOfLine/((double)animation.width-2)*10, (double)yPositionOfLine/(animation.height-2)*10).getX());
                                    yPositionOfLine = (int)(((double)animation.height - 2) / 10 * animation.field.findPointForWall((double)x/((double)animation.width-2)*10, (double)yPositionOfLine/(animation.height-2)*10).getY());
                                }
                            } else {
                                isDrawingALine = false;

                                if (animation.field.findPointForWall((double)e.getX()/((double)animation.width-2)*10, (double)e.getY()/((double)animation.height-2)*10) != null &&
                                        (Math.abs(animation.field.findPointForWall((double)e.getX()/((double)animation.width-2)*10, (double)e.getY()/((double)animation.height-2)*10).getX() - (double)xPositionOfLine/((double)animation.width-2)*10) > 0.1 ||
                                        Math.abs(animation.field.findPointForWall((double)e.getX()/((double)animation.width-2)*10, (double)e.getY()/((double)animation.height-2)*10).getY() - (double)yPositionOfLine/((double)animation.width-2)*10) > 0.1)){
                                    animation.field.addWall((double)xPositionOfLine/((double)animation.width-2)*10,
                                            (double)yPositionOfLine/((double)animation.height-2)*10,
                                            animation.field.findPointForWall((double)e.getX()/((double)animation.width-2)*10, (double)e.getY()/((double)animation.height-2)*10).getX(),
                                            animation.field.findPointForWall((double)e.getX()/((double)animation.width-2)*10, (double)e.getY()/((double)animation.height-2)*10).getY());

                                }else {
                                    animation.field.addWall((double) xPositionOfLine / ((double)animation.width - 2) * 10, (double) yPositionOfLine / ((double)animation.height - 2) * 10,
                                            (double) e.getX() / ((double)animation.width - 2) * 10, (double) e.getY() / ((double)animation.height - 2) * 10);
                                }
                                yPositionOfLine = xPositionOfLine = -1;
                            }

                        }
                        animation.repaint();
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                });

                panel.add(animation);
                frame.getContentPane().add(panel);

                buttonReset.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        animation.field = new Field(false);
                        animation.vectors[0] = animation.field.getZeroArray();
                        animation.alive[0] = animation.getNumberOfAlive(animation.time);
                        animation.dead[0] = animation.getNumberOfDead(animation.time);
                        animation.zombie[0] = animation.getNumberOfZombie(animation.time);
                        animation.time = 0;
                        animation.stop();
                        animation.repaint();
                        moving = false;
                        buttonStart.setText("Start");
                        needToCount = true;
                        animation.setText();
                    }
                });

                buttonStart.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (moving) {
                            moving = false;
                            animation.stop();
                            buttonStart.setText("Go on");
                        } else {
                            if (buttonStart.getText().equals("Start") && needToCount){
                                animation.vectors = (animation.field ).getAnimation(MAX_TIME);
                            }
                            moving = true;
                            animation.start();
                            buttonStart.setText("Stop ");
                        }
                        needToCount = false;
                        animation.setText();
                    }
                });

                checkBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        realLifeShowNumbers = !realLifeShowNumbers;
                        animation.setText();
                    }
                });

                JPanel jPanel = new JPanel();
                jPanel.add(buttonReset);
                jPanel.add(buttonStart);

                animation.setText();
                textFieldAlive.setEditable(false);
                textFieldDead.setEditable(false);
                textFieldZombie.setEditable(false);

                jPanel.add(textFieldAlive);
                jPanel.add(textFieldZombie);
                jPanel.add(textFieldDead);
                jPanel.add(checkBox);
                panel.add(jPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setBounds(300,80,530,585);
                frame.setVisible(true);
            }
        });
    }
}