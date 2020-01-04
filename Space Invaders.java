import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.MouseInfo;
import java.util.*;

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
    if(120<shipBullet.getbx() && shipBullet.getbx()<300 && 500<shipBullet.getby() && shipBullet.getby()<550){
      shipBullet.setRect();
      shipBullet.resetby();
    }
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
      shipBullet.shipBulletshoot(ship.position());
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
      g.setColor(new Color(0,255,0));  
      g.fillRect(150,500,150,50);
      g.fillRect(430,500,150,50);
      g.fillRect(700,500,150,50);      
    }
    
    public int position(){
      return position;
    }
}

class Bullet{
  private int bx=0;
  private int by=650;
  private String type;
  private Image bulletPic;
  private ArrayList<Integer>points = new ArrayList<Integer>();
  
  public Bullet(String type){
    bulletPic=new ImageIcon("SpaceInvadersIMGS/Bullets/"+type+".png").getImage();
    points.add(0);
    points.add(0);
  }
  public void shipBulletshoot(int x){
    if(bx==0){
      bx=x+20;
    }
    if(by==650){
      by-=10;
    }
  }
  public void shipBulletDraw(Graphics g){
    if(by<650){
      by-=10;
      g.drawImage(bulletPic,bx,by,null);
      for(int i=0; i<points.size(); i+=2){
        g.setColor(new Color(255,0,0));  
        g.fillRect(points.get(i),points.get(i+1),10,10);
      }
    }
    if(by<=0){
      by=650;
      bx=0;
    }
  }
  
  public int getbx(){
    return bx;
  }
  public int getby(){
    return by;
  }
  public void resetby(){
    by=0;
  }
  public void setRect(){
    points.add(bx);
    points.add(by);
  }
}
