import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.io.*;
import javax.sound.midi.*;
import java.util.*;
import java.applet.*;
import javax.sound.sampled.*;

public class SpaceInvaders extends JFrame implements ActionListener, KeyListener {
  Timer myTimer;
  GamePanel game = new GamePanel();
  public static Sequencer midiPlayer; // Midi music player 

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
    startMidi("SpaceInvadersSound/trippygaia1.mid"); // Midi sound file 

    add(game);
  }

  public static void startMidi(String midFilename) { // Playing background music 
    try {
      File midiFile = new File(midFilename);
      Sequence song = MidiSystem.getSequence(midiFile);
      midiPlayer = MidiSystem.getSequencer();
      midiPlayer.open(); // Opens file 
      midiPlayer.setSequence(song); // Sets music in player 
      midiPlayer.setLoopCount(100); // Repeat 0 times (play once)
      midiPlayer.start(); // Starts playing music 
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
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

class Sound{  // Class for sound effects
  File wavFile1 = new File("shipBulletSound.wav"); // File names
  File wavFile2 = new File("enemyBulletSound.wav");
  File wavFile3 = new File("explosion.wav");
  static AudioClip sound1;                         // Makes an audio clip with the sound
  static AudioClip sound2;
  static AudioClip sound3;
  public Sound(){
    try{sound1 = Applet.newAudioClip(wavFile1.toURL());} // Gets the sound and path
    catch(Exception e){e.printStackTrace();}
    try{sound2 = Applet.newAudioClip(wavFile2.toURL());}
    catch(Exception e){e.printStackTrace();}
    try{sound3 = Applet.newAudioClip(wavFile3.toURL());}
    catch(Exception e){e.printStackTrace();}
  }

  public static void shipBulletplay(){   // Plays the shipBullet sound
    sound1.play();
  }
  public static void enemyBulletplay(){  // Plays the enemyBullet sound    
    sound2.play();
  }
  public static void explosionplay(){   // Plays the explosion sound
    sound3.play();
  }
}

class Audio{
  static Clip clip;
  javax.sound.sampled.AudioInputStream audioInputStream;
  public Audio()
          throws UnsupportedAudioFileException,
          IOException, LineUnavailableException
  {
    // create AudioInputStream object
    audioInputStream = AudioSystem.getAudioInputStream(new File("src/SpaceInvadersSound/bottle-open.wav").getAbsoluteFile());

    // create clip reference
    clip = AudioSystem.getClip();

    // open audioInputStream to the clip
    clip.open(audioInputStream);

    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }
  public static void main (String[]args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    Audio audioPlayer = new Audio();
    audioPlayer.play();
  }
  public void play(){
    clip.start();
  }
}



class GamePanel extends JPanel implements KeyListener{
  // Making objects 
  private Ship ship=new Ship();
  private Bullet shipBullet=new Bullet();
  private Shield shield=new Shield();
  private Enemy enemy = new Enemy();
  private Bullet enemyBullet=new Bullet();
  private Stats stats = new Stats();
  private Sound sound = new Sound();
  private TitleScreen title = new TitleScreen();
  private boolean [] keys;
  private boolean onTitle;
  private boolean shipHit;
  private int shipCount;

  public GamePanel(){
    setSize(1000,850);

    keys = new boolean[KeyEvent.KEY_LAST+1];
    addKeyListener(this);
    onTitle = true;
    shipHit = false;
    shipCount = 0;
  }
  public void addNotify(){
    super.addNotify();
    setFocusable(true);
    requestFocus();
  }

  public void paintComponent(Graphics g){
    if (onTitle){ // If the user is on the title screen
      onTitle = title.draw(g);
    }
    else if (!onTitle){ // Once start game button is clicked draw the rest of the game 
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
      if (shipHit) { // draw -1 when life lost 
        stats.drawLives(g);
        shipCount +=1;
        if (shipCount == 70) { // -1 stays for 70 frames 
          shipHit = false;
          shipCount = 0;
        }
      }
      shipCheck();
      enemy.gameOver(g);
    }
  }

  public void move(){ // Changing the ships X position based on key pressed
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
      if (keys[KeyEvent.VK_SPACE]) { // Shoot bullets if space pressed 
        shipBullet.shipBulletshoot(ship.position());
      }
      enemy.move(); // Moving enemies 
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

  public void shieldEdit(){       // Method that takes the bullets shot by the ship and checks if they are colliding with the shield
    int bx=shipBullet.getbx();    // Gets the x and y coordinate of the shipBullet
    int by=shipBullet.getby();
    if(200<=bx+10 && bx+10<320){  // Checks to see if the bx and by are in the range of the first shield
      if(500<=by && by<560){
        if(shield.getShield1()[(by-500)/12][(bx-200+10)/12]==0){  // If the bullet is hitting the shield, the shield is checked to see if there is actually shield there or if it has already been destroyed.
// The by-500 subtracts off the y value of the shield leaving numbers from 0-60. When divided by 12, you get an index which correspons to a spot in the shield array.
// The distance away from the edge is subtracted from the x value to leave values from 0,120. This is divided by 12 which corresponds to a spot in the shield array. With x and y, you get an index from a 10 x 6 shield.
          shield.setShield1((bx-200+10)/12,(by-500)/12);          // Takes the co ordinate and calculates the x and y indexes in the shield array using ranges and division. The shield at that index is set to false meaning that it will be destroyed
          shipBullet.reset();   // The bullet is reset if it destroys the shield
        }
      }
    }
    else if(450<=bx+10 && bx+10<570){    // Same steps are repeated for sheild 2 except the bx values are subtracted by more as these shields are further from the edge
      if(500<=by && by<560){
        if(shield.getShield2()[(by-500)/12][(bx-450+10)/12]==0){
          shield.setShield2((bx-450+10)/12,(by-500)/12);
          shipBullet.reset();
        }
      }
    }
    else if(700<=bx+10 && bx+10<820){    // Same steps are repeated for the third shield
      if(500<=by && by<560){
        if(shield.getShield3()[(by-500)/12][(bx-700+10)/12]==0){
          shield.setShield3((bx-700+10)/12,(by-500)/12);
          shipBullet.reset();
        }
      }
    }
  }

  public void enemyCheck(){                  // Checks if the shipBullet collides with an enemy
    int bx=shipBullet.getbx();               // Gets the x and y position of the shipBullet
    int by=shipBullet.getby();
    ArrayList<Integer>posX=enemy.getposX();  // posX is an array of all the x positions of the enemies
    int posY=enemy.getpositionY();           // posY is the Y position of the bottom most enemy
    int[][]enemies=enemy.getenemies();       // enemies is an array of the enemies (1,2,3 represent enemy types
    if(posY-250<by && by<posY && posX.get(0)<bx && bx<posX.get(posX.size()-1)+50){ // If the shipBullet is in the range of connecting with an enemy
      for(int y=0; y<5; y++){                                 // Loops for every value in the enemy array (55 indexes)
        for(int x=0; x<11; x++){
          if(enemies[y][x]!=0){                               // If there is an enemy at the index
            if(posX.get(x)<bx && bx<posX.get(x)+50){          // and the x and y positions of the bullet are in the range of the enemy at the index (collision)
              if(posY-250+(y*50)<by && by<posY-200+(y*50)){
                sound.explosionplay(); // Play the explosion sound
                shipBullet.reset();    // The shipBullet is reset
                if(enemies[y][x]==1){  // If the enemy is type 1, you get 10 points
                  stats.scoreAdd(10);
                }
                else if(enemies[y][x]==2){ // If the enemy is type 2, you et 20 points
                  stats.scoreAdd(20);
                }
                else if(enemies[y][x]==3){ // If the enemy is type 43 you get 40 points
                  stats.scoreAdd(40);
                }
                enemies[y][x]=0;           // The bullet destroys the enemy and the original array is changed to not have an enemy where it was destroyed
                enemy.setenemies(enemies);
              }
            }
          }
        }
      }
    }
  }

  public void enemyShoot(){             // Initiates random enemies shooting bullets at you
    int[][]enemies=enemy.getenemies();  // Makes an array with the enemies in it
    for(int x=0; x<11; x++){            // Loops through every value in the array starting from the bottom left all the way to the top right
      for(int y=4; y>=0; y--){
        if(enemies[y][x]!=0){                           // If there is an enemy there
          if(randint(1,2001-enemy.bulletSpeed())==1){   // It has a 1 in 2000 chance of shooting a bullet. bulletSpeed goes up and increases bulletShoot chance when you kill al the enemies
            sound.enemyBulletplay();                    // Sound effect is played
            enemyBullet.addPts(enemy.getposX().get(x));           // The x and y positions of the bullets are added
            enemyBullet.addPts(enemy.getpositionY()-((4-y)*50));
          }
          y=4;           // The indexes are reset so that it starts checking the next row over at the bottom, only the bottom most enemy can shoot
          x+=1;
          if(x>=11){     // Breaks if the x is too big
            break;
          }
        }
      }
    }
  }

  public void enemyshieldEdit(){            // Changes the shield when the enemies shoot it
    ArrayList<Integer>enemyPts=enemyBullet.getenemyPts();  // Array holds the co ordinates of the enemy bullets
    for(int i=0; i<enemyPts.size(); i+=2){  // Loops for the amount of points in the array
      int bx=enemyPts.get(i);               // The x and y positions of the bullet are retrieved
      int by=enemyPts.get(i+1);
      if(700<=bx+10 && bx+10<820){          // Checks to see if the bullet is in the range of the first shield
        if(500<=by && by<560){
          if(shield.getShield3()[(by-500)/12][(bx-700+10)/12]==0){ // Uses the same math as shieldEdit
            shield.setShield3((bx-700+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);                   // Resets the enemyBullet
          }
        }
      }
      else if(450<=bx+10 && bx+10<570){                            // Same as Shield 1 except for the second shield
        if(500<=by && by<560){
          if(shield.getShield2()[(by-500)/12][(bx-450+10)/12]==0){
            shield.setShield2((bx-450+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);
          }
        }
      }
      else if(200<=bx+10 && bx+10<320){                            // Thrid shield check
        if(500<=by && by<560){
          if(shield.getShield1()[(by-500)/12][(bx-200+10)/12]==0){
            shield.setShield1((bx-200+10)/12,(by-500)/12);
            enemyBullet.enemyBulletreset(i,i+1);
          }
        }
      }
    }
  }

  public void ufoCheck(){
    if(enemy.UFOx()<shipBullet.getbx() && shipBullet.getbx()<enemy.UFOx()+60){ // Checks to see if the bullet is within the ufo's x range
      if(enemy.UFOy()<shipBullet.getby() && shipBullet.getby()<enemy.UFOy()+32){ // Checks to see if the bullet is within the ufo's y range
        sound.explosionplay();   // Plays the explosion sound
        enemy.ufoOffScreen();    // Stops drawing ufo if hit 
        stats.scoreAdd(randint(1,3)*50); // Adding score if ufo is hit 
        shipBullet.reset();      // Resets the ships bullet after it hits the ufo
      }
    }
  }

  public void shipCheck(){                                       // Checks to see if the enemy bullets hit your ship
    ArrayList<Integer> enemyPts=enemyBullet.getenemyPts();       // Array gets all the enemy bullet locations
    if(enemyPts.size()>0){
      for(int i=0; i<enemyPts.size(); i+=2){                     // Loops for all the enemy bullet points
        if(enemyPts.get(i+1)>=650 && enemyPts.get(i+1)<=720){
          if(ship.position()<=enemyPts.get(i) && enemyPts.get(i)<=ship.position()+60) { // Checks if the enemy bullet collides with your ship
            stats.setLives();                // Subtracts 1 life from your lives
            enemyBullet.enemyBulletClear();  // Resets all the enemy Bullets
            enemy.resetEnemies();            // Resets all the enemies
            shipHit = true;
            break;
          }
        }
      }
    }
  }

  public static int randint(int low, int high){   // Gets a random integer between a set range
    return (int)(Math.random()*(high-low+1)+low);
  }

  public TitleScreen getTitleScreen(){ // Returns title object 
    return title;
  }
}



class TitleScreen implements MouseListener, MouseMotionListener{ // Class for the title screen
  private Image spacePic; // Background image
  private Image enemyShip1; // Enemy ships 
  private Image enemyShip2;
  private Image enemyShip3;
  private Image ufoPic; // Red ufo picture 
  private boolean mousePressed; // Checks if the mouse if pressed down
  private int mx; // Mouse x position
  private int my; // Mouse y position 

  public TitleScreen(){
    // Getting images 
    ufoPic = new ImageIcon("SpaceInvadersIMGS/Ufo.png").getImage();
    spacePic = new ImageIcon("SpaceInvadersIMGS/spaceBackground.png").getImage();
    enemyShip1 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
    enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
    enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
  }

  @Override
  public void mouseClicked(MouseEvent mouseEvent) {

  }

  public void mousePressed (MouseEvent e){ // Checks if mouse is pressed down
    if (e.getButton() == MouseEvent.BUTTON1){
      mousePressed = true;
    }
  }

  public void mouseReleased (MouseEvent e){ // Checks if mouse is released 
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

  public boolean draw (Graphics g){ // Drawing the title screen 
    g.drawImage(spacePic, 0, 0, null); // Drawing background 
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas",Font.PLAIN,190); // Initializing fonts 
    Font smallFont = new Font("Consolas",Font.PLAIN,90);
    Font superSmallFont = new Font ("Consolas",Font.PLAIN,50);
    Font verySmallFont = new Font ("Consolas",Font.PLAIN,30);
    g2d.setFont(font); // Setting font 
    g2d.setColor(Color.white); // Setting Colour 
    g2d.drawString("SPACE",245,170); // Drawing text 
    g2d.setFont(smallFont);
    g2d.setColor(Color.green);
    g2d.drawString("INVADERS",315,260);
    g.drawImage(enemyShip1, 395, 290, null); // Drawing first image 
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
    if ((365<=mx)&&(mx<=641)&&(my<=650)&&(615<=my)){ // Checking if the user is hovering over the "Start Game" button
      g2d.setColor(Color.green);
      g2d.drawString("Start Game",358,620);
      if (mousePressed == true){ // If the user presses start game then draw the rest of the game 
        return false;
      }
    }
    else{ // If the user is not hovering over "Start Game"
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




class Ship{                     // Class for your ship
  private int lives;            // Lives stores how many lives you have left
  private int score;            // Score stores your current score
  private static int position;  // Position stores your position
  private Image shipPic;        // shipPic is the image of the ship

  public Ship(){
    lives = 3;        // You start at three lives with no score 100 pixels away from the left
    score  = 0;
    position = 100;
    shipPic= new ImageIcon("SpaceInvadersIMGS/spaceship.png").getImage(); // Ship image
  }

  public void move (int x){  // Moves the ship
    position+=x;             // position is added by the parameter
    if(position<0){          // Checks to see if the ship is in range, you cannot go off the screen
      position=0;
    }
    else if(position>920){
      position=920;
    }
  }

  public void draw(Graphics g){              // Draws the ship at it's position, it only goes horizontally
    g.drawImage(shipPic,position,650,null);
  }

  public static int position(){              // Returns the ship's position
    return position;
  }
}



class Enemy{                     // Class for the enemies
  private int positionY;         // The y position of the bottom most enemy
  private boolean left = false;  // Boolean for the direction
  private Image enemyShip;       // Three enemy images for the pics
  private Image enemyShip2;
  private Image enemyShip3;
  private Image spaceBackground; // Background image
  private Image ufoPic;          // ufo image
  private boolean ufoOnScreen;   // Boolean it see if the ufo is on the screen
  private int ufoPosX;           // X and Y position of the ufo
  private int ufoPosY;
  private boolean ufoLeft;       // Variables to control ufo movement
  private int ufoCount;
  private ArrayList<Integer> posX = new ArrayList<Integer>(); // Keeps the x position of the enemy
  private int[][]enemies=                    // Array for the enemies. Each value represents a different image and a different enemy
          {{3,3,3,3,3,3,3,3,3,3,3},
                  {2,2,2,2,2,2,2,2,2,2,2},
                  {2,2,2,2,2,2,2,2,2,2,2},
                  {1,1,1,1,1,1,1,1,1,1,1},
                  {1,1,1,1,1,1,1,1,1,1,1}};
  private int bulletSpeed;                   // As bulletSpeed increases, the enemies shoot bullets more frequently

  public Enemy () {
    ufoCount = 0;
    positionY = 370;
    ufoOnScreen = false;  // ufo dosen't start on the screen
    ufoLeft = true;
    ufoPosX = 984;
    ufoPosY = 80;
    for (int i = 50; i<600 ;i+=50) { // Adds 10 x positions to the posX list for the rows of enemies
      posX.add(i);
    }
    ufoPic = new ImageIcon("SpaceInvadersIMGS/Ufo.png").getImage();                        // Images
    spaceBackground = new ImageIcon("SpaceInvadersIMGS/spaceBackground.png").getImage();
    enemyShip2 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy.jpg").getImage();
    enemyShip = new ImageIcon("SpaceInvadersIMGS/spaceEnemy2.png").getImage();
    enemyShip3 = new ImageIcon("SpaceInvadersIMGS/spaceEnemy3.png").getImage();
    bulletSpeed=0;
  }

  public void draw (Graphics g) {       // Draws the enemies
    for (int y = 0; y < 5; y++) {       // For every enemy, depending on it's value, it will draw its respective image
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
    if ((GamePanel.randint(0, 1000) == 1) && ufoOnScreen == false) {  // 1/1000 times the ufo will go on the screen unless it's already there
      ufoOnScreen = true;
    }
    if (ufoOnScreen == true) {                      // If the ufo is on the screen, it's drawn and then moved
      g.drawImage(ufoPic, ufoPosX, ufoPosY, null);
      ufoMove();
    }

    if ((ufoCount == 3 && ufoPosX<-50) | ufoOnScreen==false) { // Resets ufo variables
      ufoOnScreen = false;
      ufoPosX = 984;
      ufoCount = 0;
    }
  }

  public void ufoMove(){                  // Moves the ufo
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

  public void move (){                  // Moves the enemies
    int edgePos=-1;                     // edgePos is a variable that checks the enemies at the left and right most sides. If there are no enemies, the value will be -1 (resets enemies)
    if(left){                           // If you are going left
      for(int x=0; x<11; x++){          // every index in the array is checked for an enemy starting from the left side
        for(int y=0; y<5; y++){
          if(enemies[y][x]!=0){         // If there is an enemy, that index is the edge of your enemies
            edgePos=x;
            if (posX.get(edgePos)==0){  // If the edge of your enemies is at the edge of the screen, the enemies move down by 20 pixels. The direction is changed
              left = false;
              positionY+=20;
            }
            x=99;  //Breaks out of both for loops
            y=99;
          }
        }
      }
    }
    else if(!left){                     // If you are going right
      for(int x=10; x>=0; x--){         // Loops from the right side to the left side
        for(int y=0; y<5; y++){
          if(enemies[y][x]!=0){         // Checks for an enemy and than checks if it is at the rightmost part of the screen
            edgePos=x;
            if (posX.get(edgePos)==930){
              left = true;
              positionY+=20;
            }
            x=-99;
            y=99;
          }
        }
      }
    }
    for (int i = 0; i<11; i++){     // If you are going left, all the enemy co ordinates decrease by 1
      if (left){
        posX.set(i,posX.get(i)-1);
      }
      else if (!left){              // If you are going right, all the enemy co ordinates increase by 1
        posX.set(i,posX.get(i)+1);
      }
    }
    if(edgePos==-1){                     // If there are no enemies on the screen, the enemies reset
      for (int y = 4; y >= 0; y--) {     // Starts from the bottom row and goes up
        for (int x = 0; x < 11; x++) {
          enemies[4-y][x]=(y+2)/2;       // 4-y gives you the opposite value (higher number means on the top), y+2/2 will give you | 1 | 2 | 3 | based on the y value. Draws the threes on top, then twos, then ones.
        }
      }
      positionY=370;                     // Reseting the x and y positions of the enemies
      posX.clear();
      for (int i = 50; i<600 ;i+=50) {
        posX.add(i);
      }
      bulletSpeed+=200;  // Bullet speed increases so enemeis shoot more frequently
      edgePos=0;
    }
  }

  public int[][] getenemies(){           // Gets the 2D array with enemies
    return enemies;
  }

  public ArrayList<Integer> getposX(){   // Gets the x positions of the enemies
    return posX;
  }

  public int getpositionY(){             // Gets the Y position
    return positionY;
  }

  public void resetEnemies(){            // Resets the Y position
    positionY=370;
  }

  public void setenemies(int[][]newEnemies){ // Updates the enemy array
    enemies=newEnemies;
  }

  public int bulletSpeed(){              // Gets the current bulletSpeed
    return bulletSpeed;
  }

  public int UFOx(){                     // Gets the x position of the ufo
    return ufoPosX;
  }

  public int UFOy(){                     // Gets the y position of the ufo
    return ufoPosY;
  }

  public void ufoOffScreen(){            // Puts the ufo off the screen
    ufoOnScreen=false;
  }

  public void gameOver(Graphics g){ // Once lives = 0 display a "Game Over" screen
    Graphics2D g2d = (Graphics2D) g;
    Font winFont = new Font ("Consolas",Font.PLAIN,150); // Initializing fonts 
    Font scoreFont = new Font ("Consolas", Font.PLAIN, 60);
    g2d.setFont(winFont); // Setting fonts 

    if (Stats.getLives() <= 0){
      Stats.setGameOver(true); // Not letting score change anymore 
      g.drawImage(spaceBackground, 0, 0, null);
      g2d.setColor(Color.green);
      g2d.drawString("GAME OVER!", 100, 340);
      g2d.setFont(scoreFont);
      g2d.setColor(Color.white);
      g2d.drawString("SCORE: " + Stats.getScore(),320, 450); // Displaying the final score 
    }
  }
}



class Bullet{          // Class for the bullets
  private int bx=0;    // bx and by are the x and y positions
  private int by=650;
  private ArrayList<Integer> enemyPts=new ArrayList<Integer>(); // Array for the enemy bullets co ordinates
  private Image bulletPic;       // Bullet Images
  private Image enemyBulletPic;
  private Sound sound=new Sound(); // Makes a sound object

  public Bullet(){
    enemyBulletPic = new ImageIcon("SpaceInvadersIMGS/Bullets/enemybullet.png").getImage(); // Images
    bulletPic=new ImageIcon("SpaceInvadersIMGS/Bullets/shipBullet.png").getImage();
  }

  public void shipBulletshoot(int x){  // Method that shoots the ship bullet
    if(bx==0){     // The x and y co ordinates are set based on where the ship is
      bx=x+22;
    }
    if(by==650){
      sound.shipBulletplay();  // Plays a sound effect
      by-=10;
    }
  }

  public void shipBulletDraw(Graphics g){  // Method that draws the bullet shot by the ship
    if(by<=0){    // If the by goes off the screen from the top, the x and y are reset and the bullet goes away
      by=650;
      bx=0;
    }
    if(by<650){   // The bullet moves down the screen by 10 pixels at a time
      by-=10;
      g.drawImage(bulletPic,bx,by,null); // Draws the bullet
    }
  }

  public void enemyBulletDraw(Graphics g){    // Method that draws the enemies bullets
    if(enemyPts.size()>0){
      for(int i=0; i<enemyPts.size(); i+=2){  // Loops for every value in the enemyPts array
        g.drawImage(enemyBulletPic,enemyPts.get(i),enemyPts.get(i+1),null); // Draws the enemy bullets at the
        enemyPts.set(i+1,enemyPts.get(i+1)+8);// Adds 8 to the y position so the bullet moves
        if(enemyPts.get(i+1)>800){            // Checks to see if the enemy bullet is leaving the screen, if they are they're removed
          enemyPts.remove(i+1);
          enemyPts.remove(i);
          break;
        }
      }
    }
  }

  public int getbx(){   // Gets the x position of the ship Bullet
    return bx;
  }

  public int getby(){   // Gets the y position of the ship Bullet
    return by;
  }

  public void reset(){  // Resets the shipBullet
    by=-1;
  }

  public void addPts(int points){  // Takes in co ordinates and adds them to the enemyPts
    enemyPts.add(points);
  }

  public ArrayList<Integer> getenemyPts(){     // Gets the enemy Bullet's points
    return enemyPts;
  }

  public void enemyBulletreset(int x, int y){  // Removes the points designated in the parameters from the enemyPts
    enemyPts.remove(y);
    enemyPts.remove(x);
  }

  public void enemyBulletClear(){  // Resets all the enemy bullets
    enemyPts.clear();
  }
}



class Shield{   // Class for the shields
  private int[][]shield1=                  // Three 2D arrays store the values for shields. Each index represent a 12 x 12 pixel square. 0 means it's filled, 1 means it's been destryoed
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

  public Shield(){  // Empty constructor
  }

  public void shieldDraw(Graphics g){  // Draws the shields
    g.setColor(new Color(0,255,0));
    for(int y=0; y<5; y++){
      for(int x=0; x<10; x++){ // For every value in the shields, if the value is 0, a green 12 x 12 rect is drawn
        if(shield1[y][x]==0){
          g.fillRect(200+(x*12),500+(y*12),12,12);
        }
        if(shield2[y][x]==0){
          g.fillRect(450+(x*12),500+(y*12),12,12);
        }
        if(shield3[y][x]==0){
          g.fillRect(700+(x*12),500+(y*12),12,12);
        }
      }
    }
  }

  public int[][] getShield1(){  // Gets shield 1
    return shield1;
  }

  public int[][] getShield2(){  // Gets shield 2
    return shield2;
  }

  public int[][] getShield3(){  // // Gets shield 3
    return shield3;
  }

  public void setShield1(int x, int y){ // Sets shield 1
    shield1[y][x]=1;
  }

  public void setShield2(int x, int y){  // Sets shield 2
    shield2[y][x]=1;
  }

  public void setShield3(int x, int y){  // Sets shield 3
    shield3[y][x]=1;
  }
}



class Stats { // Class for the stats 
  private static int score; // Total score accumulated 
  private static int lives; // Number of lives remaining 
  private static boolean gameOver; // If all lives have been lost 
  private Image smallSpaceshipPic; // Picture used for lives 

  public Stats (){ // Constructor method 
    score = 0; // Starting score at 0
    lives = 3; // Giving the user 3 lives 
    gameOver = false; // Starting game over as false 
    smallSpaceshipPic=new ImageIcon("SpaceInvadersIMGS/spaceshipSmall.png").getImage(); // Loading spaceship image
  }
  public void draw (Graphics g){ // Drawing the "Game Over" screen 
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas",Font.PLAIN,32); // Initializing font
    g2d.setFont(font); // Setting font 
    g2d.setColor(Color.white); // Setting colour 
    g2d.drawString("SCORE:",100,50); 
    g2d.setColor(Color.green);
    g2d.drawString(Integer.toString(score),220,50); // Drawing the user's score 
    g2d.setColor(Color.white);
    g2d.drawString("LIVES", 600,50); 
    for(int i=0; i<lives; i++){
      g.drawImage(smallSpaceshipPic,730+(i*70),20,null); // Drawing a spaceship for each life that the user has remaining 
    }
  }
  public void drawLives (Graphics g){ // Method for when the user loses a life 
    Graphics2D g2d = (Graphics2D) g;
    Font font = new Font("Consolas",Font.PLAIN,40);
    g2d.setFont(font);
    g2d.setColor(Color.red);
    g2d.drawString("-1", Ship.position()+20 ,630); // Drawing a red -1 to indicate a life lost 

  }
  public void scoreAdd(int points){ // Adding to the user's score 
    if (!gameOver) { // Only add to score if not on the "Game Over" screen
      score += points;
    }
  }
  public static int getScore(){return score;} // Returns the user's score
  public static int getLives() {return lives;} // Returns how many lives the user has left 
  public static void setLives(){lives-=1;} // Subtracting a life when the user if hit by an enemy
  public static void setGameOver(boolean setGameOver){gameOver = setGameOver;} // Setting gaameOver to true once all lives are lost 
}
