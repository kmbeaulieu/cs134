package Helpers;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.jogamp.nativewindow.WindowClosingProtocol;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;

import Animation.AnimationData;
import Animation.AnimationDef;
import Animation.FrameDef;
import Background.BackgroundDef;
import Background.BackgroundLayers;
import Background.Tile;
import Character.*;
import Sound.ClipPlayer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GameLoop {

    // Set this to true to make the game loop exit.
    private static boolean shouldExit;
    private static boolean gameStart = true;
    private static int loadScreenTexture;
    private static int[] loadScreenSize = new int[2];
    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];
    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];

    // Texture for the sprite
    private static int yoshiSprite;
    // texture of still yoshi
    private static int yoshiStillSprite;
    // Size of the sprite, this should probably be put somewhere into the
    // Yoshi.java
    private static int[] yoshiSize = new int[2];
    private static int[] yoshiStillSize = new int[2]; // yoshi standing still

    // background stuff
    private static Tile[][] backgroundTrees;
    private static Tile[][] backgroundBushes;
    private static Tile[][] backgroundLevel; //current level loaded
    private static Tile[][] bossLevel; //storage for the level tiles
    private static Tile[][] mainLevel; //storage for the level tiles
    private static int[] tileSize1 = new int[2];

    //pause menu
    private static int[] pauseScreenSize = new int[2];
    private static int pauseScreenTexture;

    static BackgroundDef backgroundMain;
    private static boolean pause = false;
    private static int pauseTime = 100;

    private static boolean inBossRoom = false;

    private static void addCoins(AnimationDef def) {
        AnimationData coin1a = new AnimationData(def);
        AnimationData coin2a = new AnimationData(def);
        AnimationData coin3a = new AnimationData(def);
        AnimationData coin4a = new AnimationData(def);
        AnimationData coin5a = new AnimationData(def);
        AnimationData coin6a = new AnimationData(def);
        AnimationData coin7a = new AnimationData(def);
        AnimationData coin8a = new AnimationData(def);
        AnimationData coin9a = new AnimationData(def);
        AnimationData coin10a = new AnimationData(def);
        AnimationData coin11a = new AnimationData(def);
        AnimationData coin12a = new AnimationData(def);

        Item coin1 = new Item(10, 145, coin1a, 0);
        Item coin2 = new Item(100, 145, coin2a, 0);
        Item coin3 = new Item(200, 145, coin3a, 0);
        Item coin4 = new Item(220, 130, coin4a, 0);
        Item coin5 = new Item(240, 145, coin5a, 0);
        Item coin6 = new Item(300, 120, coin6a, 0);
        Item coin7 = new Item(400, 145, coin7a, 0);
        Item coin8 = new Item(500, 145, coin8a, 0);
        Item coin9 = new Item(600, 145, coin9a, 0);
        //-----
        Item coin10 = new Item(700, 145, coin10a, 0);
        Item coin11 = new Item(800, 145, coin11a, 0);
        Item coin12 = new Item(900, 145, coin12a, 0);

        items.add(coin1);
        items.add(coin2);
        items.add(coin3);
        items.add(coin4);
        items.add(coin5);
        items.add(coin6);
        items.add(coin7);
        items.add(coin8);
        items.add(coin9);
        items.add(coin10);
        items.add(coin11);
        items.add(coin12);

    }

    private static int[] bossShellSize = new int[2];
    private static int[] bossSize1 = new int[2];
    private static int[] bossSize2 = new int[2];
    private static int[] bossSize3 = new int[2];
    private static int[] bossSize4 = new int[2];
    private static int[] bossSize5 = new int[2];
    private static Boss boss;

    private static void loadBoss(AnimationData walk, Camera c) {

        boss = new Boss(150, 130, bossSize1[0], bossSize1[1], walk);
    }
    BackgroundDef BackgroundForeGround;
    BackgroundDef levelTiles;
    // Tile size for Background
    private static ArrayList<Tile> tiles = new ArrayList<Tile>();
    private static int[] tileSize = new int[2];
    // world's x tiles and y tile lengths
    private static int worldXTiles = 40;
    private static int worldYTiles = 14;

    // resolution of the screen for the camera
    private static int camWidth = 200;
    private static int camHeight = 200;

    // animation stuff
    private static AnimationDef yoshiSpace;
    private static AnimationDef yoshiWalk;

    // items
    private static int smallEggSprite;
    private static int[] smallEggSize;

    private static int aimTex;
    private static int[] aimSize = new int[2];
    // private static AnimationDef eggExisting;
    // private static AnimationDef eggThrown;
    // TODO enemies
    private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private static AnimationDef shyguywalkleft;

    //items 
    private static ArrayList<Item> items = new ArrayList<Item>();

    // Sounds
    public static void main(String[] args) {
        GLProfile gl2Profile;

        try {
            // Make sure we have a recent version of OpenGL
            gl2Profile = GLProfile.get(GLProfile.GL2);
        } catch (GLException ex) {
            System.out.println("OpenGL max supported version is too low.");
            System.exit(1);
            return;
        }

        // Create the window and OpenGL context.
        GLWindow window = GLWindow.create(new GLCapabilities(gl2Profile));
        window.setSize(camWidth * 2, camHeight * 2);
        window.setTitle("Walk Yoshi");
        window.setVisible(true);
        window.setDefaultCloseOperation(WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = true;
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.isAutoRepeat()) {
                    return;
                }
                kbState[keyEvent.getKeyCode()] = false;
            }
        });

        // Setup OpenGL state.
        window.getContext().makeCurrent();
        GL2 gl = window.getGL().getGL2();
        gl.glViewport(0, 0, camWidth * 2, camHeight * 2);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, camWidth, camHeight, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Game initialization goes here.
        // ------KEEP LOADING THINGS IN THIS ORDER OR FUN TIMES WILL NOT BE HAD,
        // ADD TO BOTTOM ONLY-----------
        // NOTE: File names start at 1 but the tiles index starts at 0 because
        // arraylist TODO refactor file names for images
        // load the bajillions of background files 1-68 is for the hills, 69 is
        // transparent
        for (int i = 1; i < 70; i++) {
            String filename = BackgroundLayers.FTL + i + BackgroundLayers.FNE;
            // store the tga info into a class called tile so it can store more
            // states later
            tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false));
            // bottom of the trees will be collidable TODO remove this later,
            // this is just for now
            if (i == 68) {
                tiles.get(i - 1).setCollidable(false);
            }

        }
        // 70-104 is bushes, there is a 69 for transparent in this folder too
        for (int i = 70; i < 105; i++) {
            String filename = BackgroundLayers.FBL + i + BackgroundLayers.FNE;
            // store the tga info into a class called tile so it can store more
            // states later
            tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false));

        }
        // 105 is the blue sky, it's 101x101 TODO make this the full length of
        // the bgtrees array
        tiles.add(new Tile("sky", glTexImageTGAFile(gl, "res\\backgrounds\\colortiles\\sky.tga", tileSize), tileSize,
                false));
        Tile[][] skybackground = new Tile[101][101];
        for (int y = 0; y < 101; y++) {
            for (int x = 0; x < 101; x++) {
                skybackground[y][x] = tiles.get(104);
            }
        }
        // 106-123 are collideable, they are the level floor
        for (int i = 106; i < 283; i++) {
            tileSize = new int[2];
            String filename = BackgroundLayers.FLL + i + BackgroundLayers.FNE;
            // custom non collideable
            if ((i >= 106 && i <= 111) || i == 125 || i == 130 || i == 135
                    || i == 136 || i == 140 || i == 141 || i == 142 || i == 145 || i == 146
                    || i == 151 || i == 152 || i == 153 || i == 200 || i == 201 || i == 197 || i == 193 || i == 188 || i == 183
                    || i == 178 || i == 173 || i == 168 || i == 167 || i == 163 || i == 162 || i == 151 || i == 160 || i == 159 || i == 279 || i == 280 || i == 281 || i == 282) {
                Tile t;

                t = new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false);

                tiles.add(t);
                //  t.isGrass = true;
            } else {
                Tile t;

                t = new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, true);

                tiles.add(t);
            }

        }

        //load pause screen texture
        pauseScreenTexture = glTexImageTGAFile(gl, "res\\backgrounds\\pauseScreen.tga", pauseScreenSize);
        loadScreenTexture = glTexImageTGAFile(gl, "res\\backgrounds\\title\\yoshisisland.tga", loadScreenSize);
        // ----------THIS IS THE BOTTOM OF LOADING BACKGROUND IMAGES, ADD MORE
        // RIGHT ABOVE ME----------------
        // -----------SOUND LOADING-----------
        ClipPlayer cp = new ClipPlayer();
        Clip jumpClip = null;
        Clip tongueClip = null;
        Clip deathClip = null;
        Clip pauseClip = null;
        Clip eggHit = null;
        Clip coinClip = null;
        Clip doorClip = null;
//        Clip flutter = null;
//        Clip bopEnemyClip = null;
//        Clip grassStepClip = null;
        try {
            tongueClip = cp.loadClip("res\\sounds\\Lick.wav");
            pauseClip = cp.loadClip("res\\sounds\\pause.wav");
            jumpClip = cp.loadClip("res\\sounds\\ha!.wav");
            coinClip = cp.loadClip("res\\sounds\\Coin.wav");
            doorClip = cp.loadClip("res\\sounds\\Durp.wav");

//            flutter = cp.loadClip("res\\sounds\\hmmmph.wav");
//            grassStepClip = cp.loadClip("res\\sounds\\Step Grass.wav");
            eggHit = cp.loadClip("res\\sounds\\Egg Ricochet.wav");
//            bopEnemyClip = cp.loadClip("res\\sounds\\hammer.mp3");
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
        }
        // -----------END SOUND LOADING-----------
        // load the tile formation for the backgrounds with hills/trees
        loadbg();

        // camera setup
        Camera c = new Camera(0, 0);
        c.getAABB().setH(camWidth);
        c.getAABB().setW(camHeight);
        AABB tileAABB;

        // yoshi player -- 26 x 32
        FrameDef[] yoshiStillFrame = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshi1.tga", yoshiSize), yoshiSize, 1000f)};
        AnimationDef stillYoshi = new AnimationDef("stillyoshi", yoshiStillFrame);
        AnimationData stillYoshiData = new AnimationData(stillYoshi);
        aimTex = glTexImageTGAFile(gl, "res\\sprites\\eggs\\aim.tga", aimSize);

        YoshiData yoshi = new YoshiData(0, 100, yoshiSize[0], yoshiSize[1], stillYoshiData, aimTex, aimSize);
        yoshi.getAABB().setW(yoshiSize[0]); // this should set yoshi's AABB
        // width to the width of its sprite
        yoshi.getAABB().setH(yoshiSize[1]); // this should set yoshi's AABB
        // height to the height of its
        // sprite

        // TODO get cloud background working, need a background anim data and
        // such
        // int[] cloudSize = new int[2];
        // FrameDef[] cloudTiles = new FrameDef[]{
        // new FrameDef(glTexImageTGAFile(gl,
        // "backgrounds\\cloudanim\\small\\1.tga", cloudSize),cloudSize,400f),
        // new FrameDef(glTexImageTGAFile(gl,
        // "backgrounds\\cloudanim\\small\\2.tga", cloudSize),cloudSize,350f),
        // new FrameDef(glTexImageTGAFile(gl,
        // "backgrounds\\cloudanim\\small\\3.tga", cloudSize),cloudSize,400f),
        // new FrameDef(glTexImageTGAFile(gl,
        // "backgrounds\\cloudanim\\small\\4.tga", cloudSize),cloudSize,330f)
        // };
        // AnimationDef cloudbg = new
        // AnimationDef("floatycloudsmall",cloudTiles);
        // AnimationData cloudanim = new AnimationData(cloudbg);
        // set up the animation for pressing space for yoshi (stick tongue out),
        // the timing is staggered to make it look better
        int[] sizeForFrame1, sizeForFrame2, sizeForFrame3, sizeForFrame4, sizeForFrame5, sizeForFrame6;
        sizeForFrame1 = sizeForFrame2 = sizeForFrame3 = sizeForFrame4 = sizeForFrame5 = sizeForFrame6 = new int[2]; // each

        FrameDef[] yoshiSpaceFrames = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1),
            sizeForFrame1, 10f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2),
            sizeForFrame2, 20f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3),
            sizeForFrame3, 30f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4),
            sizeForFrame4, 40f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5),
            sizeForFrame5, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace6.tga", sizeForFrame6),
            sizeForFrame6, 60f), // tongue back in
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5),
            sizeForFrame5, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4),
            sizeForFrame4, 40f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3),
            sizeForFrame3, 30f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2),
            sizeForFrame2, 20f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1),
            sizeForFrame1, 10f)};
        yoshiSpace = new AnimationDef("yoshiSpace", yoshiSpaceFrames);
        AnimationData yoshiSpaceData = new AnimationData(yoshiSpace);

        //yoshi throwing animation
        int[] throwLeftSize = new int[2];
        int[] throwRightSize = new int[2];
        FrameDef yoshiThrowleftFrame[] = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\throw\\yoshiThrowLeft.tga", throwLeftSize), throwLeftSize, 20f)};
        FrameDef yoshiThrowRightFrame[] = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\throw\\yoshiThrowRight.tga", throwRightSize), throwLeftSize, 20f)};
        AnimationDef yoshiThrowleftAnimation = new AnimationDef("yoshiThrowLeft", yoshiThrowleftFrame);
        AnimationDef yoshiThrowRightAnimation = new AnimationDef("yoshiThrowRight", yoshiThrowRightFrame);
        AnimationData yoshiThrowLeftData = new AnimationData(yoshiThrowleftAnimation);
        AnimationData yoshiThrowRightData = new AnimationData(yoshiThrowRightAnimation);

        // set up walking (right) animation TODO refactor name
        int[] walkingAnimSize = new int[2];
        FrameDef[] yoshiWalkFrames = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\1.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\2.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\3.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\4.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\5.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\6.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\7.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\8.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\9.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\10.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\11.tga", walkingAnimSize), walkingAnimSize,
            50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalk\\12.tga", walkingAnimSize), walkingAnimSize,
            50f),};
        yoshiWalk = new AnimationDef("yoshiwalk", yoshiWalkFrames);
        AnimationData yoshiWalkingData = new AnimationData(yoshiWalk);

        // yoshi walking left animation setup
        FrameDef[] yoshiWalkLeftFrames = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\1.tga", walkingAnimSize),
            walkingAnimSize, 10f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\2.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\3.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\4.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\5.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\6.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\7.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\8.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\9.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\10.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\11.tga", walkingAnimSize),
            walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshiwalkleft\\12.tga", walkingAnimSize),
            walkingAnimSize, 50f),};
        AnimationDef yoshiWalkLeft = new AnimationDef("yoshiWalkLeft", yoshiWalkLeftFrames);
        AnimationData yoshiWalkingLeftData = new AnimationData(yoshiWalkLeft);

        int[] yoshiDieAnimSize1, yoshiDieAnimSize2, yoshiDieAnimSize3, yoshiDieAnimSize4, yoshiDieAnimSize5,
                yoshiDieAnimSize6, yoshiDieAnimSize7, yoshiDieAnimSize8;//
        yoshiDieAnimSize8 = new int[2];
        yoshiDieAnimSize7 = new int[2];
        yoshiDieAnimSize6 = new int[2];
        yoshiDieAnimSize5 = new int[2];
        yoshiDieAnimSize4 = new int[2];
        yoshiDieAnimSize3 = new int[2];
        yoshiDieAnimSize2 = new int[2];
        yoshiDieAnimSize1 = new int[2];

        FrameDef[] yoshiDieFrames = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\1.tga", yoshiDieAnimSize1),
            yoshiDieAnimSize1, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\2.tga", yoshiDieAnimSize2),
            yoshiDieAnimSize2, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\3.tga", yoshiDieAnimSize3),
            yoshiDieAnimSize3, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\4.tga", yoshiDieAnimSize4),
            yoshiDieAnimSize4, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\5.tga", yoshiDieAnimSize5),
            yoshiDieAnimSize5, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\6.tga", yoshiDieAnimSize6),
            yoshiDieAnimSize6, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\7.tga", yoshiDieAnimSize7),
            yoshiDieAnimSize7, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\yoshidie\\8.tga", yoshiDieAnimSize8),
            yoshiDieAnimSize8, 150f),};
        AnimationDef yoshiDie = new AnimationDef("yoshidie", yoshiDieFrames);
        AnimationData yoshiDieData = new AnimationData(yoshiDie);

        // egg sprites
        smallEggSize = new int[2];
        smallEggSprite = glTexImageTGAFile(gl, "res\\sprites\\eggs\\small\\smalleggfat.tga", smallEggSize);
        // FrameDef[] bigEggFrames = new FrameDef[]{new
        // FrameDef(glTexImageTGAFile(gl, "sprites//eggs//big//bigeggfat.tga",
        // bigEggSize), bigEggSize, 500f),
        // new FrameDef(glTexImageTGAFile(gl,
        // "sprites//eggs//big//bigeggtall.tga", bigEggSize), bigEggSize,
        // 500f)};
        // AnimationDef bigEggAnimDef = new AnimationDef("bigegganimation",
        // bigEggFrames);
        // AnimationData bigEggAnimData = new AnimationData(bigEggAnimDef);

        // //make an enemy
        int[] shyguySize = new int[2];
        int[] shyguy2Size;
        //
        FrameDef[] shyGuyWalkingFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\enemies\\shyguy\\walk\\1.tga", shyguySize), shyguySize, 150f)};
        shyguy2Size = shyguySize;
        AnimationDef shyGuyWalk = new AnimationDef("walking", shyGuyWalkingFrames);
        AnimationData shyGuyWalkingData = new AnimationData(shyGuyWalk);
        Enemy shyguy = new Enemy(130, 145, shyguySize[0], shyguySize[1], shyGuyWalkingData, true);
        shyguy.getAABB().setW(shyguySize[0]);
        shyguy.getAABB().setH(shyguySize[1]);

        AnimationData shyGuy2WalkingData = new AnimationData(shyGuyWalk);
        Enemy shyguy2 = new Enemy(270, 130, shyguy2Size[0], shyguy2Size[1], shyGuy2WalkingData, true);
        shyguy2.getAABB().setW(shyguy2Size[0]);
        shyguy2.getAABB().setH(shyguy2Size[1]);

        enemies.add(shyguy);
        enemies.add(shyguy2);

        int[] pewpewGuySize = new int[2];
        FrameDef[] pewpewGuyShootFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\enemies\\pewpewguy\\1.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\enemies\\pewpewguy\\2.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\enemies\\pewpewguy\\3.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\enemies\\pewpewguy\\4.tga", pewpewGuySize), pewpewGuySize, 150f)
        };
        AnimationDef pewpewShootDef = new AnimationDef("pew", pewpewGuyShootFrames);
        AnimationData pewpewData = new AnimationData(pewpewShootDef);
        Enemy pewpew = new Enemy(250, 145, pewpewGuySize[0], pewpewGuySize[1], pewpewData, true);
        int[] pewSize = new int[2];
        int pewTex = glTexImageTGAFile(gl, "res\\sprites\\enemies\\pewpewguy\\pew.tga", pewSize);
        pewpew.setProjectileInfo(pewTex, pewSize);
        pewpew.canShoot = true;
        shyguy.setProjectileInfo(pewTex, pewSize);
        enemies.add(pewpew);
        FrameDef bossShell[] = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\bossShell.tga", bossShellSize), bossShellSize, 1000f)};
        FrameDef bossWalkFrames[] = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\boss1.tga", bossSize1), bossSize1, 120f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\boss2.tga", bossSize2), bossSize2, 120f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\boss3.tga", bossSize3), bossSize3, 120f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\boss4.tga", bossSize4), bossSize4, 120),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\boss\\boss5.tga", bossSize5), bossSize5, 120f),};
        AnimationDef shellMove = new AnimationDef("shellMove", bossShell);
        AnimationDef bossWalkDef = new AnimationDef("bossWalk", bossWalkFrames);
        AnimationData bossShellMove = new AnimationData(shellMove);
        AnimationData bossWalkMove = new AnimationData(bossWalkDef);
        //------items----------
        int[] coinSize = new int[2];
        FrameDef[] coinFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\coin\\coin1.tga", coinSize), coinSize, 100f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\coin\\coin2.tga", coinSize), coinSize, 100f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\coin\\coin3.tga", coinSize), coinSize, 100f),
            new FrameDef(glTexImageTGAFile(gl, "res\\sprites\\coin\\coin4.tga", coinSize), coinSize, 100f),};
        AnimationDef coinSpin = new AnimationDef("coin", coinFrames);
        addCoins(coinSpin);
        int[] doorSize = new int[2];
        FrameDef[] doorFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "res\\bosshut.tga", doorSize), doorSize, 100f)};
        AnimationDef doorAnimDef = new AnimationDef("door", doorFrames);
        AnimationData doorAnimData = new AnimationData(doorAnimDef);
        Item door = new Item(545, 110, doorAnimData, 1);
        door.itemBox.setW(doorSize[0]);
        door.itemBox.setH(doorSize[1]);
        items.add(door);
        // -----------------------BEGIN Physics Setup----------------------------
        // Physics runs at 100fps, or 10ms / physics frame
        int physicsDeltaMS = 10;
        long lastPhysicsFrameMS;

        // -----------------------END Physics Setup----------------------------
        // The game loop
        long lastFrameNS;
        long curFrameNS = System.nanoTime();
        lastPhysicsFrameMS = System.nanoTime() / 1000000;

        long curFrameMS;
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
            lastFrameNS = curFrameNS;
            curFrameNS = System.nanoTime();
            curFrameMS = curFrameNS / 1000000;
            long deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
            // Actually, this runs the entire OS message pump.
            window.display();
            if (!window.isVisible()) {
                shouldExit = true;
                break;
            }
            // ------------DO physics-----------------------------------------
            do {
                // 1. character movement
                if (yoshi.isGrounded()) {
                    yoshi.yvelocity = 0;
                }
//                 enemy movement
                for (Enemy e : enemies) {
                    if (e.isGrounded()) {
                        e.yvelocity = 0;
                    }
                }
                if (inBossRoom) {
                    if (boss.isGrounded()) {
                        boss.yvelocity = 0;
                    }
                }
//yoshi flutter jump
                if (yoshi.isGrounded() && yoshi.isJumping() && !yoshi.isDead()) {
                    cp.playClip(jumpClip);
                    yoshi.yvelocity = yoshi.jumpvel;
                    yoshi.setGrounded(false);
                    // cp.playClip(jumpClip);
                }
                // if can flutter,pressing jump, and yoshi is falling then
                // flutter jump....needto do something with xvel?
                if (yoshi.canFlutter() && yoshi.isJumping() && yoshi.yvelocity > 0.2) {
                    //cp.playClip(flutter);
                    yoshi.yvelocity = -.2;
                    yoshi.setFlutter(false);
                    yoshi.setJump(false);
                }

                // update yvel of all the sprites
                for (Enemy e : enemies) {
                    e.yvelocity = e.yvelocity + e.gravity * physicsDeltaMS;
                }
                yoshi.yvelocity = yoshi.yvelocity + yoshi.gravity * physicsDeltaMS;
                if (inBossRoom) {
                    boss.yvelocity = boss.yvelocity + boss.gravity * physicsDeltaMS;
                }
                //set y for all sprites
                for (Enemy e : enemies) {
                    e.setY((e.getY() + e.yvelocity * physicsDeltaMS));
                }
                yoshi.setY((yoshi.getY() + yoshi.yvelocity * physicsDeltaMS));
                if (inBossRoom) {
                    if (!boss.isDead()) {
                        boss.setY(boss.getY() + boss.yvelocity * physicsDeltaMS);
                    }
                }
                // enemy projectile movement
                for (Enemy e : enemies) {
                    if (!e.projectiles.isEmpty()) {
                        for (Projectile p : e.projectiles) {
                            // for now no gravity for enemy projectiles
                            p.setYVel(p.getYVel() + (p.getGravity() * physicsDeltaMS));
                            p.setX(p.getX() + (p.getSpeed() * physicsDeltaMS));
                            p.setY((p.getY() + p.getYVel() * physicsDeltaMS));
                        }
                    }
                }
                // yoshi's projectile movement
                if (!yoshi.projectiles.isEmpty()) {
                    for (Projectile p : yoshi.projectiles) {
                        p.setYVel(p.getYVel() - (p.getGravity() * physicsDeltaMS));
                        p.setX(p.getX() + p.dirX * (p.getXVel() * physicsDeltaMS));
                        p.setY((p.getY() + p.dirY * (p.getYVel() * physicsDeltaMS)));
                    }
                }
                // //2. bg collision detection/res RESOLVE X THEN Y
                int tileStartX = (int) Math.floor(c.getX() / tileSize[0]);
                int tileStartY = (int) Math.floor(c.getY() / tileSize[0]);
                int tileEndX = (int) Math.floor((c.getX() + camWidth) / tileSize[0]);
                int tileEndY = (int) Math.floor((c.getY() + camHeight) / tileSize[1]);
                // EVERYONE FALLS off the cliffs
                yoshi.setGrounded(false);
//                for (Enemy e : enemies) {
//                    e.setGrounded(false);
//                }
//                pewpew.setGrounded(false);
//                shyguy.setGrounded(false);
//                shyguy2.setGrounded(false);
                // TILE COLLISSION CHECK/RESOLUTION

                //calculate tiles around the player (with checks if against the edge of the world)
                int tileYoshiStartX = (int) Math.floor(yoshi.getX() / tileSize[0]);
                tileYoshiStartX = Math.max(tileYoshiStartX, 0);
                int tileYoshiEndX = (int) Math.floor((yoshi.getX() + yoshi.getW()) / tileSize[0]);
                tileYoshiEndX = Math.min(worldXTiles, tileYoshiEndX);
                int tileYoshiStartY = (int) Math.floor(yoshi.getY() / tileSize[1]);
                tileYoshiStartY = Math.max(tileYoshiStartY, 0);
                int tileYoshiEndY = (int) Math.floor((yoshi.getY() + yoshi.getH()) / tileSize[1]);
                tileYoshiEndY = Math.max(tileYoshiEndY, worldYTiles);

                //    System.out.println("startx " + tileYoshiStartX + " endx " + tileYoshiEndX + " starty " + tileYoshiStartY + " tileYoshiEndY " + tileYoshiEndY);
                for (int y = tileYoshiStartY; y < tileYoshiEndY; y++) {
                    for (int x = tileYoshiStartX; x < tileYoshiEndX; x++) {
                        tileAABB = new AABB(x * tileSize[0], y * tileSize[1], tileSize[0], tileSize[1]);

                        if (backgroundLevel[y][x].collidable) {
                            // ths doesn't work TODO fix, wont let yoshi move
                            // left/right and if jump will warp him back to the
                            // starting tile it checked

                            if (!inBossRoom) {
                                //yoshi intersection
                                if (AABB.AABBIntersectAbove(yoshi.getAABB(), tileAABB, yoshi.yvelocity)) {

//                                if(yoshi.getX()>32 ||yoshi.getY<288)
                                    double fix = (yoshi.getY() + (double) yoshi.getCurrentAnimation().getcurFrameSize()[1]) - tileAABB.getY();
                                    yoshi.setY(yoshi.getY() - fix);
                                    yoshi.setGrounded(true);
                                    yoshi.setFlutter(true);
                                    //	cp.stopClip(flutter);
                                    //}
                                }
                            } else {
//                                if (yoshi.getX()) {
//
//                                }

                                if (AABB.AABBIntersectAbove(yoshi.getAABB(), tileAABB, yoshi.yvelocity)) {

//                                if(yoshi.getX()>32 ||yoshi.getY<288)
                                    double fix = (yoshi.getY() + (double) yoshi.getCurrentAnimation().getcurFrameSize()[1]) - tileAABB.getY();
                                    yoshi.setY(yoshi.getY() - fix);
                                    yoshi.setGrounded(true);
                                    yoshi.setFlutter(true);
                                    //	cp.stopClip(flutter);
                                    //}
                                }
                            }
                            // yoshi's projectiles hits a tile
                            if (!yoshi.projectiles.isEmpty()) {
                                for (int i = 0; i < yoshi.projectiles.size(); i++) {
                                    if (AABB.AABBIntersect(tileAABB, yoshi.projectiles.get(i).getAABB())) {
                                        yoshi.projectiles.remove(i);
                                        if (yoshi.projectiles.isEmpty()) {
                                            break;
                                        }
                                    }
                                }
                            }
                            // enemies projectiles  hit a collidable tile
                            for (Enemy e : enemies) {
                                if (!e.projectiles.isEmpty()) {
                                    for (int i = 0; i < e.projectiles.size(); i++) {
                                        if (AABB.AABBIntersect(tileAABB,
                                                e.projectiles.get(i).getAABB())) {
                                            e.projectiles.remove(i);
                                            if (e.projectiles.isEmpty()) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (Item i : items) {
                    if (AABB.AABBIntersect(yoshi.getAABB(), i.getItemBox())) {
                        if (i.type == 0) {
                            yoshi.score++;
                            cp.playClip(coinClip);
                            items.remove(i);
                            break;
                        } else if (i.type == 1) {
                            if (kbState[KeyEvent.VK_W]) {
                                if (!cp.isPlaying(doorClip)) {
                                    cp.playClip(doorClip);
                                }
                                inBossRoom = true;
                                backgroundLevel = bossLevel;
                                enemies.clear();
                                items.clear();
                                loadBoss(bossWalkMove, c);
                                c.setX(0);
                                c.setY(32);
                                yoshi.setX(108);
                                yoshi.setY(134);
                                yoshi.setJump(false);
                                break;
                            }
                        }
                    }
                }
                for (Enemy e : enemies) {
                    int estartx = (int) Math.floor(e.getX() / tileSize[0]);
                    estartx = Math.max(estartx, 0);
                    int eendx = (int) Math.floor((e.getX() + e.getW()) / tileSize[0]);
                    eendx = Math.min(worldXTiles, eendx);
                    int estarty = (int) Math.floor(e.getY() / tileSize[1]);
                    estarty = Math.max(estarty, 0);
                    int eendy = (int) Math.floor((e.getY() + e.getH()) / tileSize[1]);
                    eendy = Math.max(eendy, worldYTiles);
                    for (int x = estartx; x < eendx; x++) {
                        for (int y = estarty; y < eendy; y++) {
                            //enemies intersecting with the ground
                            if (backgroundLevel[y][x].collidable) {
                                tileAABB = new AABB(x * tileSize[0], y * tileSize[1], tileSize[0], tileSize[1]);
                                if (AABB.AABBIntersectAbove(tileAABB, e.getAABB(), e.yvelocity)) {
                                    double fix = (e.getY() + (double) e.getCurrentAnimation().getcurFrameSize()[1]) - tileAABB.getY();
                                    // System.out.println("FIX y+h " + yoshi.getY()+" + "+yoshi.getCurrentAnimation().getcurFrameSize()[1] + " tileY " + tileAABB.getY());
                                    e.setY(e.getY() - fix);
                                    e.setGrounded(true);
                                }
                            }

                        }
                    }
                }
                if (inBossRoom) {
                    if (!boss.isDead()) {
                        int bossStartx = (int) Math.floor(boss.getX() / tileSize[0]);
                        bossStartx = Math.max(bossStartx, 0);
                        int bossendx = (int) Math.floor((boss.getX() + boss.getW()) / tileSize[0]);
                        bossendx = Math.min(worldXTiles, bossendx);
                        int bossstarty = (int) Math.floor(boss.getY() / tileSize[1]);
                        bossstarty = Math.max(bossstarty, 0);
                        int bossendy = (int) Math.floor((boss.getY() + boss.getH()) / tileSize[1]);
                        bossendy = Math.max(bossendy, worldYTiles);
                        for (int x = bossStartx; x < bossendx; x++) {
                            for (int y = bossstarty; y < bossendy; y++) {
                                //enemies intersecting with the ground
                                if (backgroundLevel[y][x].collidable) {
                                    tileAABB = new AABB(x * tileSize[0], y * tileSize[1], tileSize[0], tileSize[1]);
                                    if (AABB.AABBIntersectAbove(tileAABB, boss.getAABB(), boss.yvelocity)) {
                                        double fix = (boss.getY() + (double) boss.getCurrentAnimation().getcurFrameSize()[1]) - tileAABB.getY();
                                        // System.out.println("FIX y+h " + yoshi.getY()+" + "+yoshi.getCurrentAnimation().getcurFrameSize()[1] + " tileY " + tileAABB.getY());
                                        boss.setY(boss.getY() - fix);
                                        boss.setGrounded(true);
                                    }
                                }

                            }
                        }

                    }
                }
                // yoshi projectiles disappear when leaving the screen
                if (!yoshi.projectiles.isEmpty()) {
                    for (int i = 0; i < yoshi.projectiles.size(); i++) {
                        if (!yoshi.projectiles.isEmpty()
                                && !AABB.AABBIntersect(c.getAABB(), yoshi.projectiles.get(i).getAABB())) {
                            yoshi.projectiles.remove(i);
                            if (yoshi.projectiles.isEmpty()) {
                                break;
                            }
                        }
                        // if yoshi's egg hits an enemy, kill it
                        for (Enemy e : enemies) {
                            if (AABB.AABBIntersect(e.getAABB(), yoshi.projectiles.get(i).getAABB())) {
                                cp.playClip(eggHit);
                                yoshi.projectiles.remove(i);
                                enemies.remove(e);

                                if (yoshi.projectiles.isEmpty() || enemies.isEmpty()) {
                                    break;
                                }
                            }
                        }
                        if (inBossRoom) {

                            if ((AABB.AABBIntersect(yoshi.projectiles.get(i).getAABB(), boss.getAABB()))) {
                                cp.playClip(eggHit);
                                boss.setHealth(boss.getHealth() - yoshi.projectiles.get(i).getDamage());

                                if (boss.getHealth() <= 0) {
                                    boss.setDeath(true);
                                    boss.box = null;
                                }
                                yoshi.projectiles.remove(i);
                                if (yoshi.projectiles.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }
                }

                // enemy/player collision/resolution
                for (Enemy e : enemies) {
                    if (AABB.AABBIntersect(yoshi.getAABB(), e.getAABB())) {
                        if (yoshi.getCurrentAnimation().def.name.equals("tongue")) {

                        } else {//act as normal
                            if (e.canJumpOn()) {
                                if (yoshi.yvelocity > yoshi.gravity) {
                                    yoshi.yvelocity = -.2;
                                    yoshi.setGrounded(false);
                                    enemies.remove(e);
                                    if (enemies.isEmpty()) {
                                        break;
                                    }
                                    break;
                                }
                            } else {
                                yoshi.setGrounded(true);
                                yoshi.setDeath(true);
                            }
                        }
                    }
                }
//player boss collision
                if (inBossRoom) {
                    if (!boss.isDead()) {
                        if (AABB.AABBIntersect(yoshi.getAABB(), boss.getAABB())) {
                            yoshi.setDeath(true);
                            yoshi.setGrounded(true);
                        }
                    }

                }
                lastPhysicsFrameMS += physicsDeltaMS;
            } while (lastPhysicsFrameMS + physicsDeltaMS < curFrameMS);
            // TODO cloudanim.update(deltaTimeMS);
            // ------------DO Normal update-----------------------------------------

            //try to attack but if not then walk
            if (inBossRoom) {
                if (!boss.isDead()) {
                    boss.attackTimer -= deltaTimeMS;
                    if (boss.attackTimer <= 0) {
                        boss.attackTimer = 6000;
                        boss.setCurrentAnimation(bossShellMove);
                    }
                    if ("bossWalk".equals(boss.getCurrentAnimation().def.name)) {
                        boss.walk(c);

                    } else if ("shellMove".equals(boss.getCurrentAnimation().def.name)) {
                        boss.doingattackTimer -= deltaTimeMS;
                        if (boss.doingattackTimer <= 0) {
                            boss.doingattackTimer = 3000;
                            boss.setCurrentAnimation(bossWalkMove);
                            boss.move(c);
                        }
                    }
                    boss.update(deltaTimeMS);
                }
            }
            if (yoshi.isTongueOut()) {
                yoshi.setCurrentAnimation(yoshiSpaceData);
                yoshi.update(deltaTimeMS);

                if (yoshiSpaceData.getCurFrame() == yoshiSpaceData.getMaxFrame()) {
                    // you are at the max frame so stop the animation
                    yoshi.setTongueOut(false);
                    yoshi.setCurrentAnimation(stillYoshiData);
                    yoshi.update(deltaTimeMS);
                }
            }
            if (yoshi.isDead()) {
                yoshi.setGrounded(true);
                yoshi.setCurrentAnimation(yoshiDieData);
                yoshi.update(deltaTimeMS);
                cp.playClip(deathClip);
                if (yoshi.getCurrentAnimation().getCurFrame() == yoshi.getCurrentAnimation().getMaxFrame()) {
                    yoshiDieData.resetAnimation();
                    // reset player and cam
                    yoshi.setX(30);
                    yoshi.setY(144);
                    yoshi.setCurrentAnimation(stillYoshiData);
                    yoshi.setDeath(false);
                    yoshi.update(deltaTimeMS);
                    yoshi.setGrounded(false);
                    yoshi.setJump(false);
                    yoshi.isAiming = false;
                    yoshi.numEggs = 5;
                    c.setX(0);
                    c.setY(0);
                    backgroundLevel = mainLevel;
                    inBossRoom = false;
                    gameStart = true;

                    enemies.clear();

                    enemies.add(shyguy);
                    shyguy.setX(130);
                    shyguy.setY(145);
                    shyguy.setHealth(10);
                    shyguy.setGrounded(false);

                    enemies.add(shyguy2);
                    shyguy2.setX(270);
                    shyguy2.setY(130);
                    shyguy2.setHealth(10);
                    shyguy2.setGrounded(false);
                    enemies.add(pewpew);
                    pewpew.setX(100);
                    pewpew.setY(100);
                    pewpew.setHealth(10);
                    pewpew.setGrounded(false);
                    c.setX(0);
                    c.setY(0);

                    items.clear();
                    addCoins(coinSpin);
                    items.add(door);

                }

            }

            if (yoshi.isAiming) {
                //make the aim thing appear
                yoshi.update(deltaTimeMS);
            }

            yoshi.setPrevX(yoshi.getX());
            yoshi.setPrevY(yoshi.getY());
            // ------------DO game slogic-----------------------------------------
            if (kbState[KeyEvent.VK_ESCAPE]) {
                shouldExit = true;
            }

            for (Item i : items) {
                i.update(deltaTimeMS);
            }
            if (!yoshi.isDead()) {

                if (!yoshi.isAiming) {
                    if (kbState[KeyEvent.VK_A]) {
                        gameStart = false;

                        // if not at the left edge of the world then move left
                        if (!inBossRoom) {
                            if (yoshi.getX() - c.getSpeed() >= 0) {
                                yoshi.moveYoshiLeft();
                            }
                            int cHalf = (c.getX() + camWidth) / 2;

                            if (c.getX() - yoshi.getSpeed() > 0) {
                                c.setX(c.getX() - yoshi.getSpeed());
                            } else {
                                c.setX(0);
                            }
                        } else {
                            if (yoshi.getX() - c.getSpeed() >= 32) {
                                yoshi.moveYoshiLeft();
                            }
                            int cHalf = (c.getX() + camWidth) / 2;

                            if (c.getX() - yoshi.getSpeed() > 0) {
                                c.setX(c.getX() - yoshi.getSpeed());
                            } else {
                                c.setX(0);
                            }
                        }
                        yoshi.setCurrentAnimation(yoshiWalkingLeftData);
                        yoshi.update(deltaTimeMS);
                    }

                    // go right, continue animation even if at the end
                    if (kbState[KeyEvent.VK_D]) {
                        // dont move if you are at the end of the world size
                        gameStart = false;

                        // if yoshi is within the world, move right
                        if (!inBossRoom) {
                            if (yoshi.getX() + yoshi.getSpeed() <= worldXTiles * tileSize[0] - yoshiSize[0]) {
                                yoshi.moveYoshiRight();

                                // if yoshi is beyond the middle of the screen
                                int cHalf = (c.getX() + camWidth) / 2;
                                if (cHalf < yoshi.getX()) {
                                    // if moving the camera is within the world, move
                                    // the camera, else stop at the end
                                    if (c.getX() + yoshi.getSpeed() + camWidth < worldXTiles * tileSize[0]) {
                                        c.setX(c.getX() + yoshi.getSpeed());
                                    } else {
                                        c.setX(worldXTiles * tileSize[0] - camWidth);
                                    }
                                }

                            }
                        } else {
                            if (yoshi.getX() + yoshi.getSpeed() <= 18 * tileSize[0] - yoshiSize[0]) {
                                yoshi.moveYoshiRight();

                                // if yoshi is beyond the middle of the screen
                                int cHalf = (c.getX() + camWidth) / 2;
                                if (cHalf < yoshi.getX()) {
                                    // if moving the camera is within the world, move
                                    // the camera, else stop at the end
                                    if (c.getX() + yoshi.getSpeed() + camWidth < 20 * tileSize[0]) {
                                        c.setX(c.getX() + yoshi.getSpeed());
                                    } else {
                                        c.setX(20 * tileSize[0] - camWidth);
                                    }
                                }

                            }
                        }
                        // dont move yoshi but still do walk animation
                        yoshi.setCurrentAnimation(yoshiWalkingData);
                        yoshi.update(deltaTimeMS);
                    }

                    if (kbState[KeyEvent.VK_W]) {
                        gameStart = false;

                        yoshi.setJump(true);
                    } else {
                        yoshi.setJump(false);
                    }
                }
                // decrease timer
                yoshi.projectileTimer -= deltaTimeMS;
                if (kbState[KeyEvent.VK_E]) {
                    // throw
                    gameStart = false;

                    if (!kbPrevState[KeyEvent.VK_E] && yoshi.numEggs > 0 && !yoshi.isAiming) {
                        yoshi.isAiming = true;
                        System.out.println("is aiming " + yoshi.isAiming);
                        //start the aiming process
                        if (yoshi.isGoingLeft()) {
                            yoshi.setCurrentAnimation(yoshiThrowLeftData);
                        } else if (yoshi.isGoingRight()) {
                            yoshi.setCurrentAnimation(yoshiThrowRightData);
                        }
                    } else if (!kbPrevState[KeyEvent.VK_E] && yoshi.isAiming && yoshi.numEggs > 0) {
                        //throw the egg at the current aiming spot
                        yoshi.numEggs--;
                        yoshi.isAiming = false;
                        System.out.println("is aiming " + yoshi.isAiming);
//                        double halfx = yoshi.getCurrentAnimation().getcurFrameSize()[0] / 2;
//                        double tty = yoshi.getY() + yoshi.getCurrentAnimation().getcurFrameSize()[1] * 2 / 3;

                        yoshi.projectiles.add(new Projectile(yoshi.getX() + yoshi.getCurrentAnimation().getcurFrameSize()[0], yoshi.getY(), smallEggSprite, smallEggSize[0], smallEggSize[1], yoshi.theta));
                        yoshi.setCurrentAnimation(stillYoshiData);
                        // Projectile p = new Projectile(smallEggSprite,smallEggSize[0],smallEggSize[1]);
                    }

//                    if (yoshi.projectileTimer <= 0) {
//                        yoshi.resetTimer();
//                        if (yoshi.isGoingLeft()) {
//                            Projectile p = new Projectile(yoshi.getX() - yoshiSize[0], yoshi.getY(), smallEggSprite,
//                                    smallEggSize[0], smallEggSize[1], -1);
//                            p.setGravity(.00009);
//                            yoshi.projectiles.add(p);
//
//                        }
//                        if (yoshi.isGoingRight()) {
//                            Projectile p = new Projectile(yoshi.getX() + yoshiSize[0], yoshi.getY(), smallEggSprite,
//                                    smallEggSize[0], smallEggSize[1], 1);
//                            yoshi.projectiles.add(p);
//                        }
//                    }
                }

                // tongue animation TODO add left version
                if (kbState[KeyEvent.VK_SPACE]) {
                    // get the animation rolling
                    gameStart = false;

                    if (!kbPrevState[KeyEvent.VK_SPACE]) {
                        cp.playClip(tongueClip);
                        yoshi.setTongueOut(true);
                    }
                }
            }

            pauseTime -= deltaTimeMS;
            if (kbState[KeyEvent.VK_P]) {
                //pause
                gameStart = false;
                if (pauseTime <= 0 && pause == false) {
                    pause = true;
                    //cant pause for 3 seconds
                    cp.playClip(pauseClip);
                    pauseTime = 100;
                } else if (pauseTime <= 0 && pause == true) {
                    pause = false;
                    //cant repause for 3 seconds
                    pauseTime = 100;
                }
            }
            if (kbState[KeyEvent.VK_R]) {

                // reset yoshi, enemies, camera
                gameStart = true;
                backgroundLevel = mainLevel;
                inBossRoom = false;
                yoshi.setGrounded(true);
                yoshi.setJump(false);
                yoshi.yvelocity = 0;
                yoshi.setDeath(false);
                yoshi.setCurrentAnimation(stillYoshiData);
                yoshi.setX(30);
                yoshi.setY(145);
                yoshi.isAiming = false;
                yoshi.numEggs = 5;

                enemies.clear();

                enemies.add(shyguy);
                shyguy.setX(130);
                shyguy.setY(145);
                shyguy.setHealth(10);
                shyguy.setGrounded(false);

                enemies.add(shyguy2);
                shyguy2.setX(270);
                shyguy2.setY(130);
                shyguy2.setHealth(10);
                shyguy2.setGrounded(false);
                enemies.add(pewpew);
                pewpew.setX(100);
                pewpew.setY(100);
                pewpew.setHealth(10);
                pewpew.setGrounded(false);
                c.setX(0);
                c.setY(0);
                items.clear();
                addCoins(coinSpin);
                items.add(door);
            }

            // yoshi.update(deltaTimeMS);
            if (!enemies.isEmpty()) {
                for (Enemy e : enemies) {
                    e.update(deltaTimeMS);
                }
            }
            // ------------END Game
            // Logic-----------------------------------------
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

            // all tile ends/starts
            int tileStartX = (int) Math.floor(c.getX() / tileSize[0]);
            int tileStartY = (int) Math.floor(c.getY() / tileSize[1]);
            int tileEndX = (int) Math.floor((c.getX() + camWidth) / tileSize[0]);
            int tileEndY = (int) Math.floor((c.getY() + camHeight) / tileSize[1]);

            // draw if they intersect
            if (!inBossRoom) {
                for (int x = tileStartX; x <= tileEndX; x++) {
                    for (int y = tileStartY; y <= tileEndY; y++) {

                        // always draw the sky (but only what the camera can see)
                        glDrawSprite(gl, skybackground[y][x].texture, (x * tileSize[0]) - c.getX(),
                                (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                        // TODO add background cloud animation draw

                        if (x < BackgroundLayers.BGTREELENGTH * tileSize[0]
                                && y < BackgroundLayers.BGTREEHEIGHT * tileSize[1]) {
                            // TODO readd parallax when a fuller background is
                            // implemented
                            // int treesXThirdCam = c.getX()+(yoshi.getX()/3);
                            glDrawSprite(gl, backgroundTrees[y][x].texture, (x * tileSize[0]) - c.getX(),
                                    (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                        }
                        if (x < 40) {
                            glDrawSprite(gl, backgroundLevel[y][x].texture, (x * tileSize[0]) - c.getX(),
                                    (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                        }
                    }
                    if (!items.isEmpty()) {
                        for (Item i : items) {
                            i.draw(gl, c);
                        }
                    }
                }
            } else {
                //draw boss room
                for (int x = tileStartX; x <= tileEndX; x++) {
                    for (int y = tileStartY; y <= tileEndY; y++) {
                        if (x < 20) {
                            glDrawSprite(gl, backgroundLevel[y][x].texture, (x * tileSize[0]) - c.getX(),
                                    (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                        }
                    }
                }
                //draw boss room items 
            }

            // DRAW SPRITE IF IN SCREEN
            // if (AABB.AABBIntersect(c.getAABB(), yoshi.getAABB())) {
            // draw yoshi
            yoshi.draw(gl, c);
            // glDrawSprite(gl, yoshiSprite, yoshi.getX() - c.getX(),
            // yoshi.getY() - c.getY(), yoshiSize[0], yoshiSize[1]);
            // glDrawSprite(gl, bigEggSprite, 30 - c.getX(), 30 - c.getY(),
            // bigEggAnimData.getcurFrameSize()[0],
            // bigEggAnimData.getcurFrameSize()[1]);

            // draw enemies
            if (!inBossRoom) {
                for (CharacterData cd : enemies) {
                    cd.draw(gl, c);
                }

                // draw projectiles
                for (Enemy e : enemies) {
                    for (Projectile p : e.projectiles) {
                        p.draw(gl, c);
                    }
                }
            } else {
                if (!boss.isDead()) {
                    boss.draw(gl, c);
                }
            }
            for (Projectile p : yoshi.projectiles) {
                p.draw(gl, c);
            }
            if (gameStart) {
                //draw game title
                for (int y = tileStartY; y <= tileEndY; y++) {
                    for (int x = tileStartX; x <= tileEndX; x++) {
                        glDrawSprite(gl, loadScreenTexture, x - c.getX(), y - c.getY(), loadScreenSize[0], loadScreenSize[1]);
                    }
                }
            }
            // }
            // always on top
            if (pause) {
                for (int y = tileStartY; y <= tileEndY; y++) {
                    for (int x = tileStartX; x <= tileEndX; x++) {
                        glDrawSprite(gl, pauseScreenTexture, x, y, pauseScreenSize[0], pauseScreenSize[1]);
                        //
                        // //bushes are drawn at the lower portion of the screen hence the
                        // offset to y
                        // /*the bushes are only 3 high, and NOT the size of the camera.
                        // * ONLY draw if you are at 0-2
                        // * (this avoids a bajillion clear tiles being rendered and only
                        // renders those bush tiles)
                        // */
                        // if (y < BackgroundLayers.BGBUSHHEIGHT) {
                        // //got rid of parallax for now until I can implement more
                        // backgrounds better
                        // //int bushesXTwoThirdYoshiCam = c.getX()+(yoshi.getX()*2/3);
                        // glDrawSprite(gl, backgroundBushes[y][x].texture, (x) *
                        // tileSize[0] - c.getX(), (y + 10) * tileSize[1] - c.getY(),
                        // tileSize[0], tileSize[1]);
                        // }
                    }
                }
            }
            // TODO draw HUD or other things on top of sprite
            // window.swapBuffers();
        } // Load a file into an OpenGL texture and return that texture.

    }

    public static int glTexImageTGAFile(GL2 gl, String filename, int[] out_size) {
        final int BPP = 4;
        // System.out.println(new File("."). getAbsolutePath());
        DataInputStream file = null;
        try {
            // Open the file.
            file = new DataInputStream(new FileInputStream(filename));
        } catch (FileNotFoundException ex) {
            System.err.format("File: %s -- Could not open for reading.", filename);
            return 0;
        }

        try {
            // Skip first two bytes of data we don't need.
            file.skipBytes(2);

            // Read in the image type. For our purposes the image type
            // should be either a 2 or a 3.
            int imageTypeCode = file.readByte();
            if (imageTypeCode != 2 && imageTypeCode != 3) {
                file.close();
                System.err.format("File: %s -- Unsupported TGA type: %d", filename, imageTypeCode);
                return 0;
            }

            // Skip 9 bytes of data we don't need.
            file.skipBytes(9);

            int imageWidth = Short.reverseBytes(file.readShort());
            int imageHeight = Short.reverseBytes(file.readShort());
            int bitCount = file.readByte();
            file.skipBytes(1);

            // Allocate space for the image data and read it in.
            byte[] bytes = new byte[imageWidth * imageHeight * BPP];

            // Read in data.
            if (bitCount == 32) {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = file.readByte();
                }
            } else {
                for (int it = 0; it < imageWidth * imageHeight; ++it) {
                    bytes[it * BPP + 0] = file.readByte();
                    bytes[it * BPP + 1] = file.readByte();
                    bytes[it * BPP + 2] = file.readByte();
                    bytes[it * BPP + 3] = -1;
                }
            }

            file.close();

            // Load into OpenGL
            int[] texArray = new int[1];
            gl.glGenTextures(1, texArray, 0);
            int tex = texArray[0];
            gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0, GL2.GL_BGRA,
                    GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);

            out_size[0] = imageWidth;
            out_size[1] = imageHeight;
            return tex;
        } catch (IOException ex) {
            System.err.format("File: %s -- Unexpected end of file.", filename);
            return 0;
        }
    }

    public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glColor3ub((byte) -1, (byte) -1, (byte) -1);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2i(x, y);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2i(x + w, y);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2i(x + w, y + h);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2i(x, y + h);
        }
        gl.glEnd();
    }

    public static void printTxt(int x, int y) {

    }

    public static void loadbg() {
        backgroundTrees = new Tile[][]{{tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68)},
        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68)},
        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1),
            tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0),
            tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68)},
        {tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7),
            tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9),
            tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5),
            tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6),
            tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3),
            tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4),
            tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7),
            tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9),
            tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68)},
        {tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15),
            tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16),
            tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18),
            tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13),
            tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14),
            tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12),
            tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20),
            tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15),
            tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16),
            tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18),
            tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68)},
        {tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26),
            tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27),
            tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29),
            tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24),
            tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25),
            tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22),
            tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23),
            tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31),
            tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26),
            tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27),
            tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29),
            tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68)},
        {tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37),
            tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38),
            tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40),
            tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35),
            tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36),
            tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33),
            tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34),
            tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68),
            tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42),
            tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37),
            tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38),
            tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40),
            tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68),
            tiles.get(68), tiles.get(68)},
        {tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49),
            tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55),
            tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50),
            tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45),
            tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52),
            tiles.get(53), tiles.get(54), tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47),
            tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53),
            tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48),
            tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52),
            tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54),
            tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(44), tiles.get(45),
            tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51),
            tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46),
            tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54),
            tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47),
            tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(54),
            tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49),
            tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55),
            tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50),
            tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45),
            tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52),
            tiles.get(53), tiles.get(55)},
        {tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59),
            tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61),
            tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59),
            tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58),
            tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61),
            tiles.get(62), tiles.get(63), tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59),
            tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62),
            tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59),
            tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61),
            tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63),
            tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(57), tiles.get(58),
            tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60),
            tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59),
            tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63),
            tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59),
            tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63),
            tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59),
            tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61),
            tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59),
            tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58),
            tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61),
            tiles.get(62), tiles.get(63)},
        {tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66),
            tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66),
            tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66),
            tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66),
            tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66),
            tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66),
            tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65),
            tiles.get(65), tiles.get(65)},
        {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67)},
        {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67)},
        {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67)},
        {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67),
            tiles.get(67), tiles.get(67)},};

        backgroundBushes = new Tile[][]{{tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68),
            tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77),
            tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72),
            tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68),
            tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73),
            tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68),
            tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74),
            tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69),
            tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68),
            tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71),
            tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76),
            tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68),
            tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77),
            tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72),
            tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68),
            tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73),
            tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68),
            tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74),
            tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68)},
        {tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83),
            tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89),
            tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82),
            tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88),
            tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81),
            tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87),
            tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80),
            tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86),
            tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79),
            tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85),
            tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(78), tiles.get(79),
            tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85),
            tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78),
            tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84),
            tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90),
            tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83),
            tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89),
            tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82),
            tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88),
            tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81),
            tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87),
            tiles.get(88), tiles.get(89)},
        {tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96),
            tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102),
            tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95),
            tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101),
            tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94),
            tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100),
            tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93),
            tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99),
            tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92),
            tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98),
            tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(91), tiles.get(92),
            tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98),
            tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91),
            tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93),
            tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103),
            tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96),
            tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102),
            tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95),
            tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101),
            tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94),
            tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100),
            tiles.get(101), tiles.get(102)}};

        mainLevel = new Tile[][]{
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),
                tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(141), tiles.get(129), tiles.get(105), tiles.get(105), tiles.get(150), tiles.get(151), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(192), tiles.get(187), tiles.get(182), tiles.get(177), tiles.get(172), tiles.get(167), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(110), tiles.get(109), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(140), tiles.get(128), tiles.get(120), tiles.get(120), tiles.get(149), tiles.get(152), tiles.get(68), tiles.get(68), tiles.get(200), tiles.get(196), tiles.get(191), tiles.get(186), tiles.get(181), tiles.get(176), tiles.get(171), tiles.get(166), tiles.get(162), tiles.get(159), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(107), tiles.get(108), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(127), tiles.get(132), tiles.get(142), tiles.get(148), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(199), tiles.get(195), tiles.get(190), tiles.get(185), tiles.get(180), tiles.get(175), tiles.get(170), tiles.get(165), tiles.get(161), tiles.get(158), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(126), tiles.get(131), tiles.get(131), tiles.get(147), tiles.get(154), tiles.get(120), tiles.get(121), tiles.get(198), tiles.get(194), tiles.get(189), tiles.get(184), tiles.get(179), tiles.get(174), tiles.get(169), tiles.get(164), tiles.get(160), tiles.get(157), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(111), tiles.get(112), tiles.get(113), tiles.get(114), tiles.get(115), tiles.get(116), tiles.get(117), tiles.get(112), tiles.get(111), tiles.get(116), tiles.get(113), tiles.get(118), tiles.get(111), tiles.get(119), tiles.get(117), tiles.get(115), tiles.get(117), tiles.get(125), tiles.get(130), tiles.get(137), tiles.get(146), tiles.get(153), tiles.get(111), tiles.get(112), tiles.get(197), tiles.get(193), tiles.get(188), tiles.get(183), tiles.get(178), tiles.get(173), tiles.get(168), tiles.get(163), tiles.get(136), tiles.get(156), tiles.get(111), tiles.get(115), tiles.get(119), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},};
        backgroundLevel = mainLevel;

        bossLevel = new Tile[][]{
            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(280), tiles.get(281), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(278), tiles.get(279), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},};
//                = new Tile[][]{
//            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(277), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},
//            {tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276), tiles.get(276),},};
//        bossLevel = new Tile[][]{{tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(107), tiles.get(108), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(127), tiles.get(132), tiles.get(142), tiles.get(148), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(199), tiles.get(195), tiles.get(190), tiles.get(185), tiles.get(180), tiles.get(175), tiles.get(170), tiles.get(165), tiles.get(161), tiles.get(158), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(68), tiles.get(68), tiles.get(68)},
//        {tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(126), tiles.get(131), tiles.get(131), tiles.get(147), tiles.get(154), tiles.get(120), tiles.get(121), tiles.get(198), tiles.get(194), tiles.get(189), tiles.get(184), tiles.get(179), tiles.get(174), tiles.get(169), tiles.get(164), tiles.get(160), tiles.get(157), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(68), tiles.get(68), tiles.get(68)},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
//        {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},};
    }

}
