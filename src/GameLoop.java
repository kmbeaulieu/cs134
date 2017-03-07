import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    // Size of the sprite, this should probably be put somewhere into the Yoshi.java
    private static int[] yoshiSize = new int[2];
    private static int[] yoshiStillSize = new int[2]; //yoshi standing still
    
    //Tile size for Background
    private static int[] tileSize = new int[2];
    //world's x tiles and y tile lengths
    private static int worldXTiles = 64;
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
        gl.glViewport(0, 0, screenResX, screenResY);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glOrtho(0, screenResX, screenResY, 0, 0, 100);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

		// Game initialization goes here.
        
		//load the bajillions of background files 0-68 is for the hills, 69 is transparent
		for(int i=1;i<70;i++){
			String filename = BackgroundLayers.FTL + i + BackgroundLayers.FNE;
			glTexImageTGAFile(gl,filename,tileSize);
		}
		//70-104 is bushes, there is a 69 for transparent in this folder too
		for(int i=70;i<105;i++){
			String filename = BackgroundLayers.FBL + i + BackgroundLayers.FNE;
			glTexImageTGAFile(gl,filename,tileSize);
		}
		//load the tile formation for the backgrounds
		int bgTrees[][] = BackgroundLayers.backgroundTrees;//I did this to keep the long nasty 2d array from cluttering the game loop
		int bgBushes[][] = BackgroundLayers.backgroundBushes; 
		
		//camera setup
		Camera c = new Camera(0,0);
		
		//yoshi player
        YoshiData yoshi= new YoshiData(10,150);
        yoshiStillSprite = glTexImageTGAFile(gl, ".\\sprites\\yoshi1.tga", yoshiSize);
        yoshiStillSize = yoshiSize;
		yoshiSprite = yoshiStillSprite;
		
		//set up the animation for pressing space for yoshi (stick tongue out), the timing is staggered to make it look better
		int[] sizeForFrame1,sizeForFrame2,sizeForFrame3,sizeForFrame4,sizeForFrame5,sizeForFrame6;
		sizeForFrame1=sizeForFrame2=sizeForFrame3=sizeForFrame4=sizeForFrame5=sizeForFrame6= new int[2]; //each frame is a different size so it needs to be stored in the frame information for that current frame
		FrameDef[] yoshiSpaceFrames = new FrameDef[]{new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1),sizeForFrame1,10f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2),sizeForFrame2,20f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3,30f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4,40f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5,50f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace6.tga", sizeForFrame6), sizeForFrame6,60f), //tongue back in
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace5.tga", sizeForFrame5), sizeForFrame5,50f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace4.tga", sizeForFrame4), sizeForFrame4,40f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace3.tga", sizeForFrame3), sizeForFrame3,30f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace2.tga", sizeForFrame2), sizeForFrame2,20f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshispace\\yoshispace1.tga", sizeForFrame1), sizeForFrame1,10f)};
		yoshiSpace = new AnimationDef("yoshiSpace", yoshiSpaceFrames);
		AnimationData yoshiSpaceData = new AnimationData(yoshiSpace);
		
		//set up walking animation
		int[] walkingAnimSize = new int[2];
		FrameDef[] yoshiWalkFrames = new FrameDef[]{
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\1.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\2.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\3.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\4.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\5.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\6.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\7.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\8.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\9.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\10.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\11.tga", walkingAnimSize), walkingAnimSize,1f),
				new FrameDef(glTexImageTGAFile(gl, ".\\sprites\\yoshiwalk\\12.tga", walkingAnimSize), walkingAnimSize,1f),
		};
		yoshiWalk = new AnimationDef("yoshiwalk",yoshiWalkFrames);
		AnimationData yoshiWalkingData = new AnimationData(yoshiWalk);
		
			
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
			
           //do animation changes that happen even if a key is not pressed (ex walking is not here because it only happens when a key is pressed)
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
            	}
            	
            
            
            
            
            // Game logic goes here.
            if (kbState[KeyEvent.VK_ESCAPE]) {	
                shouldExit = true;
            }
                        
            //go left, continue animation but dont move if at the end of the screen
            if(kbState[KeyEvent.VK_A]){
            	//if not at the left edge of the world then move left
            	//if it wasnt going left before, it is now so flip. 
            	if(!yoshi.goingLeft){
            		yoshi.goingRight=false;
            		yoshi.goingLeft=true;
            		yoshiSprite = gl.glRotatef(180, 0, 1, 0);
            	}
            	if(yoshi.getX()-3>=0){
            		yoshi.setX((yoshi.getX()-3));
            	}

            	
            	yoshiWalkingData.update(deltaTimeMS);
        		yoshiSprite = yoshiWalkingData.getCurFrameImage();
        		yoshiSize = yoshiWalkingData.getcurFrameSize();
        		 
            } 
           
            //go right, continue animation even if at the end
            if(kbState[KeyEvent.VK_D]){
            	//dont move if you are at the end of the world size
            	//TODO fix the camera issues here
            	//TODO add back in the delta to make it smoother?
            	//if it wasnt going left before, it is now so flip. 
            	if(!yoshi.goingRight){
            		yoshi.goingRight=true;
            		yoshi.goingLeft=false;
            		yoshiSprite = gl.glRotatef(180, 0, 1, 0);
            	}
            	if(yoshi.getX()+3 <= (worldXTiles*tileSize[0])-yoshiSize[0] ){
            		yoshi.setX(yoshi.getX()+3);

            	}
            	//dont move yoshi but still do walk animation
            	yoshiWalkingData.update(deltaTimeMS);
        		yoshiSprite = yoshiWalkingData.getCurFrameImage();
        		yoshiSize = yoshiWalkingData.getcurFrameSize();
        		
//           
            }
            
            //camera controls
            if(kbState[KeyEvent.VK_RIGHT]){
            	
            }
            
            if(kbState[KeyEvent.VK_LEFT]){
            	/*if moving the camera left keeps it within the game world, 
            	 * then move the camera. For now move the camera at yoshi's speed
            	 */
            	if(c.getX()-3>0){
            		c.setX(c.getX()-3);
            	}
            }
            //tongue animation
            if(kbState[KeyEvent.VK_SPACE]){
            	//get the animation rolling
            	yoshi.setTongueOut(true);
            }
 
            
            gl.glClearColor(0, 0, 0, 1);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
            
            //keep yoshi in middle of the screen
         //   c.setX(yoshi.getX() - screenResX / 2);
            
            //background then yoshi then bushes as foreground
         //  startTile = BackgroundCheck(c.getX(),c.getY());
            
            //start at 0, load until you get to the window(aka camera)'s right edge and add one so it isnt loading half a tile
            for(int x=0;x<64;x++){
	            
	            	for(int y = 0; y < 15 ;y++){
	            		//draw the hills/trees
	            		//move the hills/trees back/forth at an eighth yoshi's speed and camera position	
	            		if(x<bgTrees.length && y<bgTrees[y].length){
	            			//yoshiFourthCam will move the background at an eighth of the speed of yoshi (and take care of the offset of the camera)
	            			int treesXEighthCam = c.getX()+(yoshi.getX()/8);
		                	glDrawSprite(gl,bgTrees[y][x],(x*tileSize[0])-treesXEighthCam,(y*tileSize[1])-c.getY(),tileSize[0],tileSize[1]);
	            		}
	            		
	                }
	            
           }
            // Draw yoshi
            glDrawSprite(gl, yoshiSprite, yoshi.getX(), yoshi.getY(), yoshiSize[0],yoshiSize[1]);
            //bushes are 3 tiles tall and 26 tiles across
            //TODO put this in with the camera controls
            for(int y =0;y<3;y++){
            	for(int x=0;x<26;x++){    
            		//bushes are drawn at the lower portion of the screen hence the offset to y
            		//bushes move at 1/3 speed of yoshi
            		int bushesXThirdYoshiCam = c.getX()+(yoshi.getX()/3);
                	glDrawSprite(gl,bgBushes[y][x],(x)*tileSize[0]-bushesXThirdYoshiCam,(y+10)*tileSize[1]-c.getY(),tileSize[0],tileSize[1]);

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
