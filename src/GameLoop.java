import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

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
import Background.BackgroundTrees;
import Character.YoshiData;
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
    // Size of the sprite
    private static int[] yoshiSize = new int[2];
    private static int[] yoshiStillSize = new int[2];

    
    private static final int tileNumX=40, tileNumY = 40;//tiles in the world in the x and y direction;
    
    //Texture for Background
    private static int bgID[] = new int[4];
    private static int[] tileSize = new int[2];
    private static BackgroundDef bgDef;//Background Definition 
    
    //animation stuff
    private static AnimationDef yoshiSpace;

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
        window.setSize(400, 600);
        window.setTitle("Is it there?");
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
        gl.glViewport(0, 0, 400, 600);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, 400, 600, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
		
		for(int i=1;i<70;i++){
			String filename = BackgroundTrees.FL + i + BackgroundTrees.FNE;
			glTexImageTGAFile(gl,filename,tileSize);
		}
		
		int bg[][] = BackgroundTrees.backgroundTrees;// keep the long nasty 2d away from the game loop
	
		//camera setup
		Camera c = new Camera(0,0);
		
		//yoshi player
        YoshiData yoshi= new YoshiData(10,10);
        yoshiStillSprite = glTexImageTGAFile(gl, ".\\sprites\\yoshi1.tga", yoshiSize);
        yoshiStillSize = yoshiSize;
		yoshiSprite = yoshiStillSprite;
		
		//set up the animation for pressing space for yoshi (stick tongue out)
		int[] sizeForFrame = new int[2]; //each frame is a different size so it needs to be stored in the frame information for that current frame
		FrameDef[] yoshiSpaceFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame),sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame),sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace6.tga", sizeForFrame), sizeForFrame,100f), //tongue back in
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame), sizeForFrame,100f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame), sizeForFrame,100f)};
		yoshiSpace = new AnimationDef("yoshiSpace", yoshiSpaceFrames);
		AnimationData yoshiSpaceData = new AnimationData(yoshiSpace);

			
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
			
           //do animation changes
            	if(yoshi.isTongueOut()){
            		yoshiSpaceData.update(deltaTimeMS);
                	yoshiSprite = yoshiSpaceData.getCurFrameImage();
                	yoshiSize = yoshiSpaceData.getcurFrameSize();
                	if(yoshiSpaceData.getCurFrame()==yoshiSpaceData.getMaxFrame()){
                		//you are at the max frame so stop the animation
                		yoshi.setTongueOut(false);
                		yoshiSprite = yoshiStillSprite;
                		yoshiSize = yoshiStillSize;
                	}
                	//System.out.println(yoshiSpaceData.);
//                	yoshiSize = yoshiSpaceData.getAnimDefSize(yoshiSprite);
            	}
            	
            
            
            
            
            // Game logic goes here.
            if (kbState[KeyEvent.VK_ESCAPE]) {	
                shouldExit = true;
            }
            
            //TODO keyboard movement goes here
            
            //go left
            if(kbState[KeyEvent.VK_A]){
            	if(spritePos[0]>0){
            		spritePos[0]-=  1 * deltaTimeMS;
            	}
            } 
            if(kbState[KeyEvent.VK_LEFT]){
            	int currentX = c.getX();
            	if(currentX>=0){
            		
            		c.setX(currentX-=1*deltaTimeMS);
            	}
            }
            //go right
            if(kbState[KeyEvent.VK_D]){
            	if(spritePos[0]<(window.getWidth()-yoshiSize[0])){
            		spritePos[0]+= 1*deltaTimeMS;
            	}

            }
            if(kbState[KeyEvent.VK_RIGHT]){
            	int currentX = c.getX();
            	if(currentX<window.getWidth()){
            		
            		c.setX(currentX-=5*deltaTimeMS);//go 5 tiles in the delta time? currently 5*16=80
            	}
            	System.out.println(c.getX()+", "+c.getY());
            }
            
            if(kbState[KeyEvent.VK_SPACE]){
            	//do animation!
            	yoshi.setTongueOut(true);
            }
 
            
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            
            //TODO draw background,
            for(int i = 0; i<15; i++){
            	for(int j = 0; j<32;j++){
                	glDrawSprite(gl,bg[i][j],j*16,i*16,16,16);
                }
            }
//            glDrawSprite(gl,bgHillsTex,bgHillsPos[0],bgHillsPos[1],bgHillsSize[0],bgHillsSize[1]);
//            bgHillsPos[0]+=bgHillsPos[0];
//            bgHillsPos[1]+=bgHillsPos[1];
//
//            glDrawSprite(gl,bgHillsTex,bgHillsPos[0],bgHillsPos[1],bgHillsSize[0],bgHillsSize[1]);
//d
//            glDrawSprite(gl,bgTreesTex,bgTreesPos[0],bgTreesPos[1],bgTreesSize[0],bgTreesSize[1]);
//            glDrawSprite(gl,bgBushesTex,bgBushesPos[0],bgBushesPos[1],bgBushesSize[0],bgBushesSize[1]);

            // Draw the sprite
            glDrawSprite(gl, yoshiSprite, spritePos[0], spritePos[1], yoshiSize[0],yoshiSize[1]);
            
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
