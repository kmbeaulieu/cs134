

import java.io.DataInputStream;
import java.io.File;
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
import Background.BackgroundAnimDef;
import Background.BackgroundDef;
import Background.BackgroundLayers;
import Background.Tile;
import Character.*;
import Helpers.AABB;
import Helpers.Camera;

public class GameLoop {

    // Set this to true to make the game loop exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];
    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];

    // Texture for the sprite
    private static int yoshiSprite;
    //texture of still yoshi
    private static int yoshiStillSprite;
    // Size of the sprite, this should probably be put somewhere into the Yoshi.java
    private static int[] yoshiSize = new int[2];
    private static int[] yoshiStillSize = new int[2]; //yoshi standing still

    //background stuff
    private static Tile[][] backgroundTrees;
    private static Tile[][] backgroundBushes;
    private static Tile[][] backgroundLevel;

    static BackgroundDef backgroundMain;
    BackgroundDef BackgroundForeGround;
    BackgroundDef levelTiles;
    //Tile size for Background
    private static ArrayList<Tile> tiles = new ArrayList<Tile>();
    private static int[] tileSize = new int[2];
    //world's x tiles and y tile lengths
    private static int worldXTiles = 40;
    private static int worldYTiles = 15;

    //resolution of the screen for the camera
    private static int camWidth = 200;
    private static int camHeight = 200;

    //animation stuff
    private static AnimationDef yoshiSpace;
    private static AnimationDef yoshiWalk;

    //items
    private static int smallEggSprite;
    private static int[] smallEggSize;
    private static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
//    private static AnimationDef eggExisting;
//    private static AnimationDef eggThrown;

    //TODO enemies
    private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    private static AnimationDef shyguywalkleft;

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
        window.setSize(400, 400);
        window.setTitle("Walk Yoshi");
        window.setVisible(true);
        window.setDefaultCloseOperation(
                WindowClosingProtocol.WindowClosingMode.DISPOSE_ON_CLOSE);
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
        gl.glViewport(0, 0, 400, 400);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, camWidth, camHeight, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Game initialization goes here.
        //------KEEP LOADING THINGS IN THIS ORDER OR FUN TIMES WILL NOT BE HAD, ADD TO BOTTOM ONLY-----------
        //NOTE: File names start at 1 but the tiles index starts at 0 because arraylist TODO refactor file names for images
        //load the bajillions of background files 1-68 is for the hills, 69 is transparent
        for (int i = 1; i < 70; i++) {
            String filename = BackgroundLayers.FTL + i + BackgroundLayers.FNE;
            //store the tga info into a class called tile so it can store more states later
            tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false));
            //bottom of the trees will be collidable TODO remove this later, this is just for now
            if (i == 68) {
                tiles.get(i - 1).setCollidable(false);
            }

        }
        //70-104 is bushes, there is a 69 for transparent in this folder too
        for (int i = 70; i < 105; i++) {
            String filename = BackgroundLayers.FBL + i + BackgroundLayers.FNE;
            //store the tga info into a class called tile so it can store more states later
            tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false));

        }
        //105 is the blue sky, it's 101x101 TODO make this the full length of the bgtrees array 
        tiles.add(new Tile("sky", glTexImageTGAFile(gl, "backgrounds//colortiles//sky.tga", tileSize), tileSize, false));
        Tile[][] skybackground = new Tile[101][101];
        for (int y = 0; y < 101; y++) {
            for (int x = 0; x < 101; x++) {
                skybackground[y][x] = tiles.get(104);
            }
        }
        //106-123 are collideable, they are the level floor
        for (int i = 106; i < 147; i++) {
            tileSize = new int[2];
            String filename = BackgroundLayers.FLL + i + BackgroundLayers.FNE;
            //custom non collideable
            if ((i >= 106 && i <= 111) || i == 125 || i == 130 || i == 135 || i == 136 || i == 140 || i == 141 || i == 142 || i == 145 || i == 146) {
                tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, false));

            } else {
                tiles.add(new Tile(filename, glTexImageTGAFile(gl, filename, tileSize), tileSize, true));
            }

        }

        //----------THIS IS THE BOTTOM OF LOADING BACKGROUND IMAGES, ADD MORE RIGHT ABOVE ME----------------
        //load the tile formation for the backgrounds with hills/trees
        loadbg();

        //camera setup
        Camera c = new Camera(0, 0);
        c.getAABB().setH(camWidth);
        c.getAABB().setW(camHeight);
        AABB tileAABB;

        //yoshi player -- 26 x 32
        FrameDef[] yoshiStillFrame = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshi1.tga", yoshiSize), yoshiSize, 1000f)};
        AnimationDef stillYoshi = new AnimationDef("stillyoshi", yoshiStillFrame);
        AnimationData stillYoshiData = new AnimationData(stillYoshi);
        YoshiData yoshi = new YoshiData(0, 144, yoshiSize[0], yoshiSize[1], stillYoshiData);
        yoshi.getAABB().setW(yoshiSize[0]); //this should set yoshi's AABB width to the width of its sprite
        yoshi.getAABB().setH(yoshiSize[1]); //this should set yoshi's AABB height to the height of its sprite

        //TODO get cloud background working, need a background anim data and such
//		int[] cloudSize = new int[2];
//		FrameDef[] cloudTiles = new FrameDef[]{
//				new FrameDef(glTexImageTGAFile(gl, "backgrounds\\cloudanim\\small\\1.tga", cloudSize),cloudSize,400f),
//				new FrameDef(glTexImageTGAFile(gl, "backgrounds\\cloudanim\\small\\2.tga", cloudSize),cloudSize,350f),
//				new FrameDef(glTexImageTGAFile(gl, "backgrounds\\cloudanim\\small\\3.tga", cloudSize),cloudSize,400f),
//				new FrameDef(glTexImageTGAFile(gl, "backgrounds\\cloudanim\\small\\4.tga", cloudSize),cloudSize,330f)
//		};
//		AnimationDef cloudbg = new AnimationDef("floatycloudsmall",cloudTiles);
//		AnimationData cloudanim = new AnimationData(cloudbg);
        //set up the animation for pressing space for yoshi (stick tongue out), the timing is staggered to make it look better
        int[] sizeForFrame1, sizeForFrame2, sizeForFrame3, sizeForFrame4, sizeForFrame5, sizeForFrame6;
        sizeForFrame1 = sizeForFrame2 = sizeForFrame3 = sizeForFrame4 = sizeForFrame5 = sizeForFrame6 = new int[2]; //each frame is a different size so it needs to be stored in the frame information for that current frame
        FrameDef[] yoshiSpaceFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1), sizeForFrame1, 10f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2), sizeForFrame2, 20f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3, 30f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4, 40f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace6.tga", sizeForFrame6), sizeForFrame6, 60f), //tongue back in
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4, 40f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3, 30f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2), sizeForFrame2, 20f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1), sizeForFrame1, 10f)};
        yoshiSpace = new AnimationDef("yoshiSpace", yoshiSpaceFrames);
        AnimationData yoshiSpaceData = new AnimationData(yoshiSpace);

        //set up walking (right) animation TODO refactor name
        int[] walkingAnimSize = new int[2];
        FrameDef[] yoshiWalkFrames = new FrameDef[]{
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\1.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\2.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\3.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\4.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\5.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\6.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\7.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\8.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\9.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\10.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\11.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\12.tga", walkingAnimSize), walkingAnimSize, 50f),};
        yoshiWalk = new AnimationDef("yoshiwalk", yoshiWalkFrames);
        AnimationData yoshiWalkingData = new AnimationData(yoshiWalk);

        //yoshi walking left animation setup
        FrameDef[] yoshiWalkLeftFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\1.tga", walkingAnimSize), walkingAnimSize, 10f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\2.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\3.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\4.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\5.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\6.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\7.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\8.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\9.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\10.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\11.tga", walkingAnimSize), walkingAnimSize, 50f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\12.tga", walkingAnimSize), walkingAnimSize, 50f),};
        AnimationDef yoshiWalkLeft = new AnimationDef("yoshiWalkLeft", yoshiWalkLeftFrames);
        AnimationData yoshiWalkingLeftData = new AnimationData(yoshiWalkLeft);

        int[] yoshiDieAnimSize1, yoshiDieAnimSize2, yoshiDieAnimSize3, yoshiDieAnimSize4, yoshiDieAnimSize5, yoshiDieAnimSize6, yoshiDieAnimSize7, yoshiDieAnimSize8;//  
        yoshiDieAnimSize8 = new int[2];
        yoshiDieAnimSize7 = new int[2];
        yoshiDieAnimSize6 = new int[2];
        yoshiDieAnimSize5 = new int[2];
        yoshiDieAnimSize4 = new int[2];
        yoshiDieAnimSize3 = new int[2];
        yoshiDieAnimSize2 = new int[2];
        yoshiDieAnimSize1 = new int[2];

        FrameDef[] yoshiDieFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\1.tga", yoshiDieAnimSize1), yoshiDieAnimSize1, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\2.tga", yoshiDieAnimSize2), yoshiDieAnimSize2, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\3.tga", yoshiDieAnimSize3), yoshiDieAnimSize3, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\4.tga", yoshiDieAnimSize4), yoshiDieAnimSize4, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\5.tga", yoshiDieAnimSize5), yoshiDieAnimSize5, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\6.tga", yoshiDieAnimSize6), yoshiDieAnimSize6, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\7.tga", yoshiDieAnimSize7), yoshiDieAnimSize7, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshidie\\8.tga", yoshiDieAnimSize8), yoshiDieAnimSize8, 150f),};
        AnimationDef yoshiDie = new AnimationDef("yoshidie", yoshiDieFrames);
        AnimationData yoshiDieData = new AnimationData(yoshiDie);

        //egg sprites
        smallEggSize = new int[2];
        smallEggSprite = glTexImageTGAFile(gl, "sprites//eggs//small//smalleggfat.tga", smallEggSize);
//        FrameDef[] bigEggFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites//eggs//big//bigeggfat.tga", bigEggSize), bigEggSize, 500f),
//            new FrameDef(glTexImageTGAFile(gl, "sprites//eggs//big//bigeggtall.tga", bigEggSize), bigEggSize, 500f)};
//        AnimationDef bigEggAnimDef = new AnimationDef("bigegganimation", bigEggFrames);
//        AnimationData bigEggAnimData = new AnimationData(bigEggAnimDef);

        //make an enemy
        int[] shyguySize = new int[2];
        FrameDef[] shyGuyWalkingFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites//enemies//shyguy//walk//1.tga", shyguySize), shyguySize, 150f)};
        AnimationDef shyGuyWalk = new AnimationDef("walking", shyGuyWalkingFrames);
        AnimationData shyGuyWalkingData = new AnimationData(shyGuyWalk);
        Enemy shyguy = new Enemy(130, 145, shyguySize[0], shyguySize[1], shyGuyWalkingData);
        shyguy.getAABB().setW(shyguySize[0]);
        shyguy.getAABB().setH(shyguySize[1]);
        enemies.add(shyguy);

        int[] pewpewGuySize = new int[2];
        FrameDef[] pewpewGuyShootFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites//enemies//pewpewguy//1.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites//enemies//pewpewguy//2.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites//enemies//pewpewguy//3.tga", pewpewGuySize), pewpewGuySize, 150f),
            new FrameDef(glTexImageTGAFile(gl, "sprites//enemies//pewpewguy//4.tga", pewpewGuySize), pewpewGuySize, 150f)
        };
        AnimationDef pewpewShootDef = new AnimationDef("pew", pewpewGuyShootFrames);
        AnimationData pewpewData = new AnimationData(pewpewShootDef);
        Enemy pewpew = new Enemy(250, 144, pewpewGuySize[0], pewpewGuySize[1], pewpewData);
        int[] pewSize = new int[2];
        int pewTex = glTexImageTGAFile(gl, "sprites//enemies//pewpewguy//pew.tga", pewSize);
        enemies.add(pewpew);
        //-----------------------BEGIN Physics Setup----------------------------
        // Physics runs at 100fps, or 10ms / physics frame
        int physicsDeltaMS = 10;
        long lastPhysicsFrameMS;

        //-----------------------END Physics Setup----------------------------
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

            //------------DO physics-----------------------------------------
            do {
                //1. character movement
                if (yoshi.isGrounded()) {
                    yoshi.yvelocity = 0;
                }
                if (shyguy.isGrounded()) {
                    shyguy.yvelocity = 0;
                }
                if (yoshi.isGrounded() && yoshi.isJumping() && !yoshi.isDead()) {
                    yoshi.yvelocity = yoshi.jumpvel;
                    yoshi.setGrounded(false);

                }
                //if can flutter,pressing jump, and yoshi is falling then flutter jump....needto do something with xvel?
//                if(yoshi.canFlutter() && kbState[KeyEvent.VK_W] && yoshi.yvelocity>0.0 ){
//                    yoshi.yvelocity=-.15;
//                    yoshi.setFlutter(false);
//
//                }
                shyguy.yvelocity = shyguy.yvelocity + shyguy.gravity * physicsDeltaMS;
                yoshi.yvelocity = yoshi.yvelocity + yoshi.gravity * physicsDeltaMS;
                //getting rid of this int cast messes up yoshi falling. 
                //The jump portion is ok but after it hits positive it will land it right away. 
                //not casting here but casting for the AABB messing this up maybe?
                shyguy.setY((int) (shyguy.getY() + shyguy.yvelocity * physicsDeltaMS));
                yoshi.setY((int) (yoshi.getY() + yoshi.yvelocity * physicsDeltaMS));
                //projectile movement
                if (!projectiles.isEmpty()) {
                    for (Projectile p : projectiles) {
                        p.setYVel(p.getYVel() + (p.getGravity() * physicsDeltaMS));
                        p.setX(p.getX() + (p.getDir() * p.getSpeed()));
                        p.setY((p.getY() + p.getYVel() * physicsDeltaMS));
                    }
                }

//                //2. bg collision detection/res  RESOLVE X THEN Y
                int tileStartX = (int) Math.floor(c.getX() / tileSize[0]);
                int tileStartY = (int) Math.floor(c.getY() / tileSize[0]);
                int tileEndX = (int) Math.floor((c.getX() + camWidth) / tileSize[0]);
                int tileEndY = (int) Math.floor((c.getY() + camHeight) / tileSize[1]);

                for (int x = tileStartX; x < tileEndX; x++) {
                    for (int y = tileStartY; y < tileEndY; y++) {
                        if (backgroundLevel[y][x].collidable) {
                            tileAABB = new AABB(x * tileSize[0], y * tileSize[1], tileSize[0], tileSize[1]);

//                            if (AABB.AABBIntersectLeftOf(yoshi.getAABB(), tileAABB, yoshi.getPrevX(), yoshi.getX())) {
//                                yoshi.setX(x * tileSize[0] - yoshi.getCurrentAnimation().getcurFrameSize()[0]);
//                            }
                            //ths doesn't work
                            if (AABB.AABBIntersectLeftOf(yoshi.getAABB(), tileAABB, yoshi.getPrevX(), yoshi.getX())) {
                                yoshi.setX(yoshi.getX() - AABB.getOverlap(yoshi.getAABB(), tileAABB));
                            }
                            if (AABB.AABBIntersectAbove(tileAABB, yoshi.getAABB(), yoshi.yvelocity)) {
                                yoshi.setY(yoshi.getY() - AABB.getOverlap(yoshi.getAABB(), tileAABB));
                                yoshi.setGrounded(true);
                            }
                            if (AABB.AABBIntersectAbove(tileAABB, shyguy.getAABB(), shyguy.yvelocity)) {
                                shyguy.setY(shyguy.getY() - AABB.getOverlap(shyguy.getAABB(), tileAABB));
                                shyguy.setGrounded(true);
                            }
                            if (!projectiles.isEmpty()) {
                                for (int i = 0; i < projectiles.size(); i++) {
                                    if (AABB.AABBIntersect(tileAABB, projectiles.get(i).getAABB())) {
                                        projectiles.remove(i);
                                        if (projectiles.isEmpty()) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //projectile collision
                if (!projectiles.isEmpty()) {
                    for (int i = 0; i < projectiles.size(); i++) {
                        if(AABB.AABBIntersect(projectiles.get(i).getAABB(),yoshi.getAABB())){
                            yoshi.setHealth(yoshi.getHealth()-projectiles.get(i).getDamage());
                            if(yoshi.getHealth()<=0){
                                yoshi.setGrounded(true);
                                yoshi.setDeath(true);
                                
                            }
                            projectiles.remove(i);
                        }
                        if (!enemies.isEmpty() && !projectiles.isEmpty()) {
                            for (Enemy e : enemies) {
                                if (AABB.AABBIntersect(projectiles.get(i).getAABB(), e.getAABB())) {

                                    e.setHealth(e.getHealth() - projectiles.get(i).getDamage());
                                    projectiles.remove(i);

                                    if (e.getHealth() <= 0) {
                                        //shyguy.isDead = true;
                                        //let's take the enemy and move him somewhere else
                                        
                                        e.setX((int) (Math.random() * 200));
                                        e.setY(144);
                                        e.setHealth(10);
                         
                                    }
                                    if(projectiles.isEmpty()){
                                        break;
                                    }
                                } //if the projectile is NOT in the camera box, remove!
                               
                            }

                        }
                     else if (!projectiles.isEmpty()&& !AABB.AABBIntersect(c.getAABB(), projectiles.get(i).getAABB()) ) {
                                    projectiles.remove(i);
                                }
                    }
                }
                //player collision/resolution
                if (AABB.AABBIntersect(yoshi.getAABB(), shyguy.getAABB())) {
                    if (AABB.AABBIntersectAbove(shyguy.getAABB(), yoshi.getAABB(), yoshi.yvelocity)) {
                        yoshi.yvelocity = -.2;
                        shyguy.setX((int) (Math.random() * 200));
                        shyguy.setY(144);
                        shyguy.setHealth(10);

                    } else {
                        yoshi.setGrounded(true);
                        yoshi.setDeath(true);
                    }
                }
                if(AABB.AABBIntersect(yoshi.getAABB(), pewpew.getAABB())){
                    yoshi.setGrounded(true);
                    yoshi.setDeath(true);
                }

                //3.collision resolution
                lastPhysicsFrameMS += physicsDeltaMS;
            } while (lastPhysicsFrameMS + physicsDeltaMS < curFrameMS);
            //TODO cloudanim.update(deltaTimeMS);
            //------------DO Normal update-----------------------------------------	
            // System.out.println("deltatimems " + deltaTimeMS);
            pewpew.pewCooldown -= 10;
            if (pewpew.pewCooldown <= 0 && !pewpew.isDead()) {
                pewpew.resetPewCooldown();
                double sqr1 = (yoshi.getX() - pewpew.getX()) * (yoshi.getX() - pewpew.getX());
                double sqr2 = (yoshi.getY() - pewpew.getY()) * (yoshi.getY() - pewpew.getY());
                if (Math.sqrt(sqr1 - sqr2) < 50.0) {
                    //it's close so shoot

                    if (AABB.AABBisLeftOf(yoshi.getAABB(), pewpew.getAABB())) {
                        Projectile p = new Projectile(pewpew.getX()-4, pewpew.getY() + 6, pewTex, pewSize[0], pewSize[1], -1);
                        //p.setGravity();
                        p.setXVel(5);
                        projectiles.add(p);
                    } else if (AABB.AABBisRightOf(yoshi.getAABB(), pewpew.getAABB())) {
                        Projectile p = new Projectile(pewpew.getX() + pewpew.getW(), pewpew.getY() + 6, pewTex, pewSize[0], pewSize[1], 1);
                        p.setXVel(5);
                        projectiles.add(p);
                    }
                    //Projectile p = new Projectile(yoshi.getX() + yoshiSize[0], yoshi.getY(), smallEggSprite, smallEggSize[0], smallEggSize[1], 1);
                }
            }
            //do animation changes that happen even if a key is not pressed (ex walking is not here because it only happens when a key is pressed)
            if (yoshi.isTongueOut()) {
                yoshi.setCurrentAnimation(yoshiSpaceData);
                yoshi.update(deltaTimeMS);
//                yoshiSpaceData.update(deltaTimeMS);
//                yoshiSprite = yoshiSpaceData.getCurFrameImage();
//                yoshiSize = yoshiSpaceData.getcurFrameSize();

                if (yoshiSpaceData.getCurFrame() == yoshiSpaceData.getMaxFrame()) {
                    //you are at the max frame so stop the animation
                    yoshi.setTongueOut(false);
                    yoshi.setCurrentAnimation(stillYoshiData);
                    yoshi.update(deltaTimeMS);
                    //yoshi.setCurrentAnimation(yoshi.)
//                    yoshiSprite = yoshiStillSprite;
//                    yoshiSize = yoshiStillSize;
                }
            }
            if (yoshi.isDead()) {
                yoshi.setGrounded(true);
                yoshi.setCurrentAnimation(yoshiDieData);
                yoshi.update(deltaTimeMS);
                //  yoshi.update(deltaTimeMS);
//                yoshiSprite = yoshiDieData.getCurFrameImage();
//                yoshiSize = yoshiDieData.getcurFrameSize();

                if (yoshi.getCurrentAnimation().getCurFrame() == yoshi.getCurrentAnimation().getMaxFrame()) {

                    yoshiDieData.resetAnimation();
                    //reset player and cam
                    yoshi.setX(10);
                    yoshi.setY(144);
                    yoshi.setCurrentAnimation(stillYoshiData);
                    yoshi.setDeath(false);
                    yoshi.update(deltaTimeMS);
                    //yoshi.update()
                    c.setX(0);
                    c.setY(0);
                }

            }

            yoshi.setPrevX(yoshi.getX());
            yoshi.setPrevY(yoshi.getY());
            //------------DO game logic-----------------------------------------
            if (kbState[KeyEvent.VK_ESCAPE]) {
                shouldExit = true;
            }

            if (!yoshi.isDead()) {
                //go left, continue animation but dont move if at the end of the screen
                if (kbState[KeyEvent.VK_A]) {
                    //if not at the left edge of the world then move left            	
                    if (yoshi.getX() - c.getSpeed() >= 0) {
                        yoshi.moveYoshiLeft();
                    }
                    int cHalf = (c.getX() + camWidth) / 2;

                    if (c.getX() - yoshi.getSpeed() > 0) {
                        c.setX(c.getX() - yoshi.getSpeed());
                    } else {
                        c.setX(0);
                    }
                    yoshi.setCurrentAnimation(yoshiWalkingLeftData);
                    yoshi.update(deltaTimeMS);
                    //yoshiWalkingLeftData.update(deltaTimeMS);
                    // yoshiSprite = yoshiWalkingLeftData.getCurFrameImage();
                    // yoshiSize = yoshiWalkingLeftData.getcurFrameSize();
//                    System.out.println("x: " + yoshi.getX());
//                    System.out.println("y: " + yoshi.getY());

                }

                //go right, continue animation even if at the end
                if (kbState[KeyEvent.VK_D]) {
                    //dont move if you are at the end of the world size

                    //if yoshi is within the world, move right
                    if (yoshi.getX() + yoshi.getSpeed() <= worldXTiles * tileSize[0] - yoshiSize[0]) {
                        yoshi.moveYoshiRight();

                        //if yoshi is beyond the middle of the screen
                        int cHalf = (c.getX() + camWidth) / 2;
                        if (cHalf < yoshi.getX()) {
                            //if moving the camera is within the world, move the camera, else stop at the end
                            if (c.getX() + yoshi.getSpeed() + camWidth < worldXTiles * tileSize[0]) {
                                c.setX(c.getX() + yoshi.getSpeed());
                            } else {
                                c.setX(worldXTiles * tileSize[0] - camWidth);
                            }
                        }

                    }
                    //dont move yoshi but still do walk animation
                    yoshi.setCurrentAnimation(yoshiWalkingData);
                    yoshi.update(deltaTimeMS);
//                    yoshiWalkingData.update(deltaTimeMS);
//                    yoshiSprite = yoshiWalkingData.getCurFrameImage();
//                    yoshiSize = yoshiWalkingData.getcurFrameSize();
                }

                if (kbState[KeyEvent.VK_W]) {
                    // System.out.println("press w x: " + yoshi.getX());
                    // System.out.println("press w y: " + yoshi.getY());
                    yoshi.setJump(true);

                } else {
                    yoshi.setJump(false);
                }

                //decrease timer
                yoshi.projectileTimer -= deltaTimeMS;
                if (kbState[KeyEvent.VK_E]) {
                    //throw
                    if (yoshi.projectileTimer <= 0) {
                        yoshi.resetTimer();
                        if (yoshi.isGoingLeft()) {
                            Projectile p = new Projectile(yoshi.getX() - yoshiSize[0], yoshi.getY(), smallEggSprite, smallEggSize[0], smallEggSize[1], -1);
                            p.setGravity(.00009);
                            projectiles.add(p);

                        }
                        if (yoshi.isGoingRight()) {
                            Projectile p = new Projectile(yoshi.getX() + yoshiSize[0], yoshi.getY(), smallEggSprite, smallEggSize[0], smallEggSize[1], 1);
                            projectiles.add(p);
                        }
                    }

                }

                //tongue animation TODO add left version
                if (kbState[KeyEvent.VK_SPACE]) {
                    //get the animation rolling
                    yoshi.setTongueOut(true);
                }
            }

            if (kbState[KeyEvent.VK_R]) {

                //reset yoshi, enemies, camera
                yoshi.setGrounded(true);
                yoshi.setJump(false);
                yoshi.yvelocity = 0;
                yoshi.setDeath(false);
                yoshi.setCurrentAnimation(stillYoshiData);
                yoshi.setX(10);
                yoshi.setY(145);
                shyguy.setX(130);
                shyguy.setY(145);
                enemies.add(shyguy);
                shyguy.setHealth(10);
                c.setX(0);
                c.setY(0);
            }

            // yoshi.update(deltaTimeMS);
            if (!enemies.isEmpty()) {
                for (Enemy e : enemies) {
                    e.update(deltaTimeMS);
                }
            }
            //    System.out.println(yoshi.getY());
            //------------END Game Logic-----------------------------------------
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

            //all tile ends/starts
            int tileStartX = (int) Math.floor(c.getX() / tileSize[0]);
            int tileStartY = (int) Math.floor(c.getY() / tileSize[1]);
            int tileEndX = (int) Math.floor((c.getX() + camWidth) / tileSize[0]);
            int tileEndY = (int) Math.floor((c.getY() + camHeight) / tileSize[1]);

            // if (AABB.AABBIntersect(c.getAABB(), yoshi.getAABB())) {
            //draw if they intersect
            for (int x = tileStartX; x <= tileEndX; x++) {
                for (int y = tileStartY; y <= tileEndY; y++) {

                    //always draw the sky (but only what the camera can see)
                    glDrawSprite(gl, skybackground[y][x].texture, (x * tileSize[0]) - c.getX(), (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                    //TODO add background cloud animation draw

                    if (x < BackgroundLayers.BGTREELENGTH * tileSize[0] && y < BackgroundLayers.BGTREEHEIGHT * tileSize[1]) {
                        //TODO readd parallax when a fuller background is implemented 
                        //int treesXThirdCam = c.getX()+(yoshi.getX()/3);
                        glDrawSprite(gl, backgroundTrees[y][x].texture, (x * tileSize[0]) - c.getX(), (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                    }
                    if (x < 40) {
                        glDrawSprite(gl, backgroundLevel[y][x].texture, (x * tileSize[0]) - c.getX(), (y * tileSize[1]) - c.getY(), tileSize[0], tileSize[1]);
                    }
                }
            }

            //DRAW SPRITE IF IN SCREEN
            if (AABB.AABBIntersect(c.getAABB(), yoshi.getAABB())) {
                yoshi.draw(gl, c);
                //glDrawSprite(gl, yoshiSprite, yoshi.getX() - c.getX(), yoshi.getY() - c.getY(), yoshiSize[0], yoshiSize[1]);
                // glDrawSprite(gl, bigEggSprite, 30 - c.getX(), 30 - c.getY(), bigEggAnimData.getcurFrameSize()[0], bigEggAnimData.getcurFrameSize()[1]);

                for (CharacterData cd : enemies) {
                    cd.draw(gl, c);
                }

                for (Projectile p : projectiles) {
                    p.draw(gl, c);
                }

            }

            //always on top
            for (int y = tileStartY; y <= tileEndY; y++) {
                for (int x = tileStartX; x <= tileEndX; x++) {

                    //bushes are drawn at the lower portion of the screen hence the offset to y
                    /*the bushes are only 3 high, and NOT the size of the camera. 
	            		 * ONLY draw if you are at 0-2 
	            		 * (this avoids a bajillion clear tiles being rendered and only renders those bush tiles)
                     */
                    if (y < BackgroundLayers.BGBUSHHEIGHT) {
                        //got rid of parallax for now until I can implement more backgrounds better
                        //int bushesXTwoThirdYoshiCam = c.getX()+(yoshi.getX()*2/3);
                        glDrawSprite(gl, backgroundBushes[y][x].texture, (x) * tileSize[0] - c.getX(), (y + 10) * tileSize[1] - c.getY(), tileSize[0], tileSize[1]);
                    }
                }
            }
            //}

            //TODO draw HUD or other things on top of sprite
            //  window.swapBuffers();
        }
    }

    // Load a file into an OpenGL texture and return that texture.
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

            // Read in the image type.  For our purposes the image type
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
            gl.glTexImage2D(
                    GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth, imageHeight, 0,
                    GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, ByteBuffer.wrap(bytes));
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

    public static void loadbg() {
        backgroundTrees = new Tile[][]{
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(9), tiles.get(10), tiles.get(11), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(12), tiles.get(13), tiles.get(14), tiles.get(15), tiles.get(16), tiles.get(17), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(18), tiles.get(19), tiles.get(20), tiles.get(21), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(22), tiles.get(23), tiles.get(24), tiles.get(25), tiles.get(26), tiles.get(27), tiles.get(28), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(29), tiles.get(30), tiles.get(31), tiles.get(32), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(33), tiles.get(34), tiles.get(35), tiles.get(36), tiles.get(37), tiles.get(38), tiles.get(39), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(40), tiles.get(41), tiles.get(42), tiles.get(43), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68)},
            {tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(44), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(52), tiles.get(53), tiles.get(54), tiles.get(55), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(48), tiles.get(49), tiles.get(50), tiles.get(51), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(45), tiles.get(46), tiles.get(47), tiles.get(56), tiles.get(54), tiles.get(55), tiles.get(52), tiles.get(53), tiles.get(55)},
            {tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(57), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(61), tiles.get(62), tiles.get(63), tiles.get(61), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(58), tiles.get(59), tiles.get(59), tiles.get(60), tiles.get(63), tiles.get(64), tiles.get(61), tiles.get(62), tiles.get(63)},
            {tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(66), tiles.get(66), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65), tiles.get(65)},
            {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67)},
            {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67)},
            {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67)},
            {tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67), tiles.get(67)},};

        backgroundBushes = new Tile[][]{
            {tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68), tiles.get(68), tiles.get(69), tiles.get(70), tiles.get(71), tiles.get(68), tiles.get(72), tiles.get(73), tiles.get(74), tiles.get(68), tiles.get(75), tiles.get(76), tiles.get(77), tiles.get(68)},
            {tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89), tiles.get(90), tiles.get(78), tiles.get(79), tiles.get(80), tiles.get(81), tiles.get(82), tiles.get(83), tiles.get(84), tiles.get(85), tiles.get(86), tiles.get(87), tiles.get(88), tiles.get(89)},
            {tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102), tiles.get(103), tiles.get(91), tiles.get(92), tiles.get(93), tiles.get(94), tiles.get(95), tiles.get(96), tiles.get(93), tiles.get(98), tiles.get(99), tiles.get(100), tiles.get(101), tiles.get(102)}
        };

        backgroundLevel = new Tile[][]{
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(135), tiles.get(145), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(141), tiles.get(129), tiles.get(134), tiles.get(144), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(110), tiles.get(109), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(140), tiles.get(128), tiles.get(133), tiles.get(143), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},
            {tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(107), tiles.get(108), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(127), tiles.get(132), tiles.get(142), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(107), tiles.get(108), tiles.get(106), tiles.get(105), tiles.get(106), tiles.get(105), tiles.get(127), tiles.get(132), tiles.get(142)},
            {tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(126), tiles.get(131), tiles.get(138), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(121), tiles.get(120), tiles.get(126), tiles.get(131), tiles.get(138)},
            {tiles.get(111), tiles.get(112), tiles.get(113), tiles.get(114), tiles.get(115), tiles.get(116), tiles.get(117), tiles.get(112), tiles.get(111), tiles.get(116), tiles.get(113), tiles.get(118), tiles.get(111), tiles.get(119), tiles.get(119), tiles.get(115), tiles.get(119), tiles.get(125), tiles.get(130), tiles.get(137), tiles.get(111), tiles.get(112), tiles.get(113), tiles.get(114), tiles.get(115), tiles.get(116), tiles.get(117), tiles.get(112), tiles.get(111), tiles.get(116), tiles.get(113), tiles.get(118), tiles.get(111), tiles.get(119), tiles.get(119), tiles.get(115), tiles.get(119), tiles.get(125), tiles.get(130), tiles.get(137)},
            {tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68), tiles.get(68),},};
    }

}
