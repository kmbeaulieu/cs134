package Helpers;

public class Camera {
	private int x;
	private int y;
	private int speed;
	
	/**
	 * create the camera with the top left corner at the given x and y
	 * The speed is default at 5 (tiles per movement?)
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Camera(int x, int y){
		this.x=x;
		this.y=y;
		this.speed = 5;
	}
	
	//get the value for x and y
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getSpeed(){
		return speed;
	}
	//set the value for x and y
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	public void setSpeed(int s){
		this.speed = s;
	}
	
}
