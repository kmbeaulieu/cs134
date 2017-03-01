package Animation;

public class AnimationDef {
	String name;
	public FrameDef[] frames;	
	
	public AnimationDef(String name, FrameDef[] frames){
		this.name= name;
		this.frames=frames;
		//uncomment to see the ID number for the textures
//		for(int i=0;i<this.frames.length;i++){
//			System.out.println("THIS IS CUR FRAME BEING ADDED: " +frames[i].image);
//		}
	}
	
	
}
