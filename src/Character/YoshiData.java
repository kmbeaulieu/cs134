package Character;

public class YoshiData extends CharacterData {
	private boolean isTongueOut; 
	public boolean goingLeft;
	public boolean goingRight;
	private int speed;
	
	/**
	 * Make a yoshi at a certain coordinate. The default speed is 3.
	 * @param x is the x coordinate.
	 * @param y is the y coordinate. 
	 */
	public YoshiData(int x, int y){
		this.x = x;
		this.y = y;
		isTongueOut = false;
		this.speed = 3;
		
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
	
	public void moveYoshiRight(){
		this.x = this.x + speed; 
	}
	public void moveYoshiLeft(){
		this.x= this.x - speed;
	}

	public int getSpeed() {
		return speed;
	}
}
