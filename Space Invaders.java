import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;

public class SpaceInvaders extends JFrame implements ActionListener {
    Timer myTimer;
    GamePanel game = new GamePanel();
    
    public SpaceInvaders (){
      super ("Space Invaders");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setSize(1000,850);
      
      myTimer = new Timer(10, this);
      myTimer.start();
      
      setVisible (true);
      setResizable (true);
        
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

class GamePanel extends JPanel implements KeyListener{
  private Ship ship=new Ship();
  private Bullet shipBullet=new Bullet("shipBullet");
  private int position;
  private boolean [] keys;
    
  public GamePanel(){
    setSize(1000,850);
    
    keys = new boolean[KeyEvent.KEY_LAST+1];
    addKeyListener(this);
  }
  public void addNotify(){
    super.addNotify();
    requestFocus();
  }
  
  public void paintComponent(Graphics g){
    g.setColor(new Color(0,0,0));  
    g.fillRect(0,0,1000,850);  
    ship.draw(g);
    shipBullet.shipBulletDraw(g);
  }
  
  public void move(){
    if (keys[KeyEvent.VK_RIGHT]) {
      ship.move(5);
    }
    if (keys[KeyEvent.VK_LEFT]) {
      ship.move(-5);
    }
    if (keys[KeyEvent.VK_UP]) {
      shipBullet.shoot(ship.position());
    }
  }
  
  public void keyTyped(KeyEvent keyEvent) {
    
  }
  
  public void keyPressed(KeyEvent keyEvent) {
    keys[keyEvent.getKeyCode()] = true;
  }
  
  public void keyReleased(KeyEvent keyEvent) {
    keys[keyEvent.getKeyCode()] = false;
  }
}

class Ship{
    private int lives;
    private int score;
    private int position;
    private Image shipPic;
    
    public Ship(){
      lives = 3;
      score  = 0;
      position = 100;
      shipPic= new ImageIcon("SpaceInvadersIMGS/spaceship.png").getImage();
    }
    public void move (int x){
      position+=x;
      if(position<0){
        position=0;
      }
      else if(position>920){
        position=920;
      }
    }
    public void draw(Graphics g){
      g.drawImage(shipPic,position,650,null);
    }
    public int position(){
      return position;
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

class Bullet{
  private int bx;
  private int by=650;
  private String type;
  private Image bulletPic;
  
  public Bullet(String type){
    bulletPic=new ImageIcon("SpaceInvadersIMGS/Bullets/"+type+".png").getImage();
  }
  public void shoot(int x){
    bx=x+20;
    by-=10;
  }
  public void shipBulletDraw(Graphics g){
    if(by<650){
      by-=10;
      g.drawImage(bulletPic,bx,by,null);
    }
    if(by==0){
      by=650;
    }
  }   
}

