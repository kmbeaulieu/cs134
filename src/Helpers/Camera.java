package Helpers;

public class Camera {
	private int x;
	private int y;
	private int speed;
	private AABB box;
	
	/**
	 * create the camera with the top left corner at the given x and y
	 * The speed is default at 5 (tiles per movement?)
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Camera(int x, int y){
		this.x=x;
		this.y=y;
		this.speed = 1;
		this.box = new AABB(x,y);
	}
	
	//get the value for x and y
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public AABB getAABB(){
		return box;
	}
	public int getSpeed(){
		return speed;
	}
	//set the value for x and y
	public void setX(int x) {
		this.x = x;
		this.box.updateX(this.x);
	}

	public void setY(int y) {
		this.y = y;
		this.box.updateY(this.y);
	}
	public void setSpeed(int s){
		this.speed = s;
	}
	
}
