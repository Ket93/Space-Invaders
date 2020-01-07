import java.awt.*;
import java.awt.Font;
import java.awt.FontMetrics;
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
    private Shield shield=new Shield();
    private Enemy enemy = new Enemy();
    private Stats stats = new Stats();
    private boolean [] keys;

    public GamePanel(){
        setSize(1000,850);

        keys = new boolean[KeyEvent.KEY_LAST+1];
        addKeyListener(this);
    }
    public void addNotify(){
        super.addNotify();
        setFocusable(true);
        requestFocus();
    }

    public void paintComponent(Graphics g){
        g.setColor(new Color(0,0,0));
        g.fillRect(0,0,1000,850);
        ship.draw(g);
        shieldEdit();
        enemy.draw(g);
        stats.draw(g);
        shield.shieldDraw(g);
        shipBullet.shipBulletDraw(g);
    }

    public void move(){
        if (keys[KeyEvent.VK_RIGHT]) {
            ship.move(3);
        }
        if (keys[KeyEvent.VK_LEFT]) {
            ship.move(-3);
        }
        if (keys[KeyEvent.VK_SPACE]) {
            shipBullet.shipBulletshoot(ship.position());
        }
        enemy.move();
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        keys[keyEvent.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent keyEvent) {
        keys[keyEvent.getKeyCode()] = false;
    }
    public void shieldEdit(){
        if(200<=shipBullet.getbx() && shipBullet.getbx()<320){
            if(500<=shipBullet.getby() && shipBullet.getby()<560){
                if(shield.getShield1()[(shipBullet.getby()-500)/20][(shipBullet.getbx()-200)/20]==0){
                    shield.setShield1((shipBullet.getbx()-200)/20,(shipBullet.getby()-500)/20);
                    shipBullet.reset();
                }
            }
        }
        else if(450<=shipBullet.getbx() && shipBullet.getbx()<570){
            if(500<=shipBullet.getby() && shipBullet.getby()<560){
                if(shield.getShield2()[(shipBullet.getby()-500)/20][(shipBullet.getbx()-450)/20]==0){
                    shield.setShield2((shipBullet.getbx()-450)/20,(shipBullet.getby()-500)/20);
                    shipBullet.reset();
                }
            }
        }
        else if(700<=shipBullet.getbx() && shipBullet.getbx()<820){
            if(500<=shipBullet.getby() && shipBullet.getby()<560){
                if(shield.getShield3()[(shipBullet.getby()-500)/20][(shipBullet.getbx()-700)/20]==0){
                    shield.setShield3((shipBullet.getbx()-700)/20,(shipBullet.getby()-500)/20);
                    shipBullet.reset();
                }
            }
        }
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
    private int positionY;
    private boolean left = false;
    private int reachEnd;
    private Image enemyShip;
    private Image enemyShip2;
    private Image enemyShip3;
    private ArrayList<Integer> posX = new ArrayList<Integer>();

    public Enemy () {
        reachEnd = 0;
        for (int i = 50;i<600;i+=50) {
            posX.add(i);
        }
        positionY = 100;
        enemyShip = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
        enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
        enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
    }

    public void draw (Graphics g) {
        for (int i = 0; i<11; i++) {
            g.drawImage(enemyShip3, posX.get(i), positionY, null);
            g.drawImage(enemyShip, posX.get(i), positionY+50, null);
            g.drawImage(enemyShip, posX.get(i), positionY+100, null);
            g.drawImage(enemyShip2, posX.get(i), positionY+150, null);
            g.drawImage(enemyShip2, posX.get(i), positionY+200, null);
        }
    }

    public void move (){
        if (posX.get(0) == 0){
            left = false;
            reachEnd += 1;
        }
        if (posX.get(posX.size()-1)==930){
            left = true;
            reachEnd += 1;
        }
        if (reachEnd == 2){
            positionY += 20;
            reachEnd = 0;
        }
        for (int i = 0; i<11; i++){
            if (left){
                posX.set(i,posX.get(i)-1);
            }
            if (!left){
                posX.set(i,posX.get(i)+1);
            }
        }
    }
}

class Bullet{
    private int bx=0;
    private int by=650;
    private String type;
    private Image bulletPic;

    public Bullet(String type){
        bulletPic=new ImageIcon("SpaceInvadersIMGS/Bullets/"+type+".png").getImage();
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
        if(by<=0){
            by=650;
            bx=0;
        }
        if(by<650){
            by-=10;
            g.drawImage(bulletPic,bx,by,null);
        }
    }

    public int getbx(){
        return bx;
    }
    public int getby(){
        return by;
    }
    public void reset(){
        by=-1;
    }
}
class Shield{
    private int[][]shield1=
            {{0,0,0,0,0,0},
                    {0,0,0,0,0,0},
                    {0,0,0,0,0,0}};
    private int[][]shield2=
            {{0,0,0,0,0,0},
                    {0,0,0,0,0,0},
                    {0,0,0,0,0,0}};
    private int[][]shield3=
            {{0,0,0,0,0,0},
                    {0,0,0,0,0,0},
                    {0,0,0,0,0,0}};
    public Shield(){
    }
    public void shieldDraw(Graphics g){
        g.setColor(new Color(0,255,0));
        for(int y=0; y<3; y++){
            for(int x=0; x<6; x++){
                if(shield1[y][x]==0){
                    g.fillRect(200+(x*20),500+(y*20),20,20);
                }
                if(shield2[y][x]==0){
                    g.fillRect(450+(x*20),500+(y*20),20,20);
                }
                if(shield3[y][x]==0){
                    g.fillRect(700+(x*20),500+(y*20),20,20);
                }
                g.setColor(new Color(0,0,0));
                if(shield1[y][x]==1){
                    g.fillRect(200+(x*20),500+(y*20),20,20);
                }
                if(shield2[y][x]==1){
                    g.fillRect(450+(x*20),500+(y*20),20,20);
                }
                if(shield3[y][x]==1){
                    g.fillRect(700+(x*20),500+(y*20),20,20);
                }
                g.setColor(new Color(0,255,0));
            }
        }
    }
    public int[][] getShield1(){
        return shield1;
    }
    public int[][] getShield2(){
        return shield2;
    }
    public int[][] getShield3(){
        return shield3;
    }
    public void setShield1(int x, int y){
        shield1[y][x]=1;
    }
    public void setShield2(int x, int y){
        shield2[y][x]=1;
    }
    public void setShield3(int x, int y){
        shield3[y][x]=1;
    }
}

class Stats {
    private int score;
    private Image smallSpaceshipPic;

    public Stats (){
        score = 0;
        smallSpaceshipPic=new ImageIcon("SpaceInvadersIMGS/spaceshipSmall.png").getImage();
    }
    public void draw (Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("Consolas",Font.PLAIN,32);
        g2d.setFont(font);
        g2d.setColor(Color.white);
        g2d.drawString("SCORE",100,50);
        g2d.setColor(Color.green);
        g2d.drawString(Integer.toString(score),300,50);
        g2d.setColor(Color.white);
        g2d.drawString("LIVES", 600,50);
        g.drawImage(smallSpaceshipPic,730,20,null);
        g.drawImage(smallSpaceshipPic,800,20,null);
        g.drawImage(smallSpaceshipPic,870,20,null);

    }

        }
