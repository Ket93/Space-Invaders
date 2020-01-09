import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.MouseInfo;
import java.util.*;

public class SpaceInvaders extends JFrame implements ActionListener, KeyListener {
    Timer myTimer;
    GamePanel game = new GamePanel();

    public SpaceInvaders (){
        super ("Space Invaders");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,850);

        myTimer = new Timer(10, this);
        myTimer.start();

        setFocusable(true);
        requestFocus();
        setVisible (true);
        setResizable (true);
        addKeyListener(this);
        addMouseListener(game.getTitleScreen());
        addMouseMotionListener(game.getTitleScreen());

        add(game);

    }

    public void actionPerformed(ActionEvent evt){
        game.move();
        game.repaint();
    }


    public static void main (String [] args){
        SpaceInvaders frame = new SpaceInvaders();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}

class GamePanel extends JPanel implements KeyListener{
    private Ship ship=new Ship();
    private Bullet shipBullet=new Bullet("shipBullet");
    private Shield shield=new Shield();
    private Enemy enemy = new Enemy();
    private Stats stats = new Stats();
    private TitleScreen title = new TitleScreen();
    private int position;
    private boolean [] keys;
    private boolean onTitle = true;

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
        if (onTitle){
            onTitle = title.draw(g);
        }
        else if (!onTitle){
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,1000,850);
            ship.draw(g);
            shieldEdit();
            enemyCheck();
            enemy.draw(g);
            shield.shieldDraw(g);
            stats.draw(g);
            shipBullet.shipBulletDraw(g);
        }
    }

    public TitleScreen getTitleScreen(){
        return title;
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
        int bx=shipBullet.getbx();
        int by=shipBullet.getby();
        if(200<=bx+10 && bx+10<320){
            if(500<=by && by<560){
                if(shield.getShield1()[(by-500)/12][(bx-200+10)/12]==0){
                    shield.setShield1((bx-200+10)/12,(by-500)/12);
                    shipBullet.reset();
                }
            }
        }
        else if(450<=bx+10 && bx+10<570){
            if(500<=by && by<560){
                if(shield.getShield2()[(by-500)/12][(bx-450+10)/12]==0){
                    shield.setShield2((bx-450+10)/12,(by-500)/12);
                    shipBullet.reset();
                }
            }
        }
        else if(700<=bx+10 && bx+10<820){
            if(500<=by && by<560){
                if(shield.getShield3()[(by-500)/12][(bx-700+10)/12]==0){
                    shield.setShield3((bx-700+10)/12,(by-500)/12);
                    shipBullet.reset();
                }
            }
        }
    }
    public void enemyCheck(){
        int bx=shipBullet.getbx();
        int by=shipBullet.getby();
        ArrayList<Integer>posX=enemy.getposX();
        int posY=enemy.getpositionY();
        int[][]enemies=enemy.getenemies();
        if(posY-250<by && by<posY && posX.get(0)<bx && bx<posX.get(posX.size()-1)+50){
            for(int y=0; y<5; y++){
                for(int x=0; x<11; x++){
                    if(enemies[y][x]!=0){
                        if(posX.get(x)<bx && bx<posX.get(x)+50){
                            if(posY-250+(y*50)<by && by<posY-200+(y*50)){
                                shipBullet.reset();
                                enemies[y][x]=0;
                                enemy.setenemies(enemies);
                            }
                        }
                    }
                }
            }
        }
    }
}

class TitleScreen implements MouseListener, MouseMotionListener{
    private Image spacePic;
    private Image enemyShip1;
    private Image enemyShip2;
    private Image enemyShip3;
    private boolean mousePressed;
    private int mx;
    private int my;

    public TitleScreen(){
        spacePic = new ImageIcon("SpaceInvadersIMGS/spaceBackground.png").getImage();
        enemyShip1 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
        enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
        enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    public void mousePressed (MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1){
            mousePressed = true;
        }
    }

    public void mouseReleased (MouseEvent e){
        if (e.getButton() == MouseEvent.BUTTON1){
            mousePressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    public boolean draw (Graphics g){
        g.drawImage(spacePic, 0, 0, null);
        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("Consolas",Font.PLAIN,190);
        Font smallFont = new Font("Consolas",Font.PLAIN,90);
        Font superSmallFont = new Font ("Consolas",Font.PLAIN,50);
        Font verySmallFont = new Font ("Consolas",Font.PLAIN,30);
        g2d.setFont(font);
        g2d.setColor(Color.white);
        g2d.drawString("SPACE",245,170);
        g2d.setFont(smallFont);
        g2d.setColor(Color.green);
        g2d.drawString("INVADERS",315,260);
        g.drawImage(enemyShip1, 395, 290, null);
        g2d.setColor(Color.white);
        g2d.setFont(verySmallFont);
        g2d.drawString("= 10 PTS",470,320);
        g.drawImage(enemyShip2, 395, 350, null);
        g2d.drawString("= 20 PTS",470,380);
        g.drawImage(enemyShip3, 395, 410, null);
        g2d.drawString("= 40 PTS",470,440);
        g2d.drawString("By: Kevin Cui and Adam Gaisinsky",235,720);
        g2d.setFont(superSmallFont);
        if ((365<=mx)&&(mx<=641)&&(my<=650)&&(615<=my)){
            g2d.setColor(Color.green);
            g2d.drawString("Start Game",358,620);
            if (mousePressed == true){
                return false;
            }
        }
        else{
            g2d.drawString("Start Game", 358, 620);
        }
        return true;
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mx = mouseEvent.getX();
        my = mouseEvent.getY();
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
    private int[][]enemies=
            {{3,3,3,3,3,3,3,3,3,3,3},
                    {2,2,2,2,2,2,2,2,2,2,2},
                    {2,2,2,2,2,2,2,2,2,2,2},
                    {1,1,1,1,1,1,1,1,1,1,1},
                    {1,1,1,1,1,1,1,1,1,1,1}};

    public Enemy () {
        for (int i = 50; i<600 ;i+=50) {
            posX.add(i);
        }
        positionY = 350;
        enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
        enemyShip = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
        enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
    }

    public void draw (Graphics g) {
        for(int y=0; y<5; y++){
            for(int x=0; x<11; x++){
                if(enemies[y][x]==3){
                    g.drawImage(enemyShip3, posX.get(0)+(x*50), positionY-250+(y*50), null);
                }
                else if(enemies[y][x]==2){
                    g.drawImage(enemyShip2, posX.get(0)+(x*50), positionY-250+(y*50), null);
                }
                else if(enemies[y][x]==1){
                    g.drawImage(enemyShip, posX.get(0)+(x*50), positionY-250+(y*50), null);
                }
            }
        }
    }

    public void move (){
        int edgePos=0;
        if(left){
            for(int x=0; x<10; x++){
                for(int y=0; y<5; y++){
                    if(enemies[y][x]!=0){
                        edgePos=x;
                        x=99;
                        y=99;
                    }
                }
            }
            if (posX.get(edgePos)==0){
                left = false;
                positionY+=20;
            }
        }
        else if(!left){
            for(int x=10; x>=0; x--){
                for(int y=0; y<5; y++){
                    if(enemies[y][x]!=0){
                        edgePos=x;
                        x=-99;
                        y=99;
                    }
                }
            }
            if (posX.get(edgePos)==930){
                left = true;
                positionY+=20;
            }
        }
        for (int i = 0; i<11; i++){
            if (left){
                posX.set(i,posX.get(i)-1);
            }
            else if (!left){
                posX.set(i,posX.get(i)+1);
            }
        }
    }

    public int[][] getenemies(){
        return enemies;
    }
    public ArrayList<Integer> getposX(){
        return posX;
    }
    public int getpositionY(){
        return positionY;
    }
    public void setenemies(int[][]newEnemies){
        enemies=newEnemies;
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
            bx=x+22;
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
            {{0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}};

    private int[][]shield2=
            {{0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}};
    private int[][]shield3=
            {{0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0}};
    public Shield(){
    }
    public void shieldDraw(Graphics g){
        g.setColor(new Color(0,255,0));
        for(int y=0; y<5; y++){
            for(int x=0; x<10; x++){
                if(shield1[y][x]==0){
                    g.fillRect(200+(x*12),500+(y*12),12,12);
                }
                if(shield2[y][x]==0){
                    g.fillRect(450+(x*12),500+(y*12),12,12);
                }
                if(shield3[y][x]==0){
                    g.fillRect(700+(x*12),500+(y*12),12,12);
                }
                g.setColor(new Color(0,0,0));
                if(shield1[y][x]==1){
                    g.fillRect(200+(x*12),500+(y*12),12,12);
                }
                if(shield2[y][x]==1){
                    g.fillRect(450+(x*12),500+(y*12),12,12);
                }
                if(shield3[y][x]==1){
                    g.fillRect(700+(x*12),500+(y*12),12,12);
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
