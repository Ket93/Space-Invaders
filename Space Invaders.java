import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class SpaceInvaders extends JFrame {

    GamePanel game = new GamePanel();

    public SpaceInvaders (){
        super ("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100,700);
        setLayout(new BorderLayout());
        setVisible (true);

    }

    public static void main (String [] args){
        SpaceInvaders frame = new SpaceInvaders();
    }

}

class GamePanel extends JPanel{
    public void background (Graphics pic){
    pic.setColor(new Color (0,0,0) );
    pic.fillRect (0,0,getWidth(),getHeight());
    }
        }

class Ship{
    private int lives;
    private int score;
    private int position;

    public Ship(){
        lives = 3;
        score  = 0;
        position = 0;
    }

    public void move (){

    }

    public void load (){

    }

    public void shoot (){

    }

    public void shield (){

    }





}

class Enemy{
    private String enemyType;
    private String bulletType;
    private int bulletSpeed;
    private int enemiesLeft;
    private int positionX;
    private int positionY;

}

class Bullets{


}



