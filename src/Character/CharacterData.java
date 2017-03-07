package Character;
import Animation.AnimationData;

public class CharacterData {
	int x;
	int y;
	boolean isWalking;
	AnimationData currAnimation;
	
	void update(float deltaTime){
		
	}
	
	void draw(){
		
	}
	
	//for the game loop to use
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int addToX(int i){
		return (x + i);
	}
	public int addToY(int i){
		return (y+i);
	}
	public boolean isWalking(){
		return isWalking;
	}
	
	
}
