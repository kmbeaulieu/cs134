package Animation;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import Helpers.GameLoop;
 
public class AnimationData {
 public AnimationDef def;
 int curFrame; //the current frame the animation is on
 int maxFrame; //the max frame of the animation
 public float secsUntilNextFrame; //seconds until next frame, to keep it even through out the anim sequence
 
 //make a new instance of animation data
 public AnimationData(AnimationDef def){
	 this.def = def;
	 this.curFrame = 0;
	 this.maxFrame = this.def.frames.length-1;
	 this.secsUntilNextFrame = def.frames[curFrame].frameTimeSecs;
 }
 
 
 public void update(float deltaTime){
	 //update the seconds until next frame
	 secsUntilNextFrame -=  deltaTime; 
	
	 if(curFrame>=maxFrame){
		 curFrame=0;//reset the frames
                 secsUntilNextFrame = def.frames[curFrame].frameTimeSecs;
	 }
	
	 //if it is time for the next frame
	 if(secsUntilNextFrame<=0 && (curFrame<maxFrame+1)){
		 curFrame++;
		 secsUntilNextFrame = def.frames[curFrame].frameTimeSecs;
		
	 }
 }
 
// public void draw(int x, int y){
//	 int curTexIndex = def.frames[curFrame].image;
//	 int tex = getCurFrameImage(); 
//	 int[] texSize = getcurFrameSize();
//	 
//	 //GameLoop.glTexImageTGAFile(gl, tex, texSize, x, y);
//	 
// }
 /**
  * 
  * @return the current frame in the animation
  */
 public int getCurFrame(){
	 return curFrame;
 }
 /**
  * 
  * @return the TEXTURE image number for the current frame's animation
  */
 public int getCurFrameImage(){
	 return def.frames[curFrame].image;
 }
 
 public void resetAnimation(){
     curFrame = 0;
     secsUntilNextFrame = def.frames[curFrame].frameTimeSecs;
     
 }
 public int[] getcurFrameSize(){
	 //returns the frame size of a certain frame since not all sizes are the same.
	 return def.frames[curFrame].frameSize;
 }
 //get the frame of the animation
//for each frame, draw
	// for(int i = 1; i<=def.frames.length;i++){
	//	 if(curFrame==i){return def.frames[i].image}
	// }

public int getMaxFrame() {
	return maxFrame;
}
}
