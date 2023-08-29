package com.company;

import entity.Entity;
import entity.Player;
import tile.TileManager;
import tile_interactive.interactiveTile;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;


public class GamePanel extends JPanel implements Runnable {
    //  SCREEN SETTING
    final int originalTileSize =16; // 16X16 tile
    final int scale = 3;
    public  int tileSize = originalTileSize * scale; // 48X48 tile
    public  int maxScreenCol = 20;
    public  int maxScreenRow = 12;
    public  int screenWidth = tileSize*maxScreenCol; //960 pixel
    public  int screenHieght = tileSize*maxScreenRow; // 576 pixel

    //WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;

    //FULL SCREEN
    int screenWidth2= screenWidth;
    int screenHieght2 = screenHieght;
    BufferedImage tempScreen;
    Graphics2D g2;
    public boolean fullScreenOn = false;



    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);


    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Config config = new Config(this);
    Thread gameThread;

    //entity player
    public Player player = new Player(this,keyH);
    // Set players default position

    // creating objects
    public Entity obj[] = new Entity [20];

    //NPC object
    public Entity npc[] = new Entity[10];
    public Entity monster[] = new Entity[20];
    public interactiveTile iTile[] = new interactiveTile[50];
    public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();
    //Game state
    public int playState=1;
    public final int titleState = 0;
    public int gameState;
    public int pauseState=2;
    public int dialogueState = 3;
    public int characterState = 4;
    public int optionState = 5;

    public final int gameOverState = 6;




    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHieght));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setupGame(){

        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setinteractiveTile();

       // playMusic(0);
        gameState = titleState;

        tempScreen = new BufferedImage(screenWidth,screenHieght,BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D)tempScreen.getGraphics(); //whatever will be drawn it will record it
        if(fullScreenOn == true) {
            setFullScreen();
        }
    }

    public void retry(){

        player.setDefaultPositions();
        player.restoreLifeAndMan();
        aSetter.setNPC();
        aSetter.setMonster();

    }
    public void restart(){

        player.setDefaultValues();
        player.setDefaultPositions();
        player.restoreLifeAndMan();
        player.setItems();
        aSetter.setObject();
        aSetter.setNPC();
        aSetter.setMonster();
        aSetter.setinteractiveTile();


    }
    public void setFullScreen(){
        //GET LOCAL SCREEN DEVICE
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(Main.window);

        //GET FUL SCREEN WIDTH AND HEIGHT
        screenHieght2 = Main.window.getHeight();
        screenWidth2= Main.window.getWidth();
    }

    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // delta method to add restriction to allow the movement to be smooth
        double drawInterval = 1000000000 /FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        // diplays fps
        long timer = 0;
        int drawCount = 0;
        while (gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) /drawInterval;
            timer += (currentTime-lastTime);
            lastTime = currentTime;
            if(delta >=1){

                // 1. UPDATE:  update information such as character position
                update();
                // 2. DRAW: draw the screen with the updated information
//                repaint();
                drawToTempScreen();
                drawToScreen();
                delta--;
                drawCount++;
            }
            if (timer >= 1000000000){
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }



        }

    }
    public void update() {
        if (gameState == playState) {
            // for player
            player.update();
            //for NPC
            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    npc[i].update();
                }

            }
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    if (monster[i].alive == true && monster[i].dying == false) {
                        monster[i].update();
                    }
                    if (monster[i].alive == false) {
                        monster[i].checkDrop();
                        monster[i] = null;
                    }

                }
            }
            for (int i = 0; i < projectileList.size(); i++) {
                if (projectileList.get(i) != null) {
                    if (projectileList.get(i).alive == true) {
                        projectileList.get(i).update();
                    }
                    if (projectileList.get(i).alive == false) {
                        projectileList.remove(i);
                    }

                }
            }
            for (int i = 0; i <particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    if(particleList.get(i).alive == true){
                        particleList.get(i).update();
                    }
                    if(particleList.get(i).alive == false){
                        particleList.remove(i);
                    }

                }
            }
            for(int i = 0; i < iTile.length; i++) {
                if(iTile[i] != null) {
                    iTile[i].update();
                }
            }
        }
        if (gameState == pauseState) {
                //nothing
        }

    }
    // temp screen for fullscreen and to resize every objects
    public void drawToTempScreen(){
        //debug
        long drawStart = 0;
        if (keyH.checkDrawTime == true) {
            drawStart = System.nanoTime();
        }
        //TITLE SCREEN
        if (gameState == titleState) {
            ui.draw(g2);
        }
        //others
        else {
            //tile
            tileM.draw(g2);

            //Interactive Tile
            for(int i =0; i < iTile.length; i++) {
                if(iTile[i] !=null) {
                    iTile[i].draw(g2);
                }
            }

            // add entities to the list
            entityList.add(player);

            for (int i = 0; i < npc.length; i++) {
                if (npc[i] != null) {
                    entityList.add(npc[i]);
                }
            }

            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    entityList.add(obj[i]);
                }
            }

            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    entityList.add(monster[i]);
                }
            }
            for (int i = 0; i < projectileList.size(); i++) {
                if (projectileList.get(i) != null) {
                    entityList.add(projectileList.get(i));
                }
            }
            for (int i = 0; i < particleList.size(); i++) {
                if (particleList.get(i) != null) {
                    entityList.add(particleList.get(i));
                }
            }

            // Sort
            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
            });
            // Draw entities
            for (int i = 0; i < entityList.size(); i++) {
                entityList.get(i).draw(g2);
            }

            //empty entity list
            entityList.clear();


            //UI
            ui.draw(g2);

        }
        //debug
        if (keyH.checkDrawTime == true) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("Draw Time: " + passed);
        }
    }
    public void drawToScreen(){
        Graphics g = getGraphics();
        g.drawImage(tempScreen,0,0,screenWidth2,screenHieght2,null);
        g.dispose();

    }


//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//
//        //debug
//        long drawStart = 0;
//        if (keyH.checkDrawTime == true) {
//            drawStart = System.nanoTime();
//        }
//        //TITLE SCREEN
//        if (gameState == titleState) {
//            ui.draw(g2);
//
//        }
//        //others
//        else {
//            //tile
//            tileM.draw(g2);
//
//            //Interactive Tile
//            for(int i =0; i < iTile.length; i++) {
//                if(iTile[i] !=null) {
//                    iTile[i].draw(g2);
//                }
//            }
//
//            // add entities to the list
//            entityList.add(player);
//
//            for (int i = 0; i < npc.length; i++) {
//                if (npc[i] != null) {
//                    entityList.add(npc[i]);
//                }
//            }
//
//            for (int i = 0; i < obj.length; i++) {
//                if (obj[i] != null) {
//                    entityList.add(obj[i]);
//                }
//            }
//
//            for (int i = 0; i < monster.length; i++) {
//                if (monster[i] != null) {
//                    entityList.add(monster[i]);
//                }
//            }
//            for (int i = 0; i < projectileList.size(); i++) {
//                if (projectileList.get(i) != null) {
//                    entityList.add(projectileList.get(i));
//                }
//            }
//            for (int i = 0; i < particleList.size(); i++) {
//                if (particleList.get(i) != null) {
//                    entityList.add(particleList.get(i));
//                }
//            }
//
//            // Sort
//            Collections.sort(entityList, new Comparator<Entity>() {
//                @Override
//                public int compare(Entity e1, Entity e2) {
//                    int result = Integer.compare(e1.worldY, e2.worldY);
//                    return result;
//                }
//            });
//            // Draw entities
//            for (int i = 0; i < entityList.size(); i++) {
//                entityList.get(i).draw(g2);
//            }
//
//            //empty entity list
//            entityList.clear();
//
//
//            //UI
//            ui.draw(g2);
//        }
//        //debug
//        if (keyH.checkDrawTime == true) {
//            long drawEnd = System.nanoTime();
//            long passed = drawEnd - drawStart;
//            g2.setColor(Color.white);
//            g2.drawString("Draw Time: " + passed, 10, 400);
//            System.out.println("Draw Time: " + passed);
//        }
//
//        g2.dispose();
//
//    }
    public void playMusic(int i) {

        music.setFile(i);
        music.play();
        music.loop();
    }
    public void stopMusic() {
        music.stop();
    }
    public void playSE(int i) {
            se.setFile(i);
            se.play();
    }

}
