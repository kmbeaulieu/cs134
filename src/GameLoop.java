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
import Character.YoshiData;
import Helpers.AABB;
import Helpers.Camera;

public class GameLoop {
    // Set this to true to make the game loop exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];
    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];

    // Position of the sprite
    private static int[] spritePos = new int[] {0, 0};
    // Texture for the sprite
    private static int yoshiSprite;
    //texture of still yoshi
    private static int yoshiStillSprite;
    // Size of the sprite, this should probably be put somewhere into the Yoshi.java
    private static int[] yoshiSize = new int[2];
    private static int[] yoshiStillSize = new int[2]; //yoshi standing still
    
  //background stuff
    static BackgroundDef backgroundMain;
    BackgroundDef BackgroundForeGround;
    BackgroundDef levelTiles;
    //Tile size for Background
    private static ArrayList<Tile> tiles = new ArrayList<Tile>();
    private static int[] tileSize = new int[2];
    //world's x tiles and y tile lengths
    private static int worldXTiles = 100;
    private static int worldYTiles = 15;
    
    //resolution of the screen for the camera
    private static int screenResX = 200;
    private static int screenResY = 200;

    //animation stuff
    private static AnimationDef yoshiSpace;
    private static AnimationDef yoshiWalk; //TODO flip yoshi's sprite for left vs right
    

    public static void main(String[] args) {
        GLProfile gl2Profile;

        try {
            // Make sure we have a recent version of OpenGL
            gl2Profile = GLProfile.get(GLProfile.GL2);
        }
        catch (GLException ex) {
            System.out.println("OpenGL max supported version is too low.");
            System.exit(1);
            return;
        }

        // Create the window and OpenGL context.
        GLWindow window = GLWindow.create(new GLCapabilities(gl2Profile));
        window.setSize(screenResX, screenResY);
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
        gl.glViewport(0, 0, screenResX, screenResY);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, screenResX, screenResY, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
        
        //------KEEP LOADING THINGS IN THIS ORDER OR FUN TIMES WILL NOT BE HAD ADD TO BOTTOM ONLY-----------
        //NOTE: File names start at 1 but the tiles index starts at 0 because arraylist TODO refactor file names for images
		//load the bajillions of background files 1-68 is for the hills, 69 is transparent
		for(int i=1;i<70;i++){
			String filename = BackgroundLayers.FTL + i + BackgroundLayers.FNE;
			//store the tga info into a class called tile so it can store more states later
			tiles.add( new Tile(filename, glTexImageTGAFile(gl,filename,tileSize), tileSize));
			
		}
		//70-104 is bushes, there is a 69 for transparent in this folder too
		for(int i=70;i<105;i++){
			String filename = BackgroundLayers.FBL + i + BackgroundLayers.FNE;
			//store the tga info into a class called tile so it can store more states later
			tiles.add(new Tile(filename, glTexImageTGAFile(gl,filename,tileSize),tileSize));
			
		}
		//104 is the blue sky, it's 101x101 TODO make this the full length of the bgtrees array 
		tiles.add(new Tile("sky",glTexImageTGAFile(gl,".//backgrounds//colortiles//sky.tga",tileSize),tileSize));
		Tile[][] skybackground = new Tile[101][101];
		for(int y =0;y<101;y++)
		{
			for(int x=0;x<101;x++){
				skybackground[y][x] = tiles.get(104);
			}
		}
		
		//----------THIS IS THE BOTTOM OF LOADING IMAGES, ADD MORE RIGHT ABOVE ME----------------
		
		//load the tile formation for the backgrounds with hills/trees
		 Tile[][] backgroundTrees = new Tile[][]{
				{tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(0), tiles.get(1), tiles.get(2),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(9),tiles.get(10),tiles.get(11),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(9),tiles.get(10),tiles.get(11),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(9),tiles.get(10),tiles.get(11),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68), tiles.get(3), tiles.get(4), tiles.get(5), tiles.get(6), tiles.get(7), tiles.get(8),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(9),tiles.get(10),tiles.get(11),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(18),tiles.get(19),tiles.get(20),tiles.get(21),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(18),tiles.get(19),tiles.get(20),tiles.get(21),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(18),tiles.get(19),tiles.get(20),tiles.get(21),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(12),tiles.get(13),tiles.get(14),tiles.get(15),tiles.get(16),tiles.get(17),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(18),tiles.get(19),tiles.get(20),tiles.get(21),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(29),tiles.get(30),tiles.get(31),tiles.get(32),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(29),tiles.get(30),tiles.get(31),tiles.get(32),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(29),tiles.get(30),tiles.get(31),tiles.get(32),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(22),tiles.get(23),tiles.get(24),tiles.get(25),tiles.get(26),tiles.get(27),tiles.get(28),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(29),tiles.get(30),tiles.get(31),tiles.get(32),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(40),tiles.get(41),tiles.get(42),tiles.get(43),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(40),tiles.get(41),tiles.get(42),tiles.get(43),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(40),tiles.get(41),tiles.get(42),tiles.get(43),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(33),tiles.get(34),tiles.get(35),tiles.get(36),tiles.get(37),tiles.get(38),tiles.get(39),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(40),tiles.get(41),tiles.get(42),tiles.get(43),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68),tiles.get(68)},
				{tiles.get(44),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(55),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(56),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(44),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(55),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(56),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(44),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(55),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(56),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(44),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(52),tiles.get(53),tiles.get(54),tiles.get(55),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(48),tiles.get(49),tiles.get(50),tiles.get(51),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(45),tiles.get(46),tiles.get(47),tiles.get(56),tiles.get(54),tiles.get(55),tiles.get(52),tiles.get(53),tiles.get(55)},
				{tiles.get(57),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(61),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(57),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(61),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(57),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(61),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(57),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(61),tiles.get(62),tiles.get(63),tiles.get(61),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(58),tiles.get(59),tiles.get(59),tiles.get(60),tiles.get(63),tiles.get(64),tiles.get(61),tiles.get(62),tiles.get(63)},
				{tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(66),tiles.get(66),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65),tiles.get(65)},
				{tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67)},
				{tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67)},
				{tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67)},
				{tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67),tiles.get(67)},
			};
		
		Tile[][] backgroundBushes = new Tile[][]{
			{tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68),tiles.get(68),tiles.get(69),tiles.get(70),tiles.get(71),tiles.get(68),tiles.get(72),tiles.get(73),tiles.get(74),tiles.get(68),tiles.get(75),tiles.get(76),tiles.get(77),tiles.get(68)},
			{tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89),tiles.get(90),tiles.get(78),tiles.get(79),tiles.get(80),tiles.get(81),tiles.get(82),tiles.get(83),tiles.get(84),tiles.get(85),tiles.get(86),tiles.get(87),tiles.get(88),tiles.get(89)},
			{tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102),tiles.get(103),tiles.get(91),tiles.get(92),tiles.get(93),tiles.get(94),tiles.get(95),tiles.get(96),tiles.get(93),tiles.get(98),tiles.get(99),tiles.get(100),tiles.get(101),tiles.get(102)}
		};	
		//camera setup
		Camera c = new Camera(0,0);
		c.getAABB().setH(screenResX);
		c.getAABB().setW(screenResY);
		
		
		//yoshi player
        YoshiData yoshi= new YoshiData(10,150);
        yoshiStillSprite = glTexImageTGAFile(gl, ".\\sprites\\yoshi1.tga", yoshiSize);
        yoshi.getAABB().setW(yoshiSize[0]); //this should set yoshi's AABB width to the width of its sprite
        yoshi.getAABB().setH(yoshiSize[1]); //this should set yoshi's AABB height to the height of its sprite
        yoshiStillSize = yoshiSize; //TODO actually get a frame for yoshi's still sprite;
		yoshiSprite = yoshiStillSprite;
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
		int[] sizeForFrame1,sizeForFrame2,sizeForFrame3,sizeForFrame4,sizeForFrame5,sizeForFrame6;
		sizeForFrame1=sizeForFrame2=sizeForFrame3=sizeForFrame4=sizeForFrame5=sizeForFrame6= new int[2]; //each frame is a different size so it needs to be stored in the frame information for that current frame
		FrameDef[] yoshiSpaceFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1),sizeForFrame1,10f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2),sizeForFrame2,20f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3,30f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4,40f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace6.tga", sizeForFrame6), sizeForFrame6,60f), //tongue back in
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4,40f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3,30f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2), sizeForFrame2,20f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1), sizeForFrame1,10f)};
		yoshiSpace = new AnimationDef("yoshiSpace", yoshiSpaceFrames);
		AnimationData yoshiSpaceData = new AnimationData(yoshiSpace);
		
		//set up walking (right) animation TODO refactor name
		int[] walkingAnimSize = new int[2];
		FrameDef[] yoshiWalkFrames = new FrameDef[]{
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\1.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\2.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\3.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\4.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\5.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\6.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\7.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\8.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\9.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\10.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\11.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalk\\12.tga", walkingAnimSize), walkingAnimSize,50f),
		};
		yoshiWalk = new AnimationDef("yoshiwalk",yoshiWalkFrames);
		AnimationData yoshiWalkingData = new AnimationData(yoshiWalk);
		
		//yoshi walking left animation setup
		FrameDef[] yoshiWalkLeftFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\1.tga", walkingAnimSize), walkingAnimSize,10f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\2.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\3.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\4.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\5.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\6.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\7.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\8.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\9.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\10.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\11.tga", walkingAnimSize), walkingAnimSize,50f),
				new FrameDef(glTexImageTGAFile(gl, "sprites\\yoshiwalkleft\\12.tga", walkingAnimSize), walkingAnimSize,50f),
		};
		AnimationDef yoshiWalkLeft = new AnimationDef("yoshiWalkLeft",yoshiWalkLeftFrames);
		AnimationData yoshiWalkingLeftData = new AnimationData(yoshiWalkLeft);
		
			
        // The game loop
        long lastFrameNS;
        long curFrameNS = System.nanoTime();
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
            lastFrameNS = curFrameNS;
            curFrameNS = System.nanoTime();
            long deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
            // Actually, this runs the entire OS message pump.
            window.display();
            
            if (!window.isVisible()) {
                shouldExit = true;
                break;
            }
			//for physics
            /*
             * do{}while()
             */
            
           //do animation changes that happen even if a key is not pressed (ex walking is not here because it only happens when a key is pressed)
            	if(yoshi.isTongueOut){
            		yoshiSpaceData.update(deltaTimeMS);
                	yoshiSprite = yoshiSpaceData.getCurFrameImage();
                	yoshiSize = yoshiSpaceData.getcurFrameSize();
                	
                	if(yoshiSpaceData.getCurFrame()==yoshiSpaceData.getMaxFrame()){
                		//you are at the max frame so stop the animation
                		yoshi.setTongueOut(false);
                		yoshiSprite = yoshiStillSprite;
                		yoshiSize = yoshiStillSize;
                	}
            	}
            	
            //TODO cloudanim.update(deltaTimeMS);
            	
            
            
            
            
            // Game logic goes here.
            if (kbState[KeyEvent.VK_ESCAPE]) {	
                shouldExit = true;
            }
                        
            //go left, continue animation but dont move if at the end of the screen
            if(kbState[KeyEvent.VK_LEFT]){
            	//if not at the left edge of the world then move left            	
            	if(yoshi.getX()-c.getSpeed()>=0){
            		yoshi.moveYoshiLeft();
            	}
            	int cHalf = (c.getX()+screenResX)/2;
        		if(yoshi.getX()>cHalf){
        			if(c.getX()-yoshi.getSpeed()>0){
                		c.setX(c.getX()-yoshi.getSpeed());
        			}else{
        				c.setX(0);
        			}
        		}
            	yoshiWalkingLeftData.update(deltaTimeMS);
        		yoshiSprite = yoshiWalkingLeftData.getCurFrameImage();
        		yoshiSize = yoshiWalkingLeftData.getcurFrameSize();
        		 
            } 
           
            //go right, continue animation even if at the end
            if(kbState[KeyEvent.VK_RIGHT]){
            	//dont move if you are at the end of the world size
            	
            	//if yoshi is within the world, move right
            	if(yoshi.getX()+yoshi.getSpeed() <= worldXTiles*tileSize[0]-yoshiSize[0]){
            		yoshi.moveYoshiRight();
            		//if yoshi is beyond the middle of the screen
            		int cHalf = (c.getX()+screenResX)/2;
            		if(cHalf<yoshi.getX()){
            			//if moving the camera is within the world, move the camera, else stop at the end
            			if(c.getX()+yoshi.getSpeed()+screenResX<worldXTiles*tileSize[0]){
                    		c.setX(c.getX()+yoshi.getSpeed());
            			}else{
            				c.setX(worldXTiles*tileSize[0]-screenResX);
            			}
            		}

            	}
            	//dont move yoshi but still do walk animation
            	yoshiWalkingData.update(deltaTimeMS);  
        		yoshiSprite = yoshiWalkingData.getCurFrameImage();
        		yoshiSize = yoshiWalkingData.getcurFrameSize();
            } 
           
            //tongue animation TODO add left version
            if(kbState[KeyEvent.VK_SPACE]){
            	//get the animation rolling
            	yoshi.setTongueOut(true);
            }
            
            
 
            
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            
            //all tile ends/starts
            int tileStartX = (int) Math.floor(c.getX()/tileSize[0]);
            int tileStartY = (int) Math.floor(c.getY()/tileSize[0]);           
        	int tileEndX = (int) Math.floor((c.getX()+screenResX)/tileSize[0]);
        	int tileEndY = (int) Math.floor((c.getY()+screenResY)/tileSize[1]);
            if(AABB.AABBIntersect(c.getAABB(),yoshi.getAABB())){
            	//draw if they intersect
            	for(int x=tileStartX;x<=tileEndX;x++){
	            	for(int y = tileStartY; y <=tileEndY ;y++){
	            		
	            		//always draw the sky (but only what the camera can see)
	            		glDrawSprite(gl,skybackground[y][x].texture, (x*tileSize[0])-c.getX(),(y*tileSize[1])-c.getY(),tileSize[0],tileSize[1]);
	            		//TODO add background cloud animation draw
	            		
	            		if(x<BackgroundLayers.BGTREELENGTH*tileSize[0] && y<BackgroundLayers.BGTREEHEIGHT*tileSize[1]){
	            			//TODO readd parallax when a fuller background is implemented 
	            			//int treesXThirdCam = c.getX()+(yoshi.getX()/3);
		                	glDrawSprite(gl,backgroundTrees[y][x].texture,(x*tileSize[0])-c.getX(),(y*tileSize[1])-c.getY(),tileSize[0],tileSize[1]);
	            		}	
	                }
            	}
            	//DRAW SPRITE IF IN SCREEN
            	if(AABB.AABBIntersect(c.getAABB(),yoshi.getAABB())){
                        glDrawSprite(gl, yoshiSprite, yoshi.getX()-c.getX(), yoshi.getY()-c.getY(), yoshiSize[0],yoshiSize[1]);
            	}
            	
            
            	//always on top
            	for(int y =tileStartY;y<=tileEndY;y++){
            		for(int x=tileStartX;x<=tileEndX;x++){    
            			
            			//bushes are drawn at the lower portion of the screen hence the offset to y
	            		/*the bushes are only 3 high, and NOT the size of the camera. 
	            		 * ONLY draw if you are at 0-2 
	            		 * (this avoids a bajillion clear tiles being rendered and only renders those bush tiles)
	            		 */
            			if(y<BackgroundLayers.BGBUSHHEIGHT){
            				//got rid of parallax for now until I can implement more backgrounds better
            				//int bushesXTwoThirdYoshiCam = c.getX()+(yoshi.getX()*2/3);
            				glDrawSprite(gl,backgroundBushes[y][x].texture,(x)*tileSize[0]-c.getX(),(y+10)*tileSize[1]-c.getY(),tileSize[0],tileSize[1]);
            			}
            		}
            	}
            }
            
           
            
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
        }
        catch (IOException ex) {
            System.err.format("File: %s -- Unexpected end of file.", filename);
            return 0;
        }
    }

    public static void glDrawSprite(GL2 gl, int tex, int x, int y, int w, int h) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
        gl.glBegin(GL2.GL_QUADS);
        {
            gl.glColor3ub((byte)-1, (byte)-1, (byte)-1);
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
    
}
