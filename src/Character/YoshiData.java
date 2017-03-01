package Character;

public class YoshiData extends CharacterData {
	private boolean isTongueOut; 
	
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
	
}
