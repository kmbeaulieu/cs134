package Character;

public class YoshiData extends CharacterData {
	private boolean isTongueOut; 
	public boolean goingLeft;
	public boolean goingRight;
	
	public YoshiData(int x, int y){
		this.x = x;
		this.y = y;
		isTongueOut = false;
		
	}
	
	public boolean isTongueOut(){
		return isTongueOut;
	}

	public void setTongueOut(boolean b) {
		// TODO Auto-generated method stub
		isTongueOut = b;
	}

	public void setX(int i) {
		this.x = i;
	}
	public void setY(int i) {
		this.y = i;
	}
}
