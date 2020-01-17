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
    System.out.println(System.getProperty("user.dir"));
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
  private Bullet shipBullet=new Bullet();
  private Shield shield=new Shield();
  private Enemy enemy = new Enemy();
  private Bullet enemyBullet=new Bullet();
  private Stats stats = new Stats();
  private TitleScreen title = new TitleScreen();
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
      enemyShoot();
      enemyshieldEdit();
      ufoCheck();
      shield.shieldDraw(g);
      enemy.draw(g);
      enemyBullet.enemyBulletDraw(g);
      stats.draw(g);
      shipBullet.shipBulletDraw(g);
      shipCheck();
      enemy.gameWinner(g);
    }
  }

  public TitleScreen getTitleScreen(){
    return title;
  }


  public void move(){
    if(!onTitle){
      if (keys[KeyEvent.VK_D]) {
        ship.move(3);
      }
      if (keys[KeyEvent.VK_A]) {
        ship.move(-3);
      }
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
                if(enemies[y][x]==1){
                  stats.scoreAdd(10);
                }
                else if(enemies[y][x]==2){
                  stats.scoreAdd(20);
                }
                else if(enemies[y][x]==3){
                  stats.scoreAdd(40);
                }
                enemies[y][x]=0;
                enemy.setenemies(enemies);
              }
            }
          }
        }
      }
    }
  }
  public void enemyShoot(){
    int[][]enemies=enemy.getenemies();
    for(int x=0; x<11; x++){
      for(int y=4; y>=0; y--){
        if(enemies[y][x]!=0){
          if(randint(1,2000)==1){
            enemyBullet.addPts(enemy.getposX().get(x));
            enemyBullet.addPts(enemy.getpositionY()-((4-y)*50));
          }
          y=4;
          x+=1;
          if(x>=11){
            break;
          }
        }
      }
    }
  }
  public void enemyshieldEdit(){
    ArrayList<Integer>enemyPts=enemyBullet.getenemyPts();
    for(int i=0; i<enemyPts.size(); i+=2){
      int bx=enemyPts.get(i);
      int by=enemyPts.get(i+1);
      if(700<=bx+10 && bx+10<820){
        if(500<=by && by<560){
          if(shield.getShield3()[(by-500)/12][(bx-700+10)/12]==0){
            shield.setShield3((bx-700+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);
          }
        }
      }
      else if(450<=bx+10 && bx+10<570){
        if(500<=by && by<560){
          if(shield.getShield2()[(by-500)/12][(bx-450+10)/12]==0){
            shield.setShield2((bx-450+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);
          }
        }
      }
      else if(200<=bx+10 && bx+10<320){
        if(500<=by && by<560){
          if(shield.getShield1()[(by-500)/12][(bx-200+10)/12]==0){
            shield.setShield1((bx-200+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);
          }
        }
      }
    }
  }
  public void shipCheck(){
    ArrayList<Integer> enemyPts=enemyBullet.getenemyPts();
    if(enemyPts.size()>0){
      for(int i=0; i<enemyPts.size(); i+=2){
        if(enemyPts.get(i+1)>=650 && enemyPts.get(i+1)<=720){
          if(ship.position()<=enemyPts.get(i) && enemyPts.get(i)<=ship.position()+60) {
            stats.setLives();
            enemyBullet.enemyBulletClear();
            break;
          }
        }
      }
    }
  }
  public void ufoCheck(){
    if(enemy.UFOx()<shipBullet.getbx() && shipBullet.getbx()<enemy.UFOx()+60){
      if(enemy.UFOy()<shipBullet.getby() && shipBullet.getby()<enemy.UFOy()+32){
        int points = randint(1,3);
        stats.setScore(points*50);
        enemy.ufoOffScreen();
        shipBullet.reset();
      }
    }
  }

  public static int randint(int low, int high){
    return (int)(Math.random()*(high-low+1)+low);
  }
}



class TitleScreen implements MouseListener, MouseMotionListener{
  private Image spacePic;
  private Image enemyShip1;
  private Image enemyShip2;
  private Image enemyShip3;
  private Image ufoPic;
  private boolean mousePressed;
  private int mx;
  private int my;

  public TitleScreen(){
    ufoPic = new ImageIcon("SpaceInvadersIMGS/Ufo.png").getImage();
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
    g.drawImage(ufoPic, 385, 483, null);
    g2d.drawString("= ? PTS",470,505);
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
  private Image enemyShip;
  private Image enemyShip2;
  private Image enemyShip3;
  private Image spaceBackground;
  private Image ufoPic;
  private boolean ufoOnScreen;
  private int ufoPosX;
  private int ufoPosY;
  private boolean ufoLeft;
  private int ufoCount;
  private ArrayList<Integer> posX = new ArrayList<Integer>();
  private int[][]enemies=
          {{3,3,3,3,3,3,3,3,3,3,3},
                  {2,2,2,2,2,2,2,2,2,2,2},
                  {2,2,2,2,2,2,2,2,2,2,2},
                  {1,1,1,1,1,1,1,1,1,1,1},
                  {1,1,1,1,1,1,1,1,1,1,1}};
  private int enemyCount;

  public Enemy () {
    ufoCount = 0;
    positionY = 370;
    ufoOnScreen = false;
    ufoLeft = true;
    ufoPosX = 924;
    ufoPosY = 80;
    enemyCount = 0;
    for (int i = 50; i<600 ;i+=50) {
      posX.add(i);
    }
    ufoPic = new ImageIcon("SpaceInvadersIMGS/Ufo.png").getImage();
    spaceBackground = new ImageIcon("SpaceInvadersIMGS/spaceBackground.png").getImage();
    enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
    enemyShip = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
    enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
  }

  public void draw (Graphics g) {
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 11; x++) {
        if (enemies[y][x] == 3) {
          g.drawImage(enemyShip3, posX.get(0) + (x * 50), positionY - 250 + (y * 50), null);
        } else if (enemies[y][x] == 2) {
          g.drawImage(enemyShip2, posX.get(0) + (x * 50), positionY - 250 + (y * 50), null);
        } else if (enemies[y][x] == 1) {
          g.drawImage(enemyShip, posX.get(0) + (x * 50), positionY - 250 + (y * 50), null);
        }
      }
    }
    if ((GamePanel.randint(0, 750) == 1) && ufoOnScreen == false) {
      ufoOnScreen = true;
    }
    if (ufoOnScreen == true) {
      g.drawImage(ufoPic, ufoPosX, ufoPosY, null);
      ufoMove();
    }

    if ((ufoCount == 3 && ufoPosX<-50) | ufoOnScreen==false) {
      ufoOnScreen = false;
      ufoPosX = 924;
      ufoCount = 0;

    }
  }
  public void ufoMove(){
    if (ufoPosX == 0 && ufoCount != 3){
      ufoLeft = false;
      ufoCount +=1;
    }
    if (ufoPosX == 924 && ufoCount !=3){
      ufoLeft = true;
      ufoCount +=1;
    }
    if (ufoLeft == true){
      ufoPosX += -3;
    }
    if (ufoLeft == false){
      ufoPosX += 3;
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

  public void gameWinner(Graphics g){
    Graphics2D g2d = (Graphics2D) g;
    Font winFont = new Font ("Consolas",Font.PLAIN,150);
    Font scoreFont = new Font ("Consolas", Font.PLAIN, 60);
    g2d.setFont(winFont);
    enemyCount = 0;
    for (int i = 0; i<5; i++){
      for (int k = 0; k<11;k++){
        if (enemies[i][k] == 0){
          enemyCount += 1;
        }
      }
    }

    if (Stats.getLives() == 0){
      g.drawImage(spaceBackground, 0, 0, null);
      g2d.setColor(Color.green);
      g2d.drawString("GAME OVER!", 160, 340);
      g2d.setFont(scoreFont);
      g2d.setColor(Color.white);
      g2d.drawString("SCORE: " + Stats.getScore(),320, 450);
    }
  }
  public int UFOx(){
    return ufoPosX;
  }
  public int UFOy(){
    return ufoPosY;
  }
  public void ufoOffScreen(){
    ufoOnScreen=false;
  }
}




class Bullet{
  private int bx=0;
  private int by=650;
  private ArrayList<Integer> enemyPts=new ArrayList<Integer>();
  private Image bulletPic;
  private Image enemyBulletPic;

  public Bullet(){
    enemyBulletPic = new ImageIcon("SpaceInvadersIMGS/Bullets/enemybullet.png").getImage();
    bulletPic=new ImageIcon("SpaceInvadersIMGS/Bullets/shipBullet.png").getImage();
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
  public void enemyBulletDraw(Graphics g){
    ArrayList<Integer>remove=new ArrayList<Integer>();
    if(enemyPts.size()>0){
      for(int i=0; i<enemyPts.size(); i+=2){
        g.drawImage(enemyBulletPic,enemyPts.get(i),enemyPts.get(i+1),null);
        enemyPts.set(i+1,enemyPts.get(i+1)+8);
        if(enemyPts.get(i+1)>650){
          remove.add(i);
          remove.add(i+1);
        }
      }
      int size=remove.size();
      for(int i=size-1; i>=0; i--){
        enemyPts.remove(remove.get(i));
      }
      remove.clear();
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
  public void addPts(int points){
    enemyPts.add(points);
  }
  public ArrayList<Integer> getenemyPts(){
    return enemyPts;
  }
  public void enemyBulletreset(int x, int y){
    enemyPts.remove(y);
    enemyPts.remove(x);
  }
  public void enemyBulletClear(){
    enemyPts.clear();
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
  private static int score;
  private static int lives;
  private Image smallSpaceshipPic;

  public Stats (){
    score = 0;
    lives = 3;
    smallSpaceshipPic=new ImageIcon("SpaceInvadersIMGS/spaceshipSmall.png").getImage();
  }
  public void draw (Graphics g){
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas",Font.PLAIN,32);
    g2d.setFont(font);
    g2d.setColor(Color.white);
    g2d.drawString("SCORE:",100,50);
    g2d.setColor(Color.green);
    g2d.drawString(Integer.toString(score),220,50);
    g2d.setColor(Color.white);
    g2d.drawString("LIVES", 600,50);
    for(int i=0; i<lives; i++){
      g.drawImage(smallSpaceshipPic,730+(i*70),20,null);
    }
  }
  public void scoreAdd(int points){
    score+=points;
  }
  public static int getScore(){return score;}
  public static void setScore(int addScore){score += addScore;}
  public static int getLives() {return lives;}
  public static void setLives(){lives-=1;}
}
