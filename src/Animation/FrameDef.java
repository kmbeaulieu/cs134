package Animation;

public class FrameDef {
	public int image;
	public float frameTimeSecs;
	public int[] frameSize;
	
	//for an animation
	
	public FrameDef(int image, int[] frameSize, float frameTimeSecs){
		this.image = image;
		this.frameTimeSecs = frameTimeSecs;
		this.frameSize = frameSize;
	}
	
}	
