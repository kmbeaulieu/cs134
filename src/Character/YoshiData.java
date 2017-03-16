package Character;

import Helpers.AABB;

public class YoshiData extends CharacterData {
	public boolean isTongueOut; 
	public boolean goingLeft;
	public boolean goingRight;
	private int speed;
	private AABB box;
	
	
	/**
	 * Make a yoshi at a certain coordinate. The default speed is 5.
	 * @param x is the x coordinate.
	 * @param y is the y coordinate. 
	 */
	public YoshiData(int x, int y){
		this.x = x;
		this.y = y;
		isTongueOut = false;
		this.speed = 3;
		this.box = new AABB(x,y);
		
	}
	
	public AABB getAABB(){
		return box;
	}
	
//	public boolean isTongueOut(){
//		return isTongueOut;
//	}
//	public boolean isGoingRight(){
//		return goingRight;
//	}
//	public boolean isGoingLeft(){
//		return goingLeft;
//	}

	public void setTongueOut(boolean b) {
		// TODO Auto-generated method stub
		isTongueOut = b;
	}

	public void setX(int i) {
		this.x = i;
		this.box.updateX(i);
	}
	
	public void setY(int i) {
		this.y = i;
		this.box.updateY(i);
	}
	
	/**
	 * move yoshi to the right, also update its left/right status
	 */
	public void moveYoshiRight(){
		this.x = this.x + speed; 
		this.box.updateX(this.x);
		this.goingRight = true;
		this.goingLeft = false;
	}
	

	/**
	 * move yoshi to the left, also update its left/right status
	 */
	public void moveYoshiLeft(){
		this.x= this.x - speed;
		this.box.updateX(this.x);
		this.goingRight = false;
		this.goingLeft = true;
	}

	public int getSpeed() {
		return speed;
	}
}
