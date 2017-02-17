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

public class GameLoop {
    // Set this to true to make the game loop exit.
    private static boolean shouldExit;

    // The previous frame's keyboard state.
    private static boolean kbPrevState[] = new boolean[256];

    // The current frame's keyboard state.
    private static boolean kbState[] = new boolean[256];

    // Position of the sprite and chaser.
    private static int[] spritePos = new int[] { 100, 50 };
	private static int[] chaserPos = new int[] { 10, 10 };
	
    // Texture for the sprite and chaser.
    private static int spriteTex;
    private static int chaserTex;
    
    // Size of the sprite and chaser.
    private static int[] spriteSize = new int[2];
    private static int[] chaserSize = new int[2];

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
        window.setSize(800, 600);
        window.setTitle("Gotta Catch 'Em All");
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
        gl.glViewport(0, 0, 800, 600);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, 800, 600, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
		spriteTex = glTexImageTGAFile(gl, "sprites\\magikarp.tga", spriteSize);
		chaserTex = glTexImageTGAFile(gl, "sprites\\test.tga", chaserSize);
			
        // The game loop
        long lastFrameNS;
        long curFrameNS = System.nanoTime();
        while (!shouldExit) {
            System.arraycopy(kbState, 0, kbPrevState, 0, kbState.length);
            lastFrameNS = curFrameNS;
            curFrameNS = System.nanoTime();
            long deltaTimeMS = (curFrameNS - lastFrameNS) / 1000000;
            if (deltaTimeMS == 0){deltaTimeMS = 1;}

            // Actually, this runs the entire OS message pump.
            window.display();
            
            if (!window.isVisible()) {
                shouldExit = true;
                break;
            }
			
            //TODO check if chaser is less than pos of magikarp, 
            //that means it is before, so look to the right. If it is more, look to the left.
            //maybe make an isBefore method to check then change the drawing of the chaser to orient to face the magikarp
            // Game logic goes here.
            if (kbState[KeyEvent.VK_ESCAPE]) {
                shouldExit = true;
            }
            
            //go up sprite
			if(kbState[KeyEvent.VK_W]){
            	if(spritePos[1]>0){
            		spritePos[1]-= 120 / deltaTimeMS;          	
            	}
            }
			
			//go left           
            if(kbState[KeyEvent.VK_A]){
            	if(spritePos[0]>0){
            		spritePos[0]-=  120/deltaTimeMS;
            	}
            } 
            //go down
            if(kbState[KeyEvent.VK_S]){
            	//if the sprite size is within delta of the height and window, do the things
            	if(spritePos[1]<(window.getHeight()-spriteSize[1])){
            		spritePos[1]+= 120/deltaTimeMS;
            	}
            } 
            //go right
            if(kbState[KeyEvent.VK_D]) {
            	//if delta of sprite size is within the width of the window, do the things
            	if(spritePos[0]<(window.getWidth()-spriteSize[0])){
            		spritePos[0]+= 120/deltaTimeMS;
            	}
            }
            
            	//move chaser            
				//if it is above of the sprite
            	if(chaserPos[1]>spritePos[1]) {
					chaserPos[1]-= (60 / deltaTimeMS);}
            	//if it is below the sprite
            	if(chaserPos[1]<spritePos[1]){
            		chaserPos[1]+= (60 / deltaTimeMS);
            	}
            	
    		
            
            	//if the chaser is to the left of the sprite
            	if(chaserPos[0]>spritePos[0]) {
					chaserPos[0]-= 60 / deltaTimeMS;}
            	//if the chaser is to the right of the sprite
            	if(chaserPos[0]<spritePos[0]) {
					chaserPos[0]+= 60 / deltaTimeMS;}
           
            
            //catch check
            catchCheck();
            
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

            // Draw the sprite
            glDrawSprite(gl, spriteTex, spritePos[0], spritePos[1], spriteSize[0],spriteSize[1]);
            glDrawSprite(gl, chaserTex, chaserPos[0], chaserPos[1], chaserSize[0],chaserSize[1]);
        }
    }

	private static void catchCheck() {
		if(Math.abs(chaserPos[0]-spritePos[0])<20 && Math.abs(chaserPos[1]-spritePos[1])<10){
			//it is caught
			//random values for width and height
			Random rW = new Random();
			Random rH = new Random();
			
			spritePos[0] = rW.nextInt(800-spriteSize[0]);
			spritePos[1] = rH.nextInt(600-spriteSize[1]);
			chaserPos[0] = rW.nextInt(800-chaserSize[0]);
			chaserPos[1] = rH.nextInt(600-chaserSize[1]);
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
