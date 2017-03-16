package Animation;
public class AnimationData {
 AnimationDef def;
 int curFrame; //the current frame the animation is on
 int maxFrame; //the max frame of the animation
 float secsUntilNextFrame; //seconds until next frame, to keep it even through out the anim sequence
 
 //make a new instance of animation data
 public AnimationData(AnimationDef def){
	 this.def = def;
	 this.curFrame = 1;
	 this.maxFrame = this.def.frames.length;
	 this.secsUntilNextFrame = def.frames[curFrame].frameTimeSecs;
 }
 
 
 public void update(float deltaTime){
	 //update the seconds until next frame
	 secsUntilNextFrame -=  deltaTime; 
	
	 if(curFrame>=def.frames.length){
		 curFrame=1;//reset the frames
	 }
	
	 //if it is time for the next frame
	 if(secsUntilNextFrame<=0){
		 curFrame++;
		 secsUntilNextFrame = def.frames[curFrame-1].frameTimeSecs;
		
	 }
 }
 
 public void draw(int x, int y){
	 
 }
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
	 return def.frames[curFrame-1].image;
 }
 
 
 public int[] getcurFrameSize(){
	 //returns the frame size of a certain frame since not all sizes are the same.
	 return def.frames[curFrame-1].frameSize;
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
