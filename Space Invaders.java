import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SpaceInvaders extends JFrame implements ActionListener {
    Timer myTimer;
    GamePanel game = new GamePanel();
    
    public SpaceInvaders (){
      super ("Space Invaders");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1100,700);
      
      myTimer = new Timer(10, this);
      myTimer.start();
      
      setVisible (true);
        
       add(game);
      
    }
    
      public void actionPerformed(ActionEvent evt){
        game.move();
        game.repaint();
      }   
    
    
    public static void main (String [] args){
      SpaceInvaders frame = new SpaceInvaders();
    }
    
}

class GamePanel extends JPanel{
  private int x;
  
  public GamePanel(){
    x=100;
    setSize(1100,700);
  }
  
  public void paintComponent(Graphics g){
    g.setColor(new Color(0,0,0));  
    g.fillRect(0,0,1100,700);  
    g.setColor(new Color(255,0,0));  
    g.fillRect(x,600,50,50);
  }
  
  public void move(){
    x+=1;
  }
      
}

/*/
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
/*/
